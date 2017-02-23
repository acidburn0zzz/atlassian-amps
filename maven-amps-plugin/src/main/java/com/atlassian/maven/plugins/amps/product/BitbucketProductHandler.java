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
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.atlassian.maven.plugins.amps.util.ProjectUtils.firstNotNull;

/**
 * @since 6.1.0
 */
public class BitbucketProductHandler extends AbstractProductHandler {

    private static final String ADMIN_OBJECT_NAME = "org.springframework.boot:type=Admin,name=SpringApplication";
    // Note: Versions require an "-a0" qualifier so that milestone, rc and snapshot releases are evaluated as
    // being 'later' (see org.apache.maven.artifact.versioning.ComparableVersion.StringItem)
    private static final DefaultArtifactVersion FIRST_SEARCH_VERSION = new DefaultArtifactVersion("4.6.0-a0");
    private static final DefaultArtifactVersion FIRST_SPRING_BOOT_VERSION = new DefaultArtifactVersion("5.0.0-a0");
    private static final String JMX_PORT = "7991";
    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://127.0.0.1:" + JMX_PORT + "/jmxrmi";
    private static final String SEARCH_GROUP_ID = "com.atlassian.bitbucket.search";
    private static final String SERVER_GROUP_ID = "com.atlassian.bitbucket.server";

    private final MavenProjectLoader projectLoader;
    private final JavaTaskFactory taskFactory;

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
        String baseUrl = MavenGoals.getBaseUrl(ctx, ctx.getHttpPort());

        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder()
                .put("baseurl", baseUrl)
                .put("baseurl.display", baseUrl)
                .put("bitbucket.home", fixSlashes(getHomeDirectory(ctx).getPath()))
                .put("johnson.spring.lifecycle.synchronousStartup", Boolean.TRUE.toString());
        if (isSpringBoot(ctx)) {
            //For 5.0.0+, setup Spring Boot. In order to shut the application down after it's
            //started we need to enable JMX
            builder.put("com.sun.management.jmxremote.authenticate", Boolean.FALSE.toString())
                    .put("com.sun.management.jmxremote.port", JMX_PORT)
                    .put("com.sun.management.jmxremote.ssl", Boolean.FALSE.toString());
        }

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
            boolean connected = false;

            try (JMXConnector connector = createConnector()) {
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
            // For Spring Boot, use maven-dependency-plugin to unpack the war file, rather than
            // copying it over
            File bootDir = new File(baseDir, "app");
            goals.unpackWebappWar(bootDir, artifact);

            return bootDir;
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
            AntJavaExecutorThread javaThread = startJavaThread(ctx, app, properties);

            return waitUntilReady(ctx, javaThread);
        }

        // For Bitbucket Server 4.x, deploy the webapp to Tomcat using Cargo
        return goals.startWebapp(ctx.getInstanceId(), app, properties, Collections.emptyList(), ctx);
    }

    @Override
    protected boolean supportsStaticPlugins() {
        return true;
    }

    private static String fixSlashes(String path) {
        return path.replaceAll("\\\\", "/");
    }

    private static boolean isSpringBoot(Product ctx) {
        return new DefaultArtifactVersion(ctx.getVersion()).compareTo(FIRST_SPRING_BOOT_VERSION) >= 0;
    }

    private JMXConnector createConnector() throws IOException {
        JMXServiceURL serviceURL = new JMXServiceURL(JMX_URL);

        return JMXConnectorFactory.connect(serviceURL);
    }

    private AntJavaExecutorThread startJavaThread(Product ctx, File app, Map<String, String> properties) {
        Java java = taskFactory.newJavaTask(JavaTaskFactory.output(ctx.getOutput()).systemProperties(properties));

        // Set the unpacked application directory as the classpath. This will allow Java to find the
        // WarLauncher, which in turn will use the manifest to assemble the real classpath
        java.createClasspath()
                .createPathElement().setLocation(app);

        java.createJvmarg().setLine(ctx.getJvmArgs());
        java.createJvmarg().setLine(ctx.getDebugArgs()); // If debug args aren't set, nothing happens

        // Set the context path and port based on the product configuration
        java.createArg().setValue("--server.contextPath=" + ctx.getContextPath());
        java.createArg().setValue("--server.port=" + ctx.getHttpPort());
        // Enable Spring Boot's admin JMX endpoints, which can be used to wait for the application
        // to start and to shut it down gracefully later
        java.createArg().setValue("--spring.application.admin.enabled=true");
        java.createArg().setValue("--spring.application.admin.jmx-name=" + ADMIN_OBJECT_NAME);

        java.setClassname("org.springframework.boot.loader.WarLauncher");

        AntJavaExecutorThread javaThread = new AntJavaExecutorThread(java);
        javaThread.start();

        return javaThread;
    }

    private int waitUntilReady(Product ctx, AntJavaExecutorThread javaThread) throws MojoExecutionException {
        long timeout = System.currentTimeMillis() + ctx.getStartupTimeout();
        while (System.currentTimeMillis() < timeout) {
            if (javaThread.isFinished()) {
                throw new MojoExecutionException("Bitbucket Server failed to start", javaThread.getBuildException());
            }

            try (JMXConnector connector = createConnector()) {
                MBeanServerConnection connection = connector.getMBeanServerConnection();

                Boolean ready = (Boolean) connection.getAttribute(new ObjectName(ADMIN_OBJECT_NAME), "Ready");
                if (Boolean.TRUE.equals(ready)) {
                    return ctx.getHttpPort();
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
