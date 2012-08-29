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
        getUpdateChecker().check();

        trackFirstRunIfNeeded();

        getAmpsPluginVersionChecker().checkAmpsVersionInPom(getSdkVersion(),getMavenContext().getProject());
        
        getGoogleTracker().track(GoogleAmpsTracker.CREATE_PLUGIN);

        getMavenGoals().createPlugin(getProductId());
    }

    @Override
    protected String getDefaultProductId() throws MojoExecutionException
    {
        return "all";
    }
}
