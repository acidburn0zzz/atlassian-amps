package com.atlassian.maven.plugins.ampsdispatcher;

import java.util.List;
import java.util.Optional;

import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.util.MojoExecutorWrapper;

import com.google.common.collect.ImmutableList;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static java.util.Optional.empty;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;

/**
 * Dispatches to the appropriate amps product-specific plugin by detecting the plugin in the project.
 *
 * @since 3.0-beta2
 */
public abstract class AbstractAmpsDispatcherMojo extends AbstractMojo {

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

    @Component
    MojoExecutorWrapper mojoExecutorWrapper;

    public final void execute() throws MojoExecutionException, MojoFailureException {
        String goal = session.getGoals().stream()
                // We only pass in the first goal since we know the shell scripts only pass in one goal
                .findFirst()
                .map(AbstractAmpsDispatcherMojo::determineGoal)
                .orElseThrow(() -> new MojoFailureException("No goals were specified to dispatch"));

        Plugin plugin = findProductPlugin()
                .orElseThrow(() -> new MojoFailureException("Couldn't detect an AMPS plugin to dispatch to"));

        mojoExecutorWrapper.executeWithMergedConfig(plugin, goal, configuration(),
                executionEnvironment(project, session, buildPluginManager));
    }

    static String determineGoal(String goal) {
        return goal.substring(goal.lastIndexOf(":") + 1);
    }

    Optional<Plugin> findProductPlugin() {
        List<Plugin> plugins = project.getBuildPlugins();
        if (plugins == null) {
            return empty();
        }

        List<String> possiblePluginTypes = ImmutableList.<String>builder()
                .addAll(ProductHandlerFactory.getIds())
                .add("amps")
                .build();

        return plugins.stream()
                .filter(pomPlugin -> "com.atlassian.maven.plugins".equals(pomPlugin.getGroupId()) &&
                        possiblePluginTypes.stream()
                                .anyMatch(type -> (type + "-maven-plugin").equals(pomPlugin.getArtifactId())))
                .findFirst();
    }
}
