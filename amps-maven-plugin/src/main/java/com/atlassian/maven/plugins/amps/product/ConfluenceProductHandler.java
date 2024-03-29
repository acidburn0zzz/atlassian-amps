package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.XmlOverride;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils.Replacement;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ConfluenceProductHandler extends AbstractWebappProductHandler
{
    private final static TreeMap<ComparableVersion, String> synchronyProxyVersions = new TreeMap<>();

    // system property to override deploying the synchrony-proxy webapp
    public static final String REQUIRE_SYNCHRONY_PROXY = "require.synchrony.proxy";
    public static final String SYNCHRONY_PROXY_VERSION = "synchrony.proxy.version";
    private final ProductArtifact synchronyProxy = new ProductArtifact("com.atlassian.synchrony", "synchrony-proxy", "RELEASE", "war");

    public ConfluenceProductHandler(MavenContext context, MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, new ConfluencePluginProvider(), artifactFactory);
        synchronyProxyVersions.put(new ComparableVersion("6.4.10000"), "1.0.17");
        synchronyProxyVersions.put(new ComparableVersion("10000"), "RELEASE");
    }

    public String getId()
    {
        return "confluence";
    }

    @Override
    protected boolean isStaticPlugin()
    {
        // assume all Confluence plugins should be installed as bundled plugins -- a pretty good assumption
        return false;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.confluence", "confluence-webapp", "RELEASE");
    }

    @Override
    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.confluence.plugins", "confluence-plugin-test-resources");
    }

    @Override
    public int getDefaultHttpPort()
    {
        return 1990;
    }

    @Override
    public int getDefaultHttpsPort()
    {
        return 8441;
    }


    @Override
    public Map<String, String> getSystemProperties(Product ctx)
    {
        ImmutableMap.Builder<String, String> systemProperties = ImmutableMap.<String, String>builder();
        systemProperties.putAll(super.getSystemProperties(ctx));
        systemProperties.put("confluence.home", getHomeDirectory(ctx).getPath());
        systemProperties.put("cargo.servlet.uriencoding", "UTF-8");
        return systemProperties.build();
    }

    @Override
    public File getUserInstalledPluginsDirectory(final Product product, final File webappDir, File homeDir)
    {
        // indicates plugins should be bundled
        return null;
    }

    @Override
    public List<ProductArtifact> getExtraContainerDependencies()
    {
        return Collections.emptyList();
    }

    private boolean shouldDeploySynchronyProxy(Product ctx)
    {
        boolean synchronyProxyRequired = true;
        if (isNotBlank(System.getProperty(REQUIRE_SYNCHRONY_PROXY))) {
            synchronyProxyRequired = Boolean.parseBoolean(System.getProperty(REQUIRE_SYNCHRONY_PROXY));
        }

        return Character.getNumericValue(ctx.getVersion().charAt(0)) >= 6 && synchronyProxyRequired;
    }

    @Override
    public List<ProductArtifact> getExtraProductDeployables(Product ctx)
    {
        return shouldDeploySynchronyProxy(ctx) ? Arrays.asList(synchronyProxy) : Collections.emptyList();
    }

    @Override
    protected void customiseInstance(Product ctx, File homeDir, File explodedWarDir) throws MojoExecutionException
    {
        ctx.setCargoXmlOverrides(serverXmlConfluenceOverride());
        if (!shouldDeploySynchronyProxy(ctx))
        {
            log.debug("Synchrony proxy is disabled or not supported");
            return;
        }

        log.debug("Resolving synchrony proxy version for Confluence " + ctx.getVersion());
        // check if version is specified
        if (isNotBlank(System.getProperty(SYNCHRONY_PROXY_VERSION)))
        {
            log.debug("Synchrony proxy version is already set in system variable ("
                    + System.getProperty(SYNCHRONY_PROXY_VERSION) + ")");
            synchronyProxy.setVersion(System.getProperty(SYNCHRONY_PROXY_VERSION));
        }
        else
        {
            log.debug("Synchrony proxy version is not set. Attempting to set corresponding version");
            Map.Entry<ComparableVersion, String> synchronyProxyVersion =
                    synchronyProxyVersions.ceilingEntry(new ComparableVersion(ctx.getVersion()));
            if(synchronyProxyVersion != null)
            {
                synchronyProxy.setVersion(synchronyProxyVersion.getValue());
                log.debug("Synchrony proxy version is set to " + synchronyProxyVersion.getValue());
            }
        }

        // check for latest stable version if version not specified
        if (Artifact.RELEASE_VERSION.equals(synchronyProxy.getVersion()) ||
                Artifact.LATEST_VERSION.equals(synchronyProxy.getVersion()))
        {
            log.debug("determining latest stable synchrony-proxy version...");
            Artifact warArtifact = artifactFactory.createProjectArtifact(synchronyProxy.getGroupId(),
                    synchronyProxy.getArtifactId(), synchronyProxy.getVersion());
            String stableVersion = ctx.getArtifactRetriever().getLatestStableVersion(warArtifact);

            log.debug("using latest stable synchrony-proxy version: " + stableVersion);
            synchronyProxy.setVersion(stableVersion);
        }

        // copy synchrony-proxy webapp war to target
        File confInstall = getBaseDirectory(ctx);
        File war = goals.copyWebappWar("synchrony-proxy", new File(confInstall, "synchrony-proxy"), synchronyProxy);

        synchronyProxy.setPath(war.getPath());
    }

    private Collection<XmlOverride> serverXmlConfluenceOverride() {
        return Collections.singletonList(new XmlOverride("conf/server.xml", "//Connector", "maxThreads", "48"));
    }

    @Override
    public File getBundledPluginPath(Product ctx, File appDir)
    {
        String bundleDirPath = "WEB-INF/atlassian-bundled-plugins";
        final File bundleDir = new File(appDir, bundleDirPath);
        if (bundleDir.exists() && bundleDir.isDirectory())
        {
            return bundleDir;
        }
        else
        {
            return new File(appDir, "WEB-INF/classes/com/atlassian/confluence/setup/atlassian-bundled-plugins.zip");
        }
    }

    @Override
    public List<Replacement> getReplacements(Product ctx)
    {
        List<Replacement> replacements = super.getReplacements(ctx);
        File homeDir = getHomeDirectory(ctx);
        // We don't rewrap homes with these values:
        replacements.add(new Replacement("@project-dir@", homeDir.getParent()));
        replacements.add(new Replacement("/confluence-home/", "/home/", false));
        replacements.add(new Replacement("<baseUrl>http://localhost:1990/confluence</baseUrl>", "<baseUrl>" + ctx.getProtocol() + "://" + ctx.getServer() + ":" + ctx.getHttpPort() + "/" + ctx.getContextPath().replaceAll("^/|/$", "") + "</baseUrl>", false));
        replacements.add(new Replacement("<baseUrl>http://localhost:8080</baseUrl>", "<baseUrl>" + ctx.getProtocol() + "://" + ctx.getServer() + ":" + ctx.getHttpPort() + "/" + ctx.getContextPath().replaceAll("^/|/$", "") + "</baseUrl>", false));
        return replacements;
    }

    @Override
    public List<File> getConfigFiles(Product product, File homeDirectory)
    {
        List<File> configFiles = super.getConfigFiles(product, homeDirectory);
        configFiles.add(new File(new File(homeDirectory, "database"), "confluencedb.script"));
        configFiles.add(new File(new File(homeDirectory, "database"), "confluencedb.log"));
        configFiles.add(new File(homeDirectory, "confluence.cfg.xml"));
        return configFiles;
    }

    @Override
    public List<ProductArtifact> getDefaultLibPlugins()
    {
        return Collections.emptyList();
    }

    @Override
    public List<ProductArtifact> getDefaultBundledPlugins()
    {
        return Collections.emptyList();
    }

    private static class ConfluencePluginProvider extends AbstractPluginProvider
    {

        @Override
        protected Collection<ProductArtifact> getSalArtifacts(String salVersion)
        {
            return Arrays.asList(
                new ProductArtifact("com.atlassian.sal", "sal-api", salVersion),
                new ProductArtifact("com.atlassian.sal", "sal-confluence-plugin", salVersion));
        }

        @Override
        protected Collection<ProductArtifact> getPdkInstallArtifacts(String pdkInstallVersion)
        {
            return Collections.emptyList();
        }
    }

    @Override
    public void cleanupProductHomeForZip(Product product, File snapshotDir) throws MojoExecutionException, IOException
    {
        super.cleanupProductHomeForZip(product, snapshotDir);
        FileUtils.deleteDirectory(new File(snapshotDir, "plugins-osgi-cache"));
        FileUtils.deleteDirectory(new File(snapshotDir, "plugins-temp"));
        FileUtils.deleteDirectory(new File(snapshotDir, "temp"));
    }
}
