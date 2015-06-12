package com.atlassian.maven.plugins.bitbucket;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.atlassian.maven.plugins.amps.StopMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;

/**
 * @since 6.1.0
 */
@Mojo(name = "stop")
public class BitbucketStopMojo extends StopMojo
{
    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return ProductHandlerFactory.BITBUCKET;
    }
}
