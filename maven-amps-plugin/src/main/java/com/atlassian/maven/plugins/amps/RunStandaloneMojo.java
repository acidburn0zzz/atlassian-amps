package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.locator.DefaultModelLocator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static java.util.Collections.singletonList;

/**
 * Run the webapp without a plugin project
 */
@Mojo(name = "run-standalone", requiresProject = false)
public class RunStandaloneMojo extends AbstractProductHandlerMojo
{
    private final String
        GROUP_ID = "com.atlassian.amps",
        ARTIFACT_ID = "standalone";

    @Component
    private ProjectBuilder projectBuilder;

    private Artifact getStandaloneArtifact()
    {
        final String version = getPluginInformation().getVersion();
        return artifactFactory.createProjectArtifact(GROUP_ID, ARTIFACT_ID, version);
    }

    protected String getAmpsGoal()
    {
        return "run";
    }

    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        getUpdateChecker().check();

        promptForEmailSubscriptionIfNeeded();
        
        trackFirstRunIfNeeded();
        
        getGoogleTracker().track(GoogleAmpsTracker.RUN_STANDALONE);

        try
        {
            MavenGoals goals;
            Xpp3Dom configuration;

            goals = createMavenGoals(projectBuilder);

            /* When we run with Maven 3 the configuration from the pom isn't automatically picked up
             * by the mojo executor. Grab it manually from pluginManagement.
             */
            PluginManagement mgmt = goals.getContextProject().getBuild().getPluginManagement();
            Plugin plugin = (Plugin) mgmt.getPluginsAsMap().get("com.atlassian.maven.plugins:maven-amps-plugin");

            configuration = (Xpp3Dom) plugin.getConfiguration();

            goals.executeAmpsRecursively(getPluginInformation().getVersion(), getAmpsGoal(), configuration);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
    protected MavenGoals createMavenGoals(ProjectBuilder projectBuilder)
            throws MojoExecutionException, MojoFailureException, ProjectBuildingException,
            IOException
    {
        // overall goal here is to create a new MavenContext / MavenGoals for the standalone project
        final MavenContext oldContext = getMavenContext();

        MavenSession oldSession = oldContext.getSession();

        File base = new File("amps-standalone").getAbsoluteFile();

        ProjectBuildingRequest pbr = oldSession.getProjectBuildingRequest();

        // hack #1 from before
        pbr.setRemoteRepositories(oldSession.getCurrentProject().getRemoteArtifactRepositories());
        pbr.setPluginArtifactRepositories(oldSession.getCurrentProject().getPluginArtifactRepositories());

        pbr.getSystemProperties().setProperty("project.basedir", base.getPath());

        ProjectBuildingResult result = projectBuilder.build(getStandaloneArtifact(), false, pbr);

        final List<MavenProject> newReactor = singletonList(result.getProject());

        MavenSession newSession = oldSession.clone();
        newSession.setProjects(newReactor);

        // Horrible hack #3 from before
        result.getProject().setFile(new DefaultModelLocator().locatePom(base));

        final MavenContext newContext = oldContext.with(
            result.getProject(),
            newReactor,
            newSession);

        return new MavenGoals(newContext);
    }
}
