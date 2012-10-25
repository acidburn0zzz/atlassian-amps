package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.util.AmpsCreatePluginPrompter;
import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Creates a new plugin
 */
@Mojo(name = "create", requiresProject = false)
public class CreateMojo extends AbstractProductAwareMojo
{
    @Component
    private AmpsCreatePluginPrompter ampsCreatePluginPrompter;
    
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        trackFirstRunIfNeeded();

        getGoogleTracker().track(GoogleAmpsTracker.CREATE_PLUGIN);

        getMavenGoals().createPlugin(getProductId(),ampsCreatePluginPrompter);
    }

    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return "all";
    }
}
