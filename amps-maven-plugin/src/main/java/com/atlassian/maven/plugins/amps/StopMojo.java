package com.atlassian.maven.plugins.amps;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Stop the webapps started by RunMojo.
 * This may be useful when you use -Dwait=false for the RunMojo and you want
 * the products to make a clean shutdown.
 */
@Mojo(name = "stop")
public class StopMojo extends RunMojo
{
    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        final List<ProductExecution> productExecutions = getProductExecutions();
        setParallelMode(productExecutions);
        stopProducts(productExecutions);
    }
}
