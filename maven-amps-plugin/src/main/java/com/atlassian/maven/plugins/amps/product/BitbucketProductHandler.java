package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.JvmArgsFix;
import com.atlassian.maven.plugins.amps.util.MavenProjectLoader;
import com.atlassian.maven.plugins.amps.util.ant.AntJavaExecutorThread;
import com.atlassian.maven.plugins.amps.util.ant.JavaTaskFactory;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.taskdefs.Java;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.atlassian.maven.plugins.amps.util.ProductHandlerUtil.pickFreePort;
import static com.atlassian.maven.plugins.amps.util.ProjectUtils.firstNotNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * @since 6.1.0
 */
public class BitbucketProductHandler extends AbstractProductHandler {

    private static final String ADMIN_OBJECT_NAME = "org.springframework.boot:type=Admin,name=SpringApplication";
    private static final int DEFAULT_JMX_PORT = 7995;
    // Note: Versions require an "-a0" qualifier so that milestone, rc and snapshot releases are evaluated as
    // being 'later' (see org.apache.maven.artifact.versioning.ComparableVersion.StringItem)
    private static final DefaultArtifactVersion FIRST_SEARCH_VERSION = new DefaultArtifactVersion("4.6.0-a0");
    private static final DefaultArtifactVersion FIRST_SPRING_BOOT_VERSION = new DefaultArtifactVersion("5.0.0-a0");
    private static final String JMX_PORT_FILE = "jmx-port";
    private static final String JMX_URL_FORMAT = "service:jmx:rmi:///jndi/rmi://127.0.0.1:%1$d/jmxrmi";
    private static final String SEARCH_GROUP_ID = "com.atlassian.bitbucket.search";
    private static final String SERVER_GROUP_ID = "com.atlassian.bitbucket.server";

    private final MavenProjectLoader projectLoader;
    private final JavaTaskFactory taskFactory;

    @SuppressWarnings("deprecation")
    public BitbucketProductHandler(MavenContext context, MavenGoals goals, ArtifactFactory artifactFactory,
                                   MavenProjectLoader projectLoader) {
        super(context, goals, new BitbucketPluginProvider(), artifactFactory);

        this.projectLoader = projectLoader;

        taskFactory = new JavaTaskFactory(log);
    }

    @Override
    public void cleanupProductHomeForZip(Product product, File snapshotDir) throws MojoExecutionException, IOException {
        super.cleanupProductHomeForZip(product, snapshotDir);

        FileUtils.deleteQuietly(new File(snapshotDir, "log/atlassian-bitbucket.log"));
        FileUtils.deleteQuietly(new File(snapshotDir, ".osgi-cache"));
    }

    @Override
    public String getId() {
        return ProductHandlerFactory.BITBUCKET;
    }

    @Override
    public List<ProductArtifact> getAdditionalPlugins(Product ctx) throws MojoExecutionException {
        ArrayList<ProductArtifact> additionalPlugins = new ArrayList<>();

        // Add the embedded elasticsearch plugin
        if (new DefaultArtifactVersion(ctx.getVersion()).compareTo(FIRST_SEARCH_VERSION) >= 0) {
            // The version of search distribution should be the same as the search plugin.
            projectLoader.loadMavenProject(context.getExecutionEnvironment().getMavenSession(), context.getProject(),
                        artifactFactory.createParentArtifact(SERVER_GROUP_ID, "bitbucket-parent", ctx.getVersion()))
                    .flatMap(mavenProject -> Optional.ofNullable(mavenProject.getDependencyManagement())
                            .flatMap(dependencyManager -> dependencyManager.getDependencies().stream()
                                    .filter(dep -> dep.getGroupId().equals(SEARCH_GROUP_ID))
                                    .findFirst()
                                    .flatMap(dependency -> Optional.ofNullable(dependency.getVersion()))))
                    .ifPresent(version -> additionalPlugins.add(new ProductArtifact(SEARCH_GROUP_ID,
                            "embedded-elasticsearch-plugin", version)));
        }

        return additionalPlugins;
    }

    @Override
    public ProductArtifact getArtifact() {
        return new ProductArtifact(SERVER_GROUP_ID, "bitbucket-webapp");
    }

    @Override
    public File getBundledPluginPath(Product ctx, File appDir) {
        // Starting from 4.8, bundled plugins are no longer a zip file. Instead, they're unpacked in the
        // webapp itself. This way, first run doesn't need to pay the I/O cost to unpack them
        File bundledPluginsDir = new File(appDir, "WEB-INF/atlassian-bundled-plugins");
        if (bundledPluginsDir.isDirectory()) {
            return bundledPluginsDir;
        }

        // If the atlassian-bundled-plugins directory doesn't exist, assume we're using an older version
        // of Bitbucket Server where bundled plugins are still zipped up
        return new File(appDir, "WEB-INF/classes/bitbucket-bundled-plugins.zip");
    }

    @Override
    public List<ProductArtifact> getDefaultBundledPlugins() {
        return Collections.emptyList();
    }

    @Override
    public String getDefaultContainerId() {
        return "tomcat8x";
    }

    @Override
    public int getDefaultHttpPort() {
        return 7990;
    }

    @Override
    public int getDefaultHttpsPort() {
        return 8447;
    }

    @Override
    public List<ProductArtifact> getDefaultLibPlugins() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getSystemProperties(Product ctx) {
        //Previously, this used MavenGoals.getBaseURL(ctx, ctx.getHttpPort()) which, with those arguments, should
        //generate the same base URL as the new Product.getBaseUrL() method will _unless HTTPS is enabled_. Unlike
        //MavenGoals.getBaseURL, Product.getBaseUrl takes whether HTTPS is enabled into consideration; MavenGoals
        //always builds an HTTP URL.
        //For 4.x, which uses MavenGoals.startWebapp, it's possible the instance will come up on a different port,
        //if the configured port is already in use.
        //For 5+, which is built using Spring Boot, the instance will either come up on the configured port or, if
        //it's already in use, will fail to start.
        String baseUrl = ctx.getBaseUrl();

        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder()
                .put("baseurl", baseUrl)
                .put("baseurl.display", baseUrl)
                .put("bitbucket.home", fixSlashes(getHomeDirectory(ctx).getPath()))
                .put("johnson.spring.lifecycle.synchronousStartup", Boolean.TRUE.toString());

        return builder.build();
    }

    @Override
    public ProductArtifact getTestResourcesArtifact() {
        return new ProductArtifact(SERVER_GROUP_ID, "bitbucket-it-resources");
    }

    @Override
    public File getUserInstalledPluginsDirectory(Product product, File webappDir, File homeDir) {
        File baseDir = homeDir;

        File sharedHomeDir = new File(homeDir, "shared");
        if(sharedHomeDir.exists()) {
            baseDir = sharedHomeDir;
        }

        return new File(new File(baseDir, "plugins"), "installed-plugins");
    }

    @Override
    public void stop(Product ctx) throws MojoExecutionException {
        if (isSpringBoot(ctx)) {
            int jmxPort = readJmxPort(ctx);

            boolean connected = false;
            try (JMXConnector connector = createConnector(jmxPort)) {
                MBeanServerConnection connection = connector.getMBeanServerConnection();
                connected = true; // Connected successfully, so an exception most likely means success

                connection.invoke(new ObjectName(ADMIN_OBJECT_NAME), "shutdown", null, null);
            } catch (InstanceNotFoundException e) {
                throw new MojoExecutionException("Spring Boot administration is not available; " +
                        "Bitbucket Server will need to be stopped manually", e);
            } catch (Exception e) {
                // Invoking shutdown will never receive a response because the application stops as part
                // of processing the call. The "connected" flag is used to differentiate errors
                if (connected) {
                    log.debug("Bitbucket Server has stopped");
                } else {
                    log.warn("There was an error attempting to stop Bitbucket Server", e);
                }
            }
        } else {
            goals.stopWebapp(ctx.getInstanceId(), ctx.getContainerId(), ctx);
        }
    }

    @Override
    protected File extractApplication(Product ctx, File homeDir) throws MojoExecutionException {
        ProductArtifact defaults = getArtifact();
        ProductArtifact artifact = new ProductArtifact(
                firstNotNull(ctx.getGroupId(), defaults.getGroupId()),
                firstNotNull(ctx.getArtifactId(), defaults.getArtifactId()),
                firstNotNull(ctx.getVersion(), defaults.getVersion()));

        //check for a stable version if needed
        if (Artifact.RELEASE_VERSION.equals(artifact.getVersion()) ||
                Artifact.LATEST_VERSION.equals(artifact.getVersion())) {
            log.info("Determining latest stable product version...");
            Artifact warArtifact = artifactFactory.createProjectArtifact(artifact.getGroupId(),
                    artifact.getArtifactId(), artifact.getVersion());
            String stableVersion = ctx.getArtifactRetriever().getLatestStableVersion(warArtifact);

            log.info("Using latest stable product version: " + stableVersion);
            artifact.setVersion(stableVersion);
            ctx.setVersion(stableVersion);
        }

        File baseDir = getBaseDirectory(ctx);
        if (isSpringBoot(ctx)) {
            // For Spring Boot, use maven-dependency-plugin to unpack the war file, rather than copying it over
            File appDir = new File(baseDir, "app");
            goals.unpackWebappWar(appDir, artifact);

            return appDir;
        }

        return goals.copyWebappWar(ctx.getId(), baseDir, artifact);
    }

    @Override
    protected void fixJvmArgs(Product ctx) {
        // Don't use JvmArgsFix.defaults(); it applies -XX:MaxPermSize which just triggers warnings
        // on Java 8 (which is the only Java version Bitbucket Server ever allowed)
        final String jvmArgs = JvmArgsFix.empty()
                .with("-Xmx", "1g") // Use a 1g max heap by default instead of 512m
                .apply(ctx.getJvmArgs());
        ctx.setJvmArgs(jvmArgs);
    }

    @Override
    protected int startApplication(Product ctx, File app, File homeDir, Map<String, String> properties)
            throws MojoExecutionException {
        if (isSpringBoot(ctx)) {
            int connectorPort = ctx.isHttps() ? ctx.getHttpsPort() : ctx.getHttpPort();
            int jmxPort = pickJmxPort(ctx, connectorPort);

            AntJavaExecutorThread javaThread = startJavaThread(ctx, app, addJmxProperties(properties, jmxPort));
            waitUntilReady(javaThread, jmxPort, ctx.getStartupTimeout());

            return connectorPort;
        }

        // For Bitbucket Server 4.x, deploy the webapp to Tomcat using Cargo
        return goals.startWebapp(ctx.getInstanceId(), app, properties, Collections.emptyList(), Collections.emptyList(), ctx);
    }

    @Override
    protected boolean supportsStaticPlugins() {
        return true;
    }

    private static Map<String, String> addJmxProperties(Map<String, String> properties, int jmxPort) {
        Map<String, String> updatedProperties = new HashMap<>(properties);
        updatedProperties.put("com.sun.management.jmxremote.authenticate", "false");
        updatedProperties.put("com.sun.management.jmxremote.port", String.valueOf(jmxPort));
        updatedProperties.put("com.sun.management.jmxremote.ssl", "false");
        // On Java 8u102 and newer, setting jmxremote.host will cause JMX to only listen on localhost. In
        // addition, rmi.server.hostname is explicitly set to localhost to match. Without both connecting
        // to JMX via TCP/IP fails in some environments.
        updatedProperties.put("com.sun.management.jmxremote.host", "127.0.0.1");
        updatedProperties.put("java.rmi.server.hostname", "127.0.0.1");

        return updatedProperties;
    }

    private static String fixSlashes(String path) {
        return path.replaceAll("\\\\", "/");
    }

    /**
     * Gets the local loopback address.
     * <p>
     * This method exists because {@link InetAddress#getLoopbackAddress()} may return an IPv6-style loopback address
     * on systems which support IPv6. Since {@link #addJmxProperties} explicitly configures RMI to run on 127.0.0.1,
     * and {@link #JMX_URL_FORMAT} is hard-coded for the same, we always want an IPv4-style address. If that address
     * fails for any reason, we fall back on {@link InetAddress#getLoopbackAddress()}.
     *
     * @return the loopback address
     * @since 6.3.4
     */
    private static InetAddress getLoopbackAddress() {
        try {
            return InetAddress.getByAddress("localhost", new byte[]{0x7f,0x00,0x00,0x01});
        } catch (UnknownHostException e) {
            return InetAddress.getLoopbackAddress();
        }
    }

    private static boolean isSpringBoot(Product ctx) {
        return new DefaultArtifactVersion(ctx.getVersion()).compareTo(FIRST_SPRING_BOOT_VERSION) >= 0;
    }

    /**
     * Normalizes Tomcat's range of supported {@code CertificateVerification} settings to their Spring Boot
     * {@code Ssl} equivalents.
     * <p>
     * Note: Spring Boot's {@code ClientAuth} enumeration does not support Tomcat's {@code OPTIONAL_NO_CA}
     * setting. If that is the requested value, client auth will not be enabled.
     *
     * @param value the Tomcat value to map
     * @return the mapped Spring Boot value, which may be {@code empty()} if client auth should not be configured
     */
    private static Optional<String> normalizeClientAuth(String value) {
        switch (defaultString(value)) {
            case "need": // Tomcat doesn't support this. It's allowed because Spring Boot does
            case "require":
            case "required":
            case "true":
            case "yes":
                return of("need");
            case "optional":
            case "want":
                return of("want");
            default:
                return empty();
        }
    }

    private JMXConnector createConnector(int jmxPort) throws IOException {
        JMXServiceURL serviceURL = new JMXServiceURL(String.format(JMX_URL_FORMAT, jmxPort));

        return JMXConnectorFactory.connect(serviceURL);
    }

    private int pickJmxPort(Product ctx, int connectorPort) throws MojoExecutionException {
        // If the configured HTTP port is the default JMX port, skip the default and select a random port.
        // Checking if the port is available will likely succeed, but startup would still fail because JMX
        // would take the port before the HTTP connector was opened
        int jmxPort = pickFreePort(DEFAULT_JMX_PORT == connectorPort ? 0 : DEFAULT_JMX_PORT, getLoopbackAddress());
        if (jmxPort != DEFAULT_JMX_PORT) {
            // If the default JMX port wasn't available, write the randomly-selected port to a file in the
            // product's base directory. This makes it available later when the product is stopped
            Path jmxFile = getBaseDirectory(ctx).toPath().resolve(JMX_PORT_FILE);

            try {
                Files.write(jmxFile, String.valueOf(jmxPort).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                // If the port file cannot be written, it won't be possible to shut the product down later
                // if it's started, so fail instead
                throw new MojoExecutionException("JMX port " + DEFAULT_JMX_PORT + " is not available, and the " +
                        "automatically-selected replacement could not be written to " + jmxFile.toAbsolutePath(), e);
            }
        }

        return jmxPort;
    }

    private int readJmxPort(Product ctx) throws MojoExecutionException {
        Path jmxFile = getBaseDirectory(ctx).toPath().resolve(JMX_PORT_FILE);

        try (BufferedReader reader = Files.newBufferedReader(jmxFile, StandardCharsets.UTF_8)) {
            return Integer.parseInt(reader.readLine());
        } catch (FileNotFoundException | NoSuchFileException e) {
            // If the JMX port was not written to a file, assume the default is in use
            return DEFAULT_JMX_PORT;
        } catch (IOException e) {
            throw new MojoExecutionException("The JMX port could not be read from " + jmxFile.toAbsolutePath(), e);
        } catch (NumberFormatException e) {
            throw new MojoExecutionException("The JMX port in " + jmxFile.toAbsolutePath() + " is not valid", e);
        }
    }

    private AntJavaExecutorThread startJavaThread(Product ctx, File app, Map<String, String> properties) {
        Java java = taskFactory.newJavaTask(JavaTaskFactory.output(ctx.getOutput()).systemProperties(properties));

        // Set the unpacked application directory as the classpath. This will allow Java to find the
        // WarLauncher, which in turn will use the manifest to assemble the real classpath
        java.createClasspath().createPathElement().setLocation(app);

        java.createJvmarg().setLine(ctx.getJvmArgs());
        java.createJvmarg().setLine(ctx.getDebugArgs()); // If debug args aren't set, nothing happens

        if (ctx.isHttps()) {
            java.createArg().setValue("--server.port=" + ctx.getHttpsPort());

            // If HTTPS was enabled, in addition to setting the port SSL also needs to be configured
            java.createArg().setValue("--server.ssl.enabled=true");
            java.createArg().setValue("--server.ssl.key-alias=" + ctx.getHttpsKeyAlias());
            java.createArg().setValue("--server.ssl.key-password=" + ctx.getHttpsKeystorePass());
            java.createArg().setValue("--server.ssl.key-store=" + ctx.getHttpsKeystoreFile());
            java.createArg().setValue("--server.ssl.key-store-password=" + ctx.getHttpsKeystorePass());
            java.createArg().setValue("--server.ssl.protocol=" + ctx.getHttpsSSLProtocol());

            // Client auth requires special handling, to map from the various values Tomcat accepts to
            // their equivalent Spring Boot value. Tomcat's native values aren't supported
            normalizeClientAuth(ctx.getHttpsClientAuth())
                    .ifPresent(clientAuth -> java.createArg().setValue("--server.ssl.client-auth=" + clientAuth));
        } else {
            // Otherwise, for HTTP, just set the port
            java.createArg().setValue("--server.port=" + ctx.getHttpPort());
        }
        // Set the context path for the application
        java.createArg().setValue("--server.contextPath=" + ctx.getContextPath());
        // Enable Spring Boot's admin JMX endpoints, which can be used to wait for the application
        // to start and to shut it down gracefully later
        java.createArg().setValue("--spring.application.admin.enabled=true");
        java.createArg().setValue("--spring.application.admin.jmx-name=" + ADMIN_OBJECT_NAME);

        java.setClassname("org.springframework.boot.loader.WarLauncher");

        AntJavaExecutorThread javaThread = new AntJavaExecutorThread(java);
        javaThread.start();

        return javaThread;
    }

    private void waitUntilReady(AntJavaExecutorThread javaThread, int jmxPort, int wait) throws MojoExecutionException {
        long timeout = System.currentTimeMillis() + wait;
        while (System.currentTimeMillis() < timeout) {
            if (javaThread.isFinished()) {
                throw new MojoExecutionException("Bitbucket Server failed to start", javaThread.getBuildException());
            }

            try (JMXConnector connector = createConnector(jmxPort)) {
                MBeanServerConnection connection = connector.getMBeanServerConnection();

                Boolean ready = (Boolean) connection.getAttribute(new ObjectName(ADMIN_OBJECT_NAME), "Ready");
                if (Boolean.TRUE.equals(ready)) {
                    return;
                }
            } catch (AttributeNotFoundException e) {
                // Unexpected change to Spring Boot's "Admin" MXBean?
                throw new MojoExecutionException(ADMIN_OBJECT_NAME + " has no \"Ready\" attribute", e);
            } catch (InstanceNotFoundException e) {
                log.debug("Spring Boot administration for Bitbucket Server is not available yet");
            } catch (ReflectionException e) {
                throw new MojoExecutionException("Failed to retrieve \"Ready\" attribute", e);
            } catch (IOException e) {
                boolean rethrow = true;

                Throwable t = e;
                while (t != null) {
                    if (t instanceof ConnectException) {
                        log.debug("Bitbucket Server's MBeanServer is not available yet");

                        rethrow = false;
                        t = null;
                    } else {
                        t = t.getCause();
                    }
                }

                if (rethrow) {
                    throw new MojoExecutionException("Could not be connect to Bitbucket Server via JMX", e);
                }
            } catch (Exception e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }

            try {
                log.debug("Waiting to retry");
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new IllegalStateException("Interrupted while waiting for Bitbucket Server to start");
            }
        }

        // If we make it here, startup timed out. Try to interrupt the process's thread, to trigger the
        // application to be shutdown, and then throw
        javaThread.interrupt();

        throw new MojoExecutionException("Timed out waiting for Bitbucket Server to start");
    }

    private static class BitbucketPluginProvider extends AbstractPluginProvider {

        @Override
        protected Collection<ProductArtifact> getSalArtifacts(String salVersion) {
            return Collections.emptyList();
        }
    }
}
