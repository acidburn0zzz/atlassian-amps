package com.atlassian.maven.plugins.amps;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 */
@Mojo(name="mvn-deploy")
public class MavenDeployMojo extends AbstractAmpsMojo
{
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getMavenGoals().mvnDeploy();
    }
}
