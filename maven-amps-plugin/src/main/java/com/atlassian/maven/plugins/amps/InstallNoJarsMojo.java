package com.atlassian.maven.plugins.amps;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 */
@Mojo(name="install-no-jars")
public class InstallNoJarsMojo extends AbstractAmpsMojo
{
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getMavenGoals().installNoJars();
    }
}
