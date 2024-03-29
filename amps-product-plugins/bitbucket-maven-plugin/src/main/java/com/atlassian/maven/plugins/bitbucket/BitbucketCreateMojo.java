package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.CreateMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @since 6.1.0
 */
@Mojo(name = "create", requiresProject = false)
public class BitbucketCreateMojo extends CreateMojo
{
    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return ProductHandlerFactory.BITBUCKET;
    }
}
