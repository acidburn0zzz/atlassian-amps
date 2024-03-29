package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singleton;

public class CrowdProductHandler extends AbstractWebappProductHandler
{
    public CrowdProductHandler(final MavenContext context, final MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, new CrowdPluginProvider(),artifactFactory);
    }

    public String getId()
    {
        return ProductHandlerFactory.CROWD;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.crowd", "crowd-web-app", "RELEASE");
    }

    @Override
    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.crowd.distribution", "crowd-plugin-test-resources");
    }

    public int getDefaultHttpPort()
    {
        return 4990;
    }

    @Override
    public int getDefaultHttpsPort()
    {
        return 8444;
    }

    @Override
    public Map<String, String> getSystemProperties(final Product ctx)
    {
        ImmutableMap.Builder<String, String> systemProperties = ImmutableMap.builder();
        systemProperties.putAll(super.getSystemProperties(ctx));
        systemProperties.put("crowd.home", getHomeDirectory(ctx).getPath());
        systemProperties.put("cargo.servlet.uriencoding", "UTF-8");
        return systemProperties.build();
    }

    @Override
    public File getUserInstalledPluginsDirectory(final Product product, final File webappDir, final File homeDir)
    {
        final File sharedHomeDir = new File(homeDir, "shared");
        if (sharedHomeDir.exists())
        {
            return new File(sharedHomeDir, "plugins");
        }
        else {
            return new File(homeDir, "plugins");
        }
    }

    @Override
    public List<ProductArtifact> getExtraContainerDependencies()
    {
        return Arrays.asList(
                new ProductArtifact("hsqldb", "hsqldb", "1.8.0.7"),
                new ProductArtifact("javax.transaction", "jta", "1.1"),
                new ProductArtifact("javax.mail", "mail", "1.4"),
                new ProductArtifact("javax.activation", "activation", "1.0.2")
        );
    }

    @Nonnull
    @Override
    protected Collection<String> getExtraJarsToSkipWhenScanningForTldsAndWebFragments() {
        // AMPS-1524: mail-1.4.jar has `activation.jar` in its Class-Path manifest header, not activation-1.0.2.jar
        return singleton("mail-*.jar");
    }

    @Override
    public File getBundledPluginPath(Product ctx, File appDir)
    {
        return new File(appDir, "WEB-INF/classes/atlassian-bundled-plugins.zip");
    }

    @Override
    public void processHomeDirectory(final Product ctx, final File homeDir) throws MojoExecutionException
    {
        /* Replace %TOKENS% */
        super.processHomeDirectory(ctx, homeDir);

        /* Now Crowd-specific config changes */
        String baseUrl = MavenGoals.getBaseUrl(ctx, ctx.getHttpPort());

        /* Crowd connects back to itself; use 'localhost' rather than the hostname an external client would see */
        try
        {
            baseUrl = withLocalhostAsHostname(baseUrl);
        }
        catch (URISyntaxException e)
        {
            throw new MojoExecutionException("Unable to process Crowd service URL", e);
        }

        try
        {
            ConfigFileUtils.replaceAll(new File(homeDir, "shared/crowd.cfg.xml"),
                    "jdbc:hsqldb:.*/(crowd-)?home/database/defaultdb",
                    "jdbc:hsqldb:" + getHomeDirectory(ctx).getCanonicalPath().replace("\\", "/") + "/database/defaultdb");
            ConfigFileUtils.replaceAll(new File(homeDir, "crowd.cfg.xml"),
                    "jdbc:hsqldb:.*/(crowd-)?home/database/defaultdb",
                    "jdbc:hsqldb:" + getHomeDirectory(ctx).getCanonicalPath().replace("\\", "/") + "/database/defaultdb");

            Map<String, String> newProperties = ImmutableMap.of(
                    "crowd.server.url", baseUrl + "/services",
                    "application.login.url", baseUrl
            );
            ConfigFileUtils.setProperties(new File(homeDir, "crowd.properties"), newProperties);
        }
        catch (final IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    static String withLocalhostAsHostname(String uri) throws URISyntaxException
    {
        URI base = new URI(uri);

        URI baseWithLocalhost = new URI(
                base.getScheme(),
                base.getUserInfo(),
                "localhost",
                base.getPort(),
                base.getPath(),
                base.getQuery(),
                base.getFragment());

        return baseWithLocalhost.toString();
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

    private static class CrowdPluginProvider extends AbstractPluginProvider
    {

        @Override
        protected Collection<ProductArtifact> getSalArtifacts(final String salVersion)
        {
            return Arrays.asList(
                    new ProductArtifact("com.atlassian.sal", "sal-api", salVersion),
                    new ProductArtifact("com.atlassian.sal", "sal-crowd-plugin", salVersion));
        }

        @Override
        protected Collection<ProductArtifact> getPdkInstallArtifacts(final String pdkInstallVersion)
        {
            final List<ProductArtifact> plugins = new ArrayList<>(super.getPdkInstallArtifacts(pdkInstallVersion));
            plugins.add(new ProductArtifact("commons-fileupload", "commons-fileupload", "1.2.1"));
            return plugins;
        }
    }

    @Override
    public void cleanupProductHomeForZip(Product product, File homeDirectory) throws MojoExecutionException, IOException
    {
        super.cleanupProductHomeForZip(product, homeDirectory);
        FileUtils.deleteQuietly(new File(homeDirectory, "caches"));
        FileUtils.deleteQuietly(new File(homeDirectory, "logs"));
    }

    @Override
    public List<File> getConfigFiles(Product product, File snapshotDir)
    {
        List<File> configFiles = super.getConfigFiles(product, snapshotDir);
        configFiles.add(new File(snapshotDir, "database.log"));
        configFiles.add(new File(snapshotDir, "crowd.cfg.xml"));
        configFiles.add(new File(snapshotDir, "shared/crowd.cfg.xml"));
        configFiles.add(new File(snapshotDir, "crowd.properties"));
        return configFiles;
    }
}
