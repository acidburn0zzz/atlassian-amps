package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.JIRA_SOFTWARE;

public class JiraSoftwareProductHandler extends JiraProductHandler
{
      public JiraSoftwareProductHandler(final MavenContext context, final MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, artifactFactory);
    }

    public String getId()
    {
        return JIRA_SOFTWARE;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.jira", "jira-software-webapp-dist", "RELEASE");
    }

    @Override
    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.jira", "jira-software-plugin-test-resources");
    }
}
