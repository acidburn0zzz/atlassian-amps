package com.atlassian.maven.plugins.amps.cli;

import com.atlassian.maven.plugins.amps.AbstractProductAwareMojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "cli")
public class CliMojo extends AbstractProductAwareMojo
{
    @Parameter(property = "cli.port", defaultValue = "4330")
    private int cliPort;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getMavenGoals().startCli(getPluginInformation(), cliPort);
    }
}
