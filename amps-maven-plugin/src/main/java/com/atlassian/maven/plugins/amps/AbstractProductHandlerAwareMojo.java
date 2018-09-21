package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.product.ProductHandler;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;

public abstract class AbstractProductHandlerAwareMojo extends AbstractProductAwareMojo
{
    /**
     * The artifact factory is used to create valid Maven
     * {@link org.apache.maven.artifact.Artifact} objects. This is used to pass Maven artifacts to
     * the artifact resolver so that it can download the required JARs to put in the embedded
     * container's classpaths.
     */
    @Component
    protected ArtifactFactory artifactFactory;
    
    /**
     * Create a ProductHandler
     * @param productId the product nickname (not the instance id)
     * @return a product handler for this nickname
     * @throws MojoExecutionException
     */
    protected ProductHandler createProductHandler(String productId)
    {
        return ProductHandlerFactory.create(productId, getMavenContext(), getMavenGoals(),artifactFactory);
    }
}
