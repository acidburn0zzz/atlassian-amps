package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.PluginModuleGenerationMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 6.1.0
 */
@Mojo(name = "create-plugin-module", requiresDependencyResolution = ResolutionScope.COMPILE)
public class BitbucketPluginModuleGenerationMojo extends PluginModuleGenerationMojo
{
    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return ProductHandlerFactory.BITBUCKET;
    }
}
