package com.atlassian.maven.plugins.amps.product;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils.Replacement;

import com.google.common.collect.ImmutableMap;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;

import static com.atlassian.maven.plugins.amps.util.FileUtils.deleteDir;

public class BambooProductHandler extends AbstractWebappProductHandler
{
    private static final String BUNDLED_PLUGINS_UNZIPPED = "WEB-INF/atlassian-bundled-plugins";
    private static final String BUNDLED_PLUGINS_ZIP = "WEB-INF/classes/atlassian-bundled-plugins.zip";

    public BambooProductHandler(MavenContext context, MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, new BambooPluginProvider(),artifactFactory);
    }

    public String getId()
    {
        return "bamboo";
    }

    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.bamboo", "atlassian-bamboo-web-app", "RELEASE");
    }

    protected Collection<ProductArtifact> getSalArtifacts(String salVersion)
    {
        return Arrays.asList(
                new ProductArtifact("com.atlassian.sal", "sal-api", salVersion),
                new ProductArtifact("com.atlassian.sal", "sal-bamboo-plugin", salVersion));
    }

    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.bamboo.plugins", "bamboo-plugin-test-resources");
    }

    @Override
    public int getDefaultHttpPort()
    {
        return 6990;
    }

    @Override
    public int getDefaultHttpsPort()
    {
        return 8446;
    }

    public Map<String, String> getSystemProperties(Product ctx)
    {
        ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
        properties.putAll(super.getSystemProperties(ctx));
        properties.put("bamboo.home", getHomeDirectory(ctx).getPath());
        properties.put("org.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES", "false");
        properties.put("cargo.servlet.uriencoding", "UTF-8");
        properties.put("file.encoding", "UTF-8");
        return properties.build();
    }

    @Override
    public File getUserInstalledPluginsDirectory(final Product product, final File webappDir, final File homeDir)
    {
        return new File(homeDir, "plugins");
    }

    public List<ProductArtifact> getExtraContainerDependencies()
    {
        return Collections.emptyList();
    }

    public File getBundledPluginPath(Product ctx, File appDir)
    {
        // the zip became a directory in 5.9, so if the directory exists, use it, otherwise fall back to the old behaviour.
        final File bundleDir = new File(appDir, BUNDLED_PLUGINS_UNZIPPED);

        if (bundleDir.isDirectory())
        {
            return bundleDir;
        }
        else
        {
            return new File(appDir, BUNDLED_PLUGINS_ZIP);
        }
    }

    public void processHomeDirectory(final Product ctx, final File homeDir) throws MojoExecutionException
    {
        super.processHomeDirectory(ctx, homeDir);

        // The regex in the following search text is used to match IPv4 ([^:]+) or IPv6 (\[.+]) addresses.
        ConfigFileUtils.replaceAll(new File(homeDir, "/xml-data/configuration/administration.xml"),
                "https?://(?:[^:]+|\\[.+]):8085", ctx.getProtocol() + "://" + ctx.getServer() + ":" + ctx.getHttpPort() + "/" + ctx.getContextPath().replaceAll("^/|/$", ""));
    }



    @Override
    public List<Replacement> getReplacements(Product product)
    {
        List<Replacement> replacements = super.getReplacements(product);
        File homeDirectory = getHomeDirectory(product);
        // We don't rewrap homes with these values:
        replacements.add(new Replacement("@project-dir@", homeDirectory.getParent(), false));
        replacements.add(new Replacement("/bamboo-home/", "/home/", false));
        replacements.add(new Replacement("${bambooHome}", homeDirectory.getAbsolutePath(), false));
        return replacements;
    }

    @Override
    public List<File> getConfigFiles(Product product, File homeDirectory)
    {
        List<File> configFiles = super.getConfigFiles(product, homeDirectory);
        configFiles.add(new File(homeDirectory, "bamboo.cfg.xml"));
        configFiles.add(new File(homeDirectory, "database.log"));
        return configFiles;
    }

    public List<ProductArtifact> getDefaultLibPlugins()
    {
        return Collections.emptyList();
    }

    public List<ProductArtifact> getDefaultBundledPlugins()
    {
        return Collections.emptyList();
    }

    @Override
    public void cleanupProductHomeForZip(Product bamboo, File genDir) throws MojoExecutionException, IOException
    {
        super.cleanupProductHomeForZip(bamboo, genDir);
        deleteDir(new File(genDir, "jms-store"));
        deleteDir(new File(genDir, "caches"));
        deleteDir(new File(genDir, "logs"));
    }

    private static class BambooPluginProvider extends AbstractPluginProvider
    {

        @Override
        protected Collection<ProductArtifact> getSalArtifacts(String salVersion)
        {
            return Arrays.asList(
                new ProductArtifact("com.atlassian.sal", "sal-api", salVersion),
                new ProductArtifact("com.atlassian.sal", "sal-bamboo-plugin", salVersion));
        }

    }
}
