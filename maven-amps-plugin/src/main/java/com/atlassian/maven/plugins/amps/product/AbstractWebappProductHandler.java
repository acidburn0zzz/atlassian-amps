package com.atlassian.maven.plugins.amps.product;

import java.io.File;

import java.util.List;
import java.util.Map;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.google.common.collect.ImmutableMap;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;

import static com.atlassian.maven.plugins.amps.util.ProjectUtils.firstNotNull;

public abstract class AbstractWebappProductHandler extends AbstractProductHandler
{
    public AbstractWebappProductHandler(final MavenContext context, final MavenGoals goals, PluginProvider pluginProvider, ArtifactFactory artifactFactory)
    {
        super(context, goals, pluginProvider, artifactFactory);
    }

    public final void stop(final Product ctx) throws MojoExecutionException
    {
        goals.stopWebapp(ctx.getInstanceId(), ctx.getContainerId(), ctx);
    }

    @Override
    protected final File extractApplication(Product ctx, File homeDir) throws MojoExecutionException
    {
        ProductArtifact defaults = getArtifact();
        ProductArtifact artifact = new ProductArtifact(
            firstNotNull(ctx.getGroupId(), defaults.getGroupId()),
            firstNotNull(ctx.getArtifactId(), defaults.getArtifactId()),
            firstNotNull(ctx.getVersion(), defaults.getVersion()));

        //check for a stable version if needed
        if(Artifact.RELEASE_VERSION.equals(artifact.getVersion()) || Artifact.LATEST_VERSION.equals(artifact.getVersion()))
        {
            log.info("determining latest stable product version...");
            Artifact warArtifact = artifactFactory.createProjectArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
            String stableVersion = ctx.getArtifactRetriever().getLatestStableVersion(warArtifact);

            log.info("using latest stable product version: " + stableVersion);
            artifact.setVersion(stableVersion);
            ctx.setVersion(stableVersion);
        }
        
        // Copy the webapp war to target
        return goals.copyWebappWar(ctx.getId(), getBaseDirectory(ctx), artifact);
    }

    @Override
    protected final int startApplication(Product ctx, File app, File homeDir, Map<String, String> properties) throws MojoExecutionException
    {
        return goals.startWebapp(ctx.getInstanceId(), app, properties,
                getExtraContainerDependencies(), getExtraProductDeployables(ctx), ctx);
    }

    @Override
    protected boolean supportsStaticPlugins()
    {
        return true;
    }

    @Override
    protected String getLog4jPropertiesPath()
    {
        return "WEB-INF/classes/log4j.properties";
    }

    protected abstract List<ProductArtifact> getExtraContainerDependencies();

    protected abstract List<ProductArtifact> getExtraProductDeployables(Product ctx);

    @Override
    protected Map<String, String> getSystemProperties(Product ctx)
    {
        return generateDataSourceSystemProperties(ctx);
    }

    protected Map<String, String> generateDataSourceSystemProperties(Product context)
    {
        ImmutableMap.Builder<String, String> systemProperties = ImmutableMap.builder();
        List<DataSource> dataSources = context.getDataSources();
        DataSource defaultValues = getDefaultDataSource(context);
        
        if (defaultValues != null)
        {
            if (dataSources.isEmpty())
            {
                dataSources.add(defaultValues);
            }
            else
            {
                dataSources.get(0).useForUnsetValues(defaultValues);
            }
        }
        if (dataSources.size()==1)
        {
            // TWData Cargo doesn't support other names than "cargo.datasource.datasource".
            systemProperties.put("cargo.datasource.datasource", dataSources.get(0).getCargoString());
        }
        else
        {
            // Multiple datasources requires CodeHaus Cargo
            for (int i = 0; i < dataSources.size(); i++)
            {
                systemProperties.put("cargo.datasource.datasource." + i, dataSources.get(i).getCargoString());
            }
        }
        return systemProperties.build();
    }

    /**
     * Opportunity for the product handler to define a default datasource for the product.
     * @return a bean containing the default values for the datasource.
     */
    protected DataSource getDefaultDataSource(Product ctx)
    {
        return null;
    }
}
