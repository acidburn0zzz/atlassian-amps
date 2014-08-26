package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.AddonProduct;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.maven.artifact.factory.ArtifactFactory;

import java.util.List;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.JIRA_PLATFORM;

public class JiraPlatformProductHandler extends JiraProductHandler
{
      public JiraPlatformProductHandler(final MavenContext context, final MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, new JiraPlatformPluginsProvider(), artifactFactory);
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

    private static class JiraPlatformPluginsProvider extends JiraPluginProvider {
        @Override
        public List<ProductArtifact> provideAddonProducts(final Product product)
        {
            return ImmutableList.copyOf(Iterables.transform(product.getAddonProducts(), new Function<AddonProduct, ProductArtifact>()
            {
                @Override
                public ProductArtifact apply(final AddonProduct input)
                {
                    if (input.getProductId().equals("jira-software"))
                    {
                        return new ProductArtifact("com.atlassian.jira", "jira-software-obr-dist", input.getVersion());
                    }
                    else if (input.getProductId().equals("servicedesk"))
                    {
                        return new ProductArtifact("com.atlassian.servicedesk", "jira-servicedesk", input.getVersion());
                    }
                    else
                    {
                        throw new RuntimeException("Unknown addon product: " + input.getProductId());
                    }
                }
            }));
        }
    }
}
