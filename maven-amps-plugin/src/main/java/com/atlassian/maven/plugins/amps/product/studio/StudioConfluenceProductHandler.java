package com.atlassian.maven.plugins.amps.product.studio;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.product.ConfluenceProductHandler;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.STUDIO_CONFLUENCE;
import static com.atlassian.maven.plugins.amps.util.FileUtils.fixWindowsSlashes;
import static java.lang.String.format;

/**
 * Handler for Studio-Confluence
 * @since 3.6
 */
public class StudioConfluenceProductHandler extends ConfluenceProductHandler implements StudioComponentProductHandler
{

    public StudioConfluenceProductHandler(MavenContext context, MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals,artifactFactory);
    }

    @Override
    public String getId()
    {
        return STUDIO_CONFLUENCE;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.confluence", "confluence-studio-webapp", "RELEASE");
    }

    @Override
    public Map<String, String> getSystemProperties(final Product product)
    {
        Map<String, String> systemProperties = new HashMap<String, String>(super.getSystemProperties(product));

        // We also add common studio system properties
        systemProperties.putAll(product.getStudioProperties().getSystemProperties());

        return systemProperties;
    }
    
    @Override
    protected DataSource getDefaultDataSource(Product ctx)
    {
        DataSource dataSource = new DataSource();
        dataSource.setJndi("jdbc/DefaultDS");
        dataSource.setUrl(format("jdbc:hsqldb:%s/database/confluencedb;hsqldb.tx=MVCC", fixWindowsSlashes(getHomeDirectory(ctx).getAbsolutePath())));
        dataSource.setDriver("org.hsqldb.jdbcDriver");
        dataSource.setType("javax.sql.DataSource");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Override
    public List<ProductArtifact> getExtraContainerDependencies()
    {
        return Arrays.asList(
                new ProductArtifact("org.hsqldb", "hsqldb", "2.2.4"),
                new ProductArtifact("jta", "jta", "1.0.1"));
    }

    @Override
    public void processHomeDirectory(Product ctx, File homeDir) throws MojoExecutionException
    {
        super.processHomeDirectory(ctx, homeDir);
        StudioProductHandler.processProductsHomeDirectory(log, ctx, homeDir);
    }

    @Override
    protected void customiseInstance(Product ctx, File homeDir, File explodedWarDir) throws MojoExecutionException
    {
        StudioProductHandler.addProductHandlerOverrides(log, ctx, homeDir, explodedWarDir);
    }
}
