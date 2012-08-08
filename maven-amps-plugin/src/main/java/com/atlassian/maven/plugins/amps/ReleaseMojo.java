package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "release")
public class ReleaseMojo extends AbstractProductHandlerMojo
{
    @Parameter(property = "maven.args", defaultValue = "")
    private String mavenArgs;

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        String trackingLabel = getPluginInformation().getId() + ":" + getPluginInformation().getVersion();
        trackFirstRunIfNeeded();
        getGoogleTracker().track(GoogleAmpsTracker.RELEASE,trackingLabel);

        getMavenGoals().release(mavenArgs);
    }
}
