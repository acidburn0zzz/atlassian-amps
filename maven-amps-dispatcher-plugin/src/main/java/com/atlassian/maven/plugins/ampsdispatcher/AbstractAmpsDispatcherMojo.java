package com.atlassian.maven.plugins.ampsdispatcher;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoComponent;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;

import com.atlassian.maven.plugins.amps.util.MavenPropertiesUtils;
import com.atlassian.maven.plugins.amps.util.VersionUtils;

/**
 * Dispatches to the appropriate amps product-specific plugin by detecting the plugin in the project.
 *
 * @since 3.0-beta2
 */
public abstract class AbstractAmpsDispatcherMojo extends AbstractMojo
{

    /**
     * The Maven Project Object
     */
    @MojoParameter(expression = "${project}", required = true, readonly = true)
    MavenProject project;

    /**
     * The Maven Session Object
     */
    @MojoParameter(expression = "${session}", required = true, readonly = true)
    MavenSession session;

    /**
     * The Maven PluginManager Object
     */
    @MojoComponent
    PluginManager pluginManager;

    public final void execute() throws MojoExecutionException, MojoFailureException
    {
        String targetArtifact = MavenPropertiesUtils.detectAmpsProduct(project);

        if (targetArtifact != null && session.getGoals().size() > 0)
        {
            // We only pass in the first goal since we know the shell scripts only pass in one goal
            String goal = determineGoal();

            executeMojo(
                plugin(
                        groupId("com.atlassian.maven.plugins"),
                        artifactId(targetArtifact),
                        version(VersionUtils.getVersion())  //ignored anyway
                ),
                goal(goal),
                configuration(),
                executionEnvironment(project, session, pluginManager));
        }
        else
        {
            throw new MojoFailureException("Couldn't detect an AMPS product to dispatch to");
        }
    }

    final String determineGoal()
    {
        String goal = (String) session.getGoals().get(0);
        goal = goal.substring(goal.lastIndexOf(":") + 1);
        return goal;
    }
}
