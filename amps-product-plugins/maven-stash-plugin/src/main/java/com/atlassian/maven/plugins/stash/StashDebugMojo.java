package com.atlassian.maven.plugins.stash;

import com.atlassian.maven.plugins.amps.DebugMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @since 3.8
 */
public class StashDebugMojo extends DebugMojo
{
    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return ProductHandlerFactory.STASH;
    }
}
