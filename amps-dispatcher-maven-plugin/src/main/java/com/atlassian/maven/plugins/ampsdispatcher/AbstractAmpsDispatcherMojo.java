package com.atlassian.maven.plugins.ampsdispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.util.MojoUtils;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;

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

    @Component
    BuildPluginManager buildPluginManager;

    public final void execute() throws MojoExecutionException, MojoFailureException
    {
        List<String> goals = session.getGoals();
        if (goals.size() < 1)
        {
            throw new MojoFailureException("No goals were specified to dispatch");
        }

        Plugin plugin = findProductPlugin();
        if (plugin == null)
        {
            throw new MojoFailureException("Couldn't detect an AMPS plugin to dispatch to");
        }

        // We only pass in the first goal since we know the shell scripts only pass in one goal
        String goal = determineGoal(goals.get(0));

        MojoUtils.executeWithMergedConfig(plugin, goal, configuration(),
                executionEnvironment(project, session, buildPluginManager));
    }

    static String determineGoal(String goal)
    {
        return goal.substring(goal.lastIndexOf(":") + 1);
    }

    Plugin findProductPlugin()
    {
        List<Plugin> plugins = project.getBuildPlugins();
        if (plugins != null)
        {
            Collection<String> productIds = ProductHandlerFactory.getIds();

            List<String> possiblePluginTypes = new ArrayList<>(productIds.size() + 1);
            possiblePluginTypes.addAll(productIds);
            possiblePluginTypes.add("amps");

            for (Plugin pomPlugin : plugins)
            {
                if ("com.atlassian.maven.plugins".equals(pomPlugin.getGroupId()))
                {
                    for (String type : possiblePluginTypes)
                    {
                        if ((type + "-maven-plugin").equals(pomPlugin.getArtifactId()))
                        {
                            return pomPlugin;
                        }
                    }
                }
            }
        }

        return null;
    }
}
