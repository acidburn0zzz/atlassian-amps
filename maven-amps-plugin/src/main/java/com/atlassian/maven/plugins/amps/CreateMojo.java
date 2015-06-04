package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.product.AbstractProductHandler;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.util.AmpsCreatePluginPrompter;
import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Creates a new plugin
 */
@Mojo(name = "create", requiresProject = false)
public class CreateMojo extends AbstractProductHandlerMojo
{
    @Component (hint = "amps-create-plugin-prompter")
    private AmpsCreatePluginPrompter ampsCreatePluginPrompter;

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        trackFirstRunIfNeeded();

        getGoogleTracker().track(GoogleAmpsTracker.CREATE_PLUGIN);


        String pid = getProductId();
        Product ctx = getProductContexts().get(pid);
        AbstractProductHandler handler = createProductHandler(pid);

        getLog().info("determining latest stable product version...");
        String stableVersion = getStableProductVersion(handler,ctx);

        if (StringUtils.isNotBlank(stableVersion))
        {
            getLog().info("using latest stable product version: " + stableVersion);
            getMavenContext().getExecutionEnvironment().getMavenSession().getExecutionProperties().setProperty(pid + "Version", stableVersion);
        }

        getLog().info("determining latest stable data version...");
        String stableDataVersion = getStableDataVersion(handler,ctx);

        if (StringUtils.isNotBlank(stableDataVersion))
        {
            getLog().info("using latest stable data version: " + stableDataVersion);
            getMavenContext().getExecutionEnvironment().getMavenSession().getExecutionProperties().setProperty(pid + "DataVersion", stableDataVersion);
        }

        getMavenGoals().createPlugin(getProductId(), ampsCreatePluginPrompter);
    }

    protected String getStableProductVersion(AbstractProductHandler handler, Product ctx) throws MojoExecutionException
    {
        ProductArtifact artifact = handler.getArtifact();

        if(null == artifact)
        {
            return "";
        }

        Artifact warArtifact = artifactFactory.createProjectArtifact(artifact.getGroupId(), artifact.getArtifactId(), "LATEST");

        return ctx.getArtifactRetriever().getLatestStableVersion(warArtifact);
    }

    protected String getStableDataVersion(AbstractProductHandler handler, Product ctx) throws MojoExecutionException
    {
        ProductArtifact artifact = handler.getTestResourcesArtifact();

        if(null == artifact)
        {
            return "";
        }

        Artifact warArtifact = artifactFactory.createProjectArtifact(artifact.getGroupId(), artifact.getArtifactId(), "LATEST");

        return ctx.getArtifactRetriever().getLatestStableVersion(warArtifact);
    }

    protected AbstractProductHandler createProductHandler(String productId)
    {
        return (AbstractProductHandler) ProductHandlerFactory.create(productId, getMavenContext(), getMavenGoals(), artifactFactory);
    }
}
