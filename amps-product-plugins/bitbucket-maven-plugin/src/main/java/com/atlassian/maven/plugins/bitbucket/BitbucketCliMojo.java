package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.cli.CliMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 6.1.0
 */
@Mojo(name = "cli", requiresDependencyResolution = ResolutionScope.TEST)
public class BitbucketCliMojo extends CliMojo
{
    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return ProductHandlerFactory.BITBUCKET;
    }
}
