package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.ReleaseMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @since 3.10
 */
@Mojo(name = "release")
public class BitbucketReleaseMojo extends ReleaseMojo
{
    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return ProductHandlerFactory.BITBUCKET;
    }
}
