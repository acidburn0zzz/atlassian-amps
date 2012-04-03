
package com.atlassian.maven.plugins.amps.product.studio;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.product.BambooProductHandler;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.STUDIO_BAMBOO;
import static com.atlassian.maven.plugins.amps.util.FileUtils.fixWindowsSlashes;
import static java.lang.String.format;

/**
 * Handler for Studio-Bamboo
 * @since 3.6
 */
public class StudioBambooProductHandler extends BambooProductHandler
{

    public StudioBambooProductHandler(MavenContext context, MavenGoals goals)
    {
        super(context, goals);
    }

    @Override
    public String getId()
    {
        return STUDIO_BAMBOO;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.studio", "studio-bamboo", "RELEASE");
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
    public List<ProductArtifact> getExtraContainerDependencies()
    {
        return Arrays.asList(
                new ProductArtifact("hsqldb", "hsqldb", "1.8.0.5"),
                new ProductArtifact("jta", "jta", "1.0.1"));
    }

    @Override
    public Map<String, String> getSystemProperties(final Product product)
    {
        Map<String, String> systemProperties = new HashMap<String, String>(super.getSystemProperties(product));

        DataSource ds = product.getDataSource();
        ds.setDefaultValues("jdbc/DefaultDS",
                format("jdbc:hsqldb:%s/database", fixWindowsSlashes(getHomeDirectory(product).getAbsolutePath())),
                   "org.hsqldb.jdbcDriver", "sa", "", "javax.sql.DataSource", null, null);

        systemProperties.put("cargo.datasource.datasource", ds.getCargoString());

        // We also add common studio system properties
        systemProperties.putAll(product.getStudioProperties().getSystemProperties());

        return systemProperties;
    }

}
