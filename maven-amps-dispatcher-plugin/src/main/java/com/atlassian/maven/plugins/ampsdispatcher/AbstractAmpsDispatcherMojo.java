package com.atlassian.maven.plugins.ampsdispatcher;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.util.VersionUtils;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

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
    @Parameter(property = "project", required = true, readonly = true)
    MavenProject project;

    /**
     * The Maven Session Object
     */
    @Parameter(property = "session", required = true, readonly = true)
    MavenSession session;

    /**
     * The Maven PluginManager Object
     */
    @Component
    PluginManager pluginManager;

    public final void execute() throws MojoExecutionException, MojoFailureException
    {
        String targetArtifactId = detectAmpsProduct();

        if (targetArtifactId != null && session.getGoals().size() > 0)
        {
            // We only pass in the first goal since we know the shell scripts only pass in one goal
            String goal = determineGoal();

            executeMojo(
                plugin(
                        groupId("com.atlassian.maven.plugins"),
                        artifactId(targetArtifactId),
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

    final String detectAmpsProduct()
    {
        List buildPlugins = project.getBuildPlugins();

        Set<String> possiblePluginTypes = new HashSet<String>(ProductHandlerFactory.getIds());
        possiblePluginTypes.add("amps");

        if (buildPlugins != null)
        {
            for (Iterator iterator = buildPlugins.iterator(); iterator.hasNext();)
            {
                Plugin pomPlugin = (Plugin) iterator.next();

                if ("com.atlassian.maven.plugins".equals(pomPlugin.getGroupId()))
                {
                    for (String type : possiblePluginTypes)
                    {
                        if (("maven-" + type + "-plugin").equals(pomPlugin.getArtifactId()))
                        {
                            return pomPlugin.getArtifactId();
                        }
                    }
                }
            }
        }
        return null;

    }
}
