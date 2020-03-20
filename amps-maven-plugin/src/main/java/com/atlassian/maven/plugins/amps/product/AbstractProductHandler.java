package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.AbstractProductHandlerMojo;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils;
import com.atlassian.maven.plugins.amps.util.FileUtils;
import com.atlassian.maven.plugins.amps.util.JvmArgsFix;
import com.atlassian.maven.plugins.amps.util.ZipUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.JIRA;
import static com.atlassian.maven.plugins.amps.util.FileUtils.doesFileNameMatchArtifact;
import static com.atlassian.maven.plugins.amps.util.ProjectUtils.createDirectory;
import static com.atlassian.maven.plugins.amps.util.ZipUtils.unzip;
import static java.lang.String.join;
import static java.util.Collections.emptyList;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.iterateFiles;
import static org.apache.commons.io.FileUtils.moveDirectory;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class AbstractProductHandler extends AmpsProductHandler
{
    private static final Map<String, Map<String, GroupArtifactPair>> APPLICATION_KEYS =
            ImmutableMap.of(
                    JIRA, ImmutableMap.of(
                            "jira-software", new GroupArtifactPair("com.atlassian.jira", "jira-software-application"),
                            "jira-servicedesk", new GroupArtifactPair("com.atlassian.servicedesk", "jira-servicedesk-application")));

    private final PluginProvider pluginProvider;
    private final ApplicationMapper applicationMapper;

    protected AbstractProductHandler(MavenContext context, MavenGoals goals, PluginProvider pluginProvider, ArtifactFactory artifactFactory)
    {
        super(context, goals, artifactFactory);
        this.pluginProvider = pluginProvider;
        this.applicationMapper = new ApplicationMapper(APPLICATION_KEYS);
    }

    /**
     * Extracts the product and its home, prepares both and starts the product
     * @return the port
     */
    public final int start(final Product ctx) throws MojoExecutionException
    {
        final File homeDir = extractAndProcessHomeDirectory(ctx);

        final File extractedApp = extractApplication(ctx, homeDir);

        final File finalApp = addArtifactsAndOverrides(ctx, homeDir, extractedApp);

        addOverridesFromProductPom(ctx);

        // Ask for the system properties (from the ProductHandler and from the pom.xml)
        Map<String, String> systemProperties = mergeSystemProperties(ctx);

        return startApplication(ctx, finalApp, homeDir, systemProperties);
    }

    /**
     * Override product context with properties inherited from product pom
     *
     * @param ctx product context
     * @throws MojoExecutionException throw during creating effective pom
     */
    protected void addOverridesFromProductPom(Product ctx) throws MojoExecutionException {
    }

    @Override
    public String getDefaultContainerId(final Product product) throws MojoExecutionException
    {
        return ProductContainerVersionMapper.containerForProductVersion(getId(), resolveVersion(product));
    }

    private String resolveVersion(final Product product) throws MojoExecutionException
    {
        String version = product.getVersion();
        if (isBlank(version))
        {
            version = Artifact.RELEASE_VERSION;
        }
        if(Artifact.RELEASE_VERSION.equals(version) || Artifact.LATEST_VERSION.equals(version))
        {
            ProductArtifact productArtifact = getArtifact();
            Artifact warArtifact = artifactFactory.createProjectArtifact(productArtifact.getGroupId(), productArtifact.getArtifactId(), version);
            version = product.getArtifactRetriever().getLatestStableVersion(warArtifact);
        }
        return version;
    }

    protected final File extractAndProcessHomeDirectory(final Product ctx) throws MojoExecutionException
    {
        final File homeDir = getHomeDirectory(ctx);

        // Check if home directory was provided by the user
        if (StringUtils.isNotBlank(ctx.getDataHome()))
        {
            // Don't modify the home. Just use it.
            return homeDir;
        }

        // Create a home dir for the product in target
        final File productHomeData = getProductHomeData(ctx);
        if (productHomeData != null)
        {

            // Only create the home dir if it doesn't exist
            if (!homeDir.exists())
            { // IT CREATES hOME?
                extractProductHomeData(productHomeData, homeDir, ctx);

                // just in case
                homeDir.mkdir();
                processHomeDirectory(ctx, homeDir);
            }

            // Always override files regardless of home directory existing or not
            overrideAndPatchHomeDir(homeDir, ctx);
        }
        return homeDir;
    }

    protected void extractProductHomeData(File productHomeData, File homeDir, Product ctx)
            throws MojoExecutionException
    {
        final File tmpDir = new File(getBaseDirectory(ctx), "tmp-resources");
        tmpDir.mkdir();

        try
        {
            if (productHomeData.isFile())
            {
                File tmp = new File(getBaseDirectory(ctx), ctx.getId() + "-home");

                unzip(productHomeData, tmpDir.getPath());

                File rootDir = getRootDir(tmpDir, ctx);

                FileUtils.copyDirectory(rootDir, getBaseDirectory(ctx), true);

                moveDirectory(tmp, homeDir);
            }
            else if (productHomeData.isDirectory())
            {
                FileUtils.copyDirectory(productHomeData, homeDir, true);
            }
        }
        catch (final IOException ex)
        {
            throw new MojoExecutionException("Unable to copy home directory", ex);
        }
    }

    protected File getRootDir(File tmpDir, Product ctx) throws MojoExecutionException, IOException
    {
        File[] topLevelFiles = tmpDir.listFiles();
        if (topLevelFiles.length != 1)
        {
            Iterable<String> filenames = Iterables.transform(Arrays.asList(topLevelFiles), File::getName);
            throw new MojoExecutionException("Expected a single top-level directory in test resources. Got: "
                    + Joiner.on(", ").join(filenames));
        }
        return topLevelFiles[0];
    }

    /**
     * Takes 'app' (the file of the application - either .war or the exploded directory),
     * adds the artifacts, then returns the 'app'.
     * @return if {@code app} was a dir, returns a dir; if {@code app} was a war, returns a war.
     */
    private final File addArtifactsAndOverrides(final Product ctx, final File homeDir, final File app) throws MojoExecutionException
    {
        try
        {
            final File appDir;
            if (app.isFile())
            {
                appDir = new File(getBaseDirectory(ctx), "webapp");
                if (!appDir.exists())
                {
                    unzip(app, appDir.getAbsolutePath());
                }
            }
            else
            {
                appDir = app;
            }

            addArtifacts(ctx, homeDir, appDir);

            // override war files
            try
            {
                addOverrides(appDir, ctx);
                customiseInstance(ctx, homeDir, appDir);
                fixJvmArgs(ctx);
            }
            catch (IOException e)
            {
                throw new MojoExecutionException("Unable to override WAR files using src/test/resources/" + ctx.getInstanceId() + "-app", e);
            }

            if (app.isFile())
            {
                final File warFile = new File(app.getParentFile(), getId() + ".war");
                ZipUtils.zipChildren(warFile, appDir);
                return warFile;
            }
            else
            {
                return appDir;
            }

        }
        catch (final Exception e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Each product handler can add specific operations on the application's home and war.
     * By default no operation is performed in this hook.
     *
     * @param ctx the product's details
     * @param homeDir the home directory
     * @param explodedWarDir the directory containing the exploded WAR of the application
     * @throws MojoExecutionException
     */
    protected void customiseInstance(Product ctx, File homeDir, File explodedWarDir) throws MojoExecutionException
    {
        // No operation by default
    }

    /**
     * Fix jvmArgs, providing necessary defaults.
     * @param ctx Product to fix jvmArgs for
     */
    protected void fixJvmArgs(Product ctx)
    {
        final String jvmArgs = JvmArgsFix.defaults()
                .apply(ctx.getJvmArgs());
        ctx.setJvmArgs(jvmArgs);
    }

    private void addArtifacts(final Product ctx, final File homeDir, final File appDir) throws Exception
    {
        File pluginsDir = getUserInstalledPluginsDirectory(ctx, appDir, homeDir);
        File bundledPluginsDir = new File(getBaseDirectory(ctx), "bundled-plugins");

        bundledPluginsDir.mkdir();
        // add bundled plugins
        final File bundledPluginsFile = getBundledPluginPath(ctx, appDir);
        if (bundledPluginsFile.exists())
        {
            if (bundledPluginsFile.isDirectory())
            {
                bundledPluginsDir = bundledPluginsFile;
            }
            else
            {
                unzip(bundledPluginsFile, bundledPluginsDir.getPath());
            }
        }

        if (isStaticPlugin())
        {
            if (!supportsStaticPlugins())
            {
                  throw new MojoExecutionException("According to your atlassian-plugin.xml file, this plugin is not " +
                          "atlassian-plugins version 2. This app currently only supports atlassian-plugins " +
                          "version 2.");
            }
            pluginsDir = new File(appDir, "WEB-INF/lib");
        }

        if (pluginsDir == null)
        {
            pluginsDir = bundledPluginsDir;
        }

        createDirectory(pluginsDir);

        // add this plugin itself if enabled
        if (ctx.isInstallPlugin())
        {
            addThisPluginToDirectory(pluginsDir);
            addTestPluginToDirectory(pluginsDir);
        }

        // add plugins2 plugins if necessary
        if (!isStaticPlugin())
        {
            addArtifactsToDirectory(pluginProvider.provide(ctx), pluginsDir);
        }

        // add plugins1 plugins
        List<ProductArtifact> artifacts = new ArrayList<>();
        artifacts.addAll(getDefaultLibPlugins());
        artifacts.addAll(ctx.getLibArtifacts());
        addArtifactsToDirectory(artifacts, new File(appDir, getLibArtifactTargetDir()));

        //add plugins provided by applications
        List<ProductArtifact> applications = applicationMapper.provideApplications(ctx);
        extractApplicationPlugins(applications, pluginsDir);

        artifacts = new ArrayList<>();
        artifacts.addAll(getDefaultBundledPlugins());
        artifacts.addAll(ctx.getBundledArtifacts());
        artifacts.addAll(getAdditionalPlugins(ctx));

        addArtifactsToDirectory(artifacts, bundledPluginsDir);

        if (bundledPluginsDir.list().length > 0)
        {
            if (!bundledPluginsFile.isDirectory())
            {
                ZipUtils.zipChildren(bundledPluginsFile, bundledPluginsDir);
            }
        }

        if (ctx.getLog4jProperties() != null && getLog4jPropertiesPath() != null)
        {
            copyFile(ctx.getLog4jProperties(), new File(appDir, getLog4jPropertiesPath()));
        }
    }

    /**
     * Processes standard replacement of configuration placeholders in the home directory.
     */
    protected void processHomeDirectory(Product ctx, File snapshotDir) throws MojoExecutionException
    {
        ConfigFileUtils.replace(getConfigFiles(ctx, snapshotDir), getReplacements(ctx), false, log);
    }

    abstract protected File extractApplication(Product ctx, File homeDir) throws MojoExecutionException;
    abstract protected int startApplication(Product ctx, File app, File homeDir, Map<String, String> properties) throws MojoExecutionException;
    abstract protected boolean supportsStaticPlugins();
    abstract protected Collection<? extends ProductArtifact> getDefaultBundledPlugins();
    abstract protected Collection<? extends ProductArtifact> getDefaultLibPlugins();
    abstract protected File getBundledPluginPath(Product ctx, File appDir);
    abstract protected File getUserInstalledPluginsDirectory(Product product, File webappDir, File homeDir);

    protected List<ProductArtifact> getAdditionalPlugins(Product ctx) throws MojoExecutionException {
        return emptyList();
    }

    protected String getLog4jPropertiesPath()
    {
        return null;
    }

    protected boolean isStaticPlugin() throws IOException
    {
        final File atlassianPluginXml = new File(project.getBasedir(), "src/main/resources/atlassian-plugin.xml");
        if (atlassianPluginXml.exists())
        {
            String text = readFileToString(atlassianPluginXml, StandardCharsets.UTF_8);
            return !text.contains("pluginsVersion=\"2\"") && !text.contains("plugins-version=\"2\"");
        }
        else
        {
            // probably an osgi bundle
            return false;
        }
    }

    protected final void addThisPluginToDirectory(final File targetDir) throws IOException
    {
        final File thisPlugin = getPluginFile();

        if (thisPlugin.exists())
        {
            // remove any existing version
            for (final Iterator<?> iterateFiles = iterateFiles(targetDir, null, false); iterateFiles.hasNext();)
            {
                final File file = (File) iterateFiles.next();
                if (doesFileNameMatchArtifact(file.getName(), project.getArtifactId()))
                {
                    file.delete();
                }
            }

            // add the plugin jar to the directory
            copyFile(thisPlugin, new File(targetDir, thisPlugin.getName()));
        }
        else
        {
            log.info("No plugin in the current project - " + thisPlugin.getAbsolutePath());
        }
    }

    protected void addTestPluginToDirectory(final File targetDir) throws IOException
    {
        final File testPluginFile = getTestPluginFile();
        if (testPluginFile.exists())
        {
            // add the test plugin jar to the directory
            copyFile(testPluginFile, new File(targetDir, testPluginFile.getName()));
        }

    }

    protected final File getPluginFile()
    {
        return new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + ".jar");
    }

    protected File getTestPluginFile()
    {
        return new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + "-tests.jar");
    }

    protected final void addArtifactsToDirectory(final List<ProductArtifact> artifacts, final File pluginsDir) throws MojoExecutionException
    {
        // copy the all the plugins we want in the webapp
        if (!artifacts.isEmpty())
        {
            // first remove plugins from the webapp that we want to update
            if (pluginsDir.isDirectory() && pluginsDir.exists())
            {
                for (final Iterator<?> iterateFiles = iterateFiles(pluginsDir, null, false); iterateFiles.hasNext();)
                {
                    final File file = (File) iterateFiles.next();
                    for (final ProductArtifact webappArtifact : artifacts)
                    {
                        if (!file.isDirectory() && doesFileNameMatchArtifact(file.getName(), webappArtifact.getArtifactId()))
                        {
                            file.delete();
                        }
                    }
                }
            }
            goals.copyPlugins(pluginsDir, artifacts);
        }
    }

    private void extractApplicationPlugins(final List<ProductArtifact> products, final File bundledPluginsDir)
            throws RuntimeException, IOException
    {
        for (final ProductArtifact product : products)
        {
            final File artifact = resolveArtifactForProduct(product).getFile();
            log.info("Extracting " + artifact.getAbsolutePath() + " into " + bundledPluginsDir.getAbsolutePath());
            unzip(artifact, bundledPluginsDir.getAbsolutePath(), 0, true, Pattern.compile(".*\\.jar"));
            log.debug("Extracted.");
        }
    }

    private Artifact resolveArtifactForProduct(final ProductArtifact product)
    {
        final Artifact artifact = artifactFactory.createArtifact(product.getGroupId(), product.getArtifactId(), product.getVersion(), "compile", "obr");
        try
        {
            final MavenSession session = context.getExecutionEnvironment().getMavenSession();
            final MavenProject project = context.getExecutionEnvironment().getMavenProject();
            final ArtifactResolver resolver = session.getContainer().lookup(ArtifactResolver.class);
            resolver.resolve(artifact, project.getRemoteArtifactRepositories(), session.getLocalRepository());
        }
        catch (ArtifactNotFoundException | ArtifactResolutionException | ComponentLookupException e)
        {
            throw new RuntimeException(e);
        }
        return artifact;
    }

    protected final void addOverrides(File appDir, final Product ctx) throws IOException
    {
        final File srcDir = new File(project.getBasedir(), "src/test/resources/" + ctx.getInstanceId() + "-app");
        if (srcDir.exists() && appDir.exists())
        {
            FileUtils.copyDirectory(srcDir, appDir, true);
        }
    }

    /**
     * Merges the properties: pom.xml overrides {@link AbstractProductHandlerMojo#setDefaultValues} overrides the Product Handler.
     * @param ctx the Product
     * @return the complete list of system properties
     */
    protected final Map<String, String> mergeSystemProperties(Product ctx)
    {
        // Start from the base properties
        final Map<String, String> properties = new HashMap<>(getSystemProperties(ctx));

        // Set the JARs to be skipped when scanning for TLDs and web fragments (read from context.xml by Tomcat)
        properties.put("jarsToSkip", getJarsToSkipWhenScanningForTldsAndWebFragments());

        // Enter the System Property Variables from product context, overwriting duplicates
        ctx.getSystemPropertyVariables().forEach((key, value) -> properties.put(key, (String) value));

        // Overwrite the default system properties with user input arguments
        Properties userProperties = context.getExecutionEnvironment().getMavenSession().getUserProperties();
        userProperties.forEach((key, value) -> properties.put((String) key, (String) value));

        return properties;
    }

    private String getJarsToSkipWhenScanningForTldsAndWebFragments() {
        final Set<String> jarsToSkip = new HashSet<>();
        jarsToSkip.add("${tomcat.util.scan.StandardJarScanFilter.jarsToSkip}");  // the Tomcat default
        jarsToSkip.addAll(getExtraJarsToSkipWhenScanningForTldsAndWebFragments());
        return join(",", jarsToSkip);
    }

    /**
     * Products should override this method in order to avoid scanning JARs known not to contain TLDs or web fragments.
     *
     * @return a list of any extra JAR name patterns to skip, e.g. "foo*.jar"
     */
    @Nonnull
    protected Collection<String> getExtraJarsToSkipWhenScanningForTldsAndWebFragments() {
        return emptyList();
    }

    /**
     * System properties which are specific to the Product Handler
     */
    protected abstract Map<String, String> getSystemProperties(Product ctx);

    /**
     * The artifact of the product (a war, a jar, a binary...)
     */
    public abstract ProductArtifact getArtifact();

    /**
     * Returns the directory where jars listed in  <libArtifacts> are
     * copied. Default is "WEB-INF/lib"
     *
     * @return the directory where lib artifacts should be written
     */
    protected String getLibArtifactTargetDir()
    {
    	return "WEB-INF/lib";
    }

}
