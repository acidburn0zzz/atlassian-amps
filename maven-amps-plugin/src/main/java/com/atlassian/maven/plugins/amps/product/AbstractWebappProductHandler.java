package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.google.common.collect.ImmutableMap;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.atlassian.maven.plugins.amps.util.ProjectUtils.firstNotNull;

public abstract class AbstractWebappProductHandler extends AbstractProductHandler
{
    private static final String CARGO_CONTAINER_ID_PROPERTY = "amps.product.specific.cargo.container";
    private static final String SPECIFIC_CONTAINER_PROPERTY= "amps.product.specific.container";

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

    protected List<ProductArtifact> getExtraProductDeployables(Product ctx) {
        return Collections.emptyList();
    }

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

    /**
     * Overrides version of webapp container artifact based on product pom
     *
     * @param ctx product context
     * @throws MojoExecutionException throw during creating effective pom
     */
    @Override
    protected void addOverridesFromProductPom(Product ctx) throws MojoExecutionException {
        ProductArtifact defaults = getArtifact();
        ProductArtifact artifact = new ProductArtifact(
                firstNotNull(ctx.getGroupId(), defaults.getGroupId()),
                firstNotNull(ctx.getArtifactId(), defaults.getArtifactId()),
                firstNotNull(ctx.getVersion(), defaults.getVersion()));
        File effectivePom = goals.generateEffectivePom(artifact, getBaseDirectory(ctx));

        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(effectivePom.getAbsoluteFile());
            Element properties = document.getRootElement().element("properties");


            if (properties != null) {
                Element customContainer = properties.element(SPECIFIC_CONTAINER_PROPERTY);
                Element cargoId = properties.element(CARGO_CONTAINER_ID_PROPERTY);
                setPropertiesInProduct(ctx, customContainer, cargoId);
            }

        } catch (DocumentException | MalformedURLException e) {
            log.error("Error when reading effective pom", e);
        }
    }

    private void setPropertiesInProduct(Product ctx, Element customContainer, Element cargoId) {
        if (customContainer != null) {
            if (ctx.getCustomContainerArtifact() == null && ctx.isContainerNotSpecified()) {
                ctx.setCustomContainerArtifact(customContainer.getStringValue());
                if (cargoId != null) {
                    ctx.setContainerId(cargoId.getStringValue());
                }
            }
        }
    }
}
