
package com.atlassian.maven.plugins.amps.product.studio;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.STUDIO_CROWD;
import static com.atlassian.maven.plugins.amps.util.FileUtils.fixWindowsSlashes;
import static java.lang.String.format;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.product.CrowdProductHandler;

/**
 * Handler for Studio-Crowd
 * @since 3.6
 */
public class StudioCrowdProductHandler extends CrowdProductHandler implements StudioComponentProductHandler
{
    public StudioCrowdProductHandler(final MavenContext context, final MavenGoals goals)
    {
        super(context, goals);
    }

    @Override
    public String getId()
    {
        return STUDIO_CROWD;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        // We return the default artifact. May be overridden by the pom.
        return new ProductArtifact("com.atlassian.studio", "studio-crowd", "RELEASE");
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

    @Override
    public Map<String, String> getSystemProperties(Product product)
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
        dataSource.setUrl(format("jdbc:hsqldb:%s/database", fixWindowsSlashes(getHomeDirectory(ctx).getAbsolutePath())));
        dataSource.setDriver("org.hsqldb.jdbcDriver");
        dataSource.setType("javax.sql.DataSource");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

}