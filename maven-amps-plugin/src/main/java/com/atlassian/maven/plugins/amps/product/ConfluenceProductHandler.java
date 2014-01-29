package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils.Replacement;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfluenceProductHandler extends AbstractWebappProductHandler
{

    public ConfluenceProductHandler(MavenContext context, MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, new ConfluencePluginProvider(),artifactFactory);
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

    public int getDefaultHttpPort()
    {
        return 1990;
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
