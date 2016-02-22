package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils.Replacement;
import com.atlassian.maven.plugins.amps.util.Version;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @since 6.1.0
 */
public class BitbucketProductHandler extends AbstractWebappProductHandler
{
    private static final String FIRST_SEARCH_VERSION = "4.5.0";

    public BitbucketProductHandler(final MavenContext context, final MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, new BitbucketPluginProvider(),artifactFactory);
    }

    @Override
    public void cleanupProductHomeForZip(Product product, File snapshotDir) throws MojoExecutionException, IOException
    {
        super.cleanupProductHomeForZip(product, snapshotDir);
        FileUtils.deleteQuietly(new File(snapshotDir, "log/atlassian-bitbucket.log"));
        FileUtils.deleteQuietly(new File(snapshotDir, ".osgi-cache"));
    }

    @Override
    public String getId()
    {
        return ProductHandlerFactory.BITBUCKET;
    }

    @Override
    public List<ProductArtifact> getAdditionalPlugins(Product ctx) {
        Version productVersion = Version.valueOf(ctx.getVersion());
        if(productVersion.isGreaterOrEqualTo(Version.valueOf(FIRST_SEARCH_VERSION))) {
            return Collections.singletonList(new ProductArtifact("com.atlassian.bitbucket.server", "bitbucket-distribution-search-bundle", ctx.getVersion()));
        }
        return Collections.emptyList();
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.bitbucket.server", "bitbucket-webapp");
    }

    @Override
    public File getBundledPluginPath(Product ctx, File appDir)
    {
        String bundledPluginPluginsPath = "WEB-INF/classes/bitbucket-bundled-plugins.zip";
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
        return "tomcat8x";
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
        String baseUrl = MavenGoals.getBaseUrl(ctx, ctx.getHttpPort());

        return ImmutableMap.<String, String>builder()
                .putAll(super.getSystemProperties(ctx))
                .put("baseurl", baseUrl)
                .put("baseurl.display", baseUrl)
                .put("bitbucket.home", fixSlashes(getHomeDirectory(ctx).getPath()))
                .put("cargo.servlet.uriencoding", "UTF-8")
                .put("johnson.spring.lifecycle.synchronousStartup", Boolean.TRUE.toString())
                .build();
    }

    @Override
    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.bitbucket.server", "bitbucket-it-resources");
    }

    @Override
    public File getUserInstalledPluginsDirectory(final Product product, final File webappDir, final File homeDir)
    {
        File baseDir = homeDir;

        File sharedHomeDir = new File(homeDir, "shared");
        if(sharedHomeDir.exists())
        {
            baseDir = sharedHomeDir;
        }

        return new File(new File(baseDir, "plugins"), "installed-plugins");
    }

    private static class BitbucketPluginProvider extends AbstractPluginProvider
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
