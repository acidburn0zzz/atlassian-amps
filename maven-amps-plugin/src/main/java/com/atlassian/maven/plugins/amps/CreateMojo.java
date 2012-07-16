package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Creates a new plugin
 */
@Mojo(name = "create", requiresProject = false)
public class CreateMojo extends AbstractProductAwareMojo
{
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getGoogleTracker().track(GoogleAmpsTracker.CREATE_PLUGIN);

        getUpdateChecker().check();

        getMavenGoals().createPlugin(getProductId());
    }

    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return "all";
    }
}
