package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.JIRA_PLATFORM;

public class JiraPlatformProductHandler extends JiraProductHandler
{
      public JiraPlatformProductHandler(final MavenContext context, final MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, artifactFactory);
    }

    public String getId()
    {
        return JIRA_PLATFORM;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.jira", "atlassian-jira-webapp-platform", "RELEASE");
    }

    @Override
    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.jira.plugins", "jira-platform-plugin-test-resources");
    }
}
