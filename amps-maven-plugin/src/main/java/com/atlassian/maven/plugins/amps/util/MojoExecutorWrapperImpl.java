package com.atlassian.maven.plugins.amps.util;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.twdata.maven.mojoexecutor.MavenCompatibilityHelper;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.util.List;

/**
 * A wrapper around Maven's {@link MojoExecutor}. Adds support for merging default goal configuration with configuration
 * explicitly specified for a given goal in the built project's POM.
 *
 * @since 8.0 (as static util class MojoUtils)
 * @since 8.2 (as a component)
 */
public class MojoExecutorWrapperImpl implements MojoExecutorWrapper {

    @Override
    public void execute(Plugin plugin, String goal, Xpp3Dom configuration,
                        MojoExecutor.ExecutionEnvironment executionEnvironment)
            throws MojoExecutionException {
        MojoExecutor.executeMojo(plugin, goal, configuration, executionEnvironment);
    }

    @Override
    public void executeWithMergedConfig(Plugin plugin, String goal, Xpp3Dom configuration,
                                        MojoExecutor.ExecutionEnvironment executionEnvironment)
            throws MojoExecutionException {
        // If the target plugin has configuration applied, we have to _manually_ supply that configuration in
        // MojoExecutor 2+ (1.5.x would find the configuration and apply it automatically; 2.x no longer does).
        Xpp3Dom mergedConfig = mergeConfig(plugin, goal, configuration, executionEnvironment);

        execute(plugin, goal, mergedConfig, executionEnvironment);
    }

    /**
     * Gets the project-level configuration for the specified mojo.
     *
     * @param plugin  the Maven plugin to retrieve configuration for
     * @param goal    the specific goal (mojo) to retrieve configuration for
     * @param project the Maven project
     * @return the project-level configuration, which may be {@code null} per Maven's API
     */
    private Xpp3Dom getGoalConfig(Plugin plugin, String goal, MavenProject project) {
        String executionId;

        int index = goal.indexOf('#');
        if (index == -1) {
            executionId = null;
        } else {
            executionId = goal.substring(index + 1);
            goal = goal.substring(0, index);
        }

        return project.getGoalConfiguration(plugin.getGroupId(), plugin.getArtifactId(), executionId, goal);
    }

    /**
     * Retrieves a list of parameters that are supported by the specified mojo, which can be used to filter project-
     * level configuration to ensure only elements relevant to the mojo being executed are provided.
     * <p>
     * When configuring a Maven plugin, it's not uncommon to provide a single {@code <configuration/>} block which
     * configures multiple goals. (That pattern is particularly common for AMPS configuration.) However, when using
     * {@code executeMojo}, that means simply passing in the entire project-level configuration can result in Maven
     * errors for unsupported parameters. Using the mojo descriptor's parameters, it's possible to filter out those
     * unexpected parameters before executing the mojo.
     *
     * @param plugin               the Maven plugin to load a descriptor for
     * @param goal                 the goal (mojo) to retrieve parameters for from the plugin's descriptor
     * @param executionEnvironment the execution environment, providing access to the Maven project and session
     *                             and the plugin manager
     * @return the specified goal's parameters, which may be {@code null} per Maven's API
     * @throws MojoExecutionException if the Maven plugin's descriptor cannot be loaded
     */
    private List<Parameter> getGoalParameters(Plugin plugin, String goal,
                                                     MojoExecutor.ExecutionEnvironment executionEnvironment)
            throws MojoExecutionException
    {
        PluginDescriptor pluginDescriptor;
        try {
            pluginDescriptor = MavenCompatibilityHelper.loadPluginDescriptor(plugin,
                    executionEnvironment, executionEnvironment.getMavenSession());
        } catch (MojoExecutionException e) {
            // Don't wrap MojoExecutionExceptions; they're already compatible with our throws clause
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to load descriptor for " +
                    plugin.getGroupId() + ":" + plugin.getArtifactId(), e);
        }

        return pluginDescriptor.getMojo(goal).getParameters();
    }

    /**
     * Merges the provided {@code defaultConfig} with any project-level configuration, filters the merged configuration
     * using the {@link #getGoalParameters mojo's supported parameters} and returns the result.
     *
     * @param plugin               the Maven plugin to merge configuration for
     * @param goal                 the goal (mojo) to merge configuration for
     * @param defaultConfig        the <i>default</i> configuration to apply, which may be overridden by any project-
     *                             level configuration applied for the plugin or mojo
     * @param executionEnvironment the execution environment, providing access to the Maven project and session
     *                             and the plugin manager
     * @return the merged and filtered configuration
     * @throws MojoExecutionException if the Maven plugin's descriptor cannot be loaded
     */
    private Xpp3Dom mergeConfig(Plugin plugin, String goal, Xpp3Dom defaultConfig,
                                       MojoExecutor.ExecutionEnvironment executionEnvironment)
            throws MojoExecutionException {
        Xpp3Dom goalConfig = getGoalConfig(plugin, goal, executionEnvironment.getMavenProject());
        if (goalConfig == null) {
            // The goal has not been configured in the project, so just use the provided configuration
            return defaultConfig;
        }

        // This probably seems backward, but it's this way for historical reasons. The <configuration/> passed
        // to executeMojo is treated as _default_ configuration and is overridden by any <configuration/> that
        // was applied in the project
        Xpp3Dom merged = Xpp3DomUtils.mergeXpp3Dom(goalConfig, defaultConfig, true);

        // For AMPS plugins it's common to have a "common" configuration that handles multiple goals' worth of
        // configuration. We need to load the MojoDescriptor and filter the shared configuration to include
        // only values relevant to the target goal
        List<Parameter> parameters = getGoalParameters(plugin, goal, executionEnvironment);
        if (parameters == null || parameters.isEmpty()) {
            // The mojo being executed doesn't appear to support any parameters; assume the provided
            // configuration is "correct" as-is
            return merged;
        }

        // Loop over the parameters the targeted mojo _supports_, and copy over any configuration elements
        // that target those parameters (or their aliases)
        Xpp3Dom finalConfig = MojoExecutor.configuration();
        for (Parameter parameter : parameters) {
            Xpp3Dom element = merged.getChild(parameter.getName());
            if (element == null && parameter.getAlias() != null) {
                element = merged.getChild(parameter.getAlias());
            }

            if (element != null) {
                finalConfig.addChild(element);
            }
        }

        return finalConfig;
    }
}
