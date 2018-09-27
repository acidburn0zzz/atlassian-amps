package com.atlassian.maven.plugins.fecru;

import com.atlassian.maven.plugins.amps.PluginModuleGenerationMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 3.6
 */
@Mojo(name = "create-plugin-module", requiresDependencyResolution = ResolutionScope.COMPILE)
public class FeCruPluginModuleGenerationMojo extends PluginModuleGenerationMojo{

    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return ProductHandlerFactory.FECRU;
    }
}
