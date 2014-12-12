package com.atlassian.maven.plugins.amps.product;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils.Replacement;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;

public class StashProductHandler extends AbstractWebappProductHandler
{
    public StashProductHandler(final MavenContext context, final MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, new StashPluginProvider(),artifactFactory);
    }

    @Override
    public void cleanupProductHomeForZip(Product product, File snapshotDir) throws MojoExecutionException, IOException
    {
        super.cleanupProductHomeForZip(product, snapshotDir);
        FileUtils.deleteQuietly(new File(snapshotDir, "log/atlassian-stash.log"));
        FileUtils.deleteQuietly(new File(snapshotDir, ".osgi-cache"));
    }

    @Override
    public String getId()
    {
        return ProductHandlerFactory.STASH;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.stash", "stash-webapp");
    }

    @Override
    public File getBundledPluginPath(Product ctx, File appDir)
    {
        String bundledPluginPluginsPath = "WEB-INF/classes/stash-bundled-plugins.zip";
        return new File(appDir, bundledPluginPluginsPath);
    }

    @Override
    public List<File> getConfigFiles(Product product, File homeDir)
    {
        List<File> configFiles = super.getConfigFiles(product, homeDir);
        configFiles.add(new File(homeDir, "data/db.log"));
        configFiles.add(new File(homeDir, "data/db.script"));
        configFiles.add(new File(homeDir, "data/db.properties"));
        configFiles.add(new File(homeDir, "shared/data/db.log"));
        configFiles.add(new File(homeDir, "shared/data/db.script"));
        configFiles.add(new File(homeDir, "shared/data/db.properties"));
        return configFiles;
    }

    @Override
    public List<ProductArtifact> getDefaultBundledPlugins()
    {
        return Collections.emptyList();
    }

    @Override
    public String getDefaultContainerId()
    {
        return "tomcat7x";
    }

    @Override
    public int getDefaultHttpPort()
    {
        return 7990;
    }

    @Override
    public int getDefaultHttpsPort()
    {
        return 8447;
    }

    @Override
    public List<ProductArtifact> getDefaultLibPlugins()
    {
        return Collections.emptyList();
    }

    @Override
    public List<ProductArtifact> getExtraContainerDependencies()
    {
        return Collections.emptyList();
    }

    @Override
    public List<Replacement> getReplacements(Product ctx)
    {
        return super.getReplacements(ctx);
    }

    @Override
    public Map<String, String> getSystemProperties(final Product ctx)
    {
        ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
        properties.putAll(super.getSystemProperties(ctx));
        properties.put("stash.home", fixSlashes(getHomeDirectory(ctx).getPath()));

        String baseUrl = MavenGoals.getBaseUrl(ctx, ctx.getHttpPort());
        properties.put("baseurl", baseUrl);
        properties.put("baseurl.display", baseUrl);
        properties.put("cargo.servlet.uriencoding", "UTF-8");
        return properties.build();
    }

    @Override
    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.stash", "stash-plugin-test-resources");
    }

    @Override
    public File getUserInstalledPluginsDirectory(final Product product, final File webappDir, final File homeDir)
    {
        File baseDir = homeDir;

        File sharedHomeDir = new File(homeDir, "shared");
        // In Stash 3.1 we've changed the home directory layout and moved plugins/installed-plugins under
        // shared home, which usually is home/shared. If this directory does not exist, then we assume that
        // Stash will do the migration at a later point.
        if(sharedHomeDir.exists()) {
            baseDir = sharedHomeDir;
        }

        return new File(new File(baseDir, "plugins"), "installed-plugins");
    }

    private static class StashPluginProvider extends AbstractPluginProvider
    {

        @Override
        protected Collection<ProductArtifact> getSalArtifacts(String salVersion)
        {
            return Collections.emptyList();
        }
    }

    private static String fixSlashes(final String path)
    {
        return path.replaceAll("\\\\", "/");
    }

}
