package com.atlassian.maven.plugins.stash;

import org.apache.maven.plugin.MojoExecutionException;

import com.atlassian.maven.plugins.amps.StopMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;

/**
 * @since 3.8
 */
public class StashStopMojo extends StopMojo
{
    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return ProductHandlerFactory.STASH;
    }
}
