package com.atlassian.maven.plugins.amps;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Shadows the real maven-deploy-plugin:deploy goal, but does some prep-work before and after the execution of the
 * install because of AMPS-1042.
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
