package com.atlassian.maven.plugins.stash;

import com.atlassian.maven.plugins.amps.pdk.TestInstallMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @since 3.8
 */
public class StashTestInstallMojo extends TestInstallMojo
{
    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return ProductHandlerFactory.STASH;
    }
}
