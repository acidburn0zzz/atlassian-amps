package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.SERVICEDESK;

public class JiraServiceDeskProductHandler extends JiraProductHandler
{
      public JiraServiceDeskProductHandler(final MavenContext context, final MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, artifactFactory);
    }

    public String getId()
    {
        return SERVICEDESK;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.servicedesk", "servicedesk-webapp-dist", "RELEASE");
    }

    @Override
    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.servicedesk", "servicedesk-plugin-test-resources");
    }
}
