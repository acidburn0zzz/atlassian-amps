package com.atlassian.maven.plugins.amps.util;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor;

public interface MojoExecutorWrapper {
    /**
     * Executes the specified mojo, <i>using only the provided configuration</i>. Any configuration that has been
     * applied at the project level is <i>ignored</i>.
     *
     * @param plugin               the Maven plugin to execute
     * @param goal                 the goal (mojo) to execute
     * @param configuration        the <i>complete configuration</i> to apply
     * @param executionEnvironment the execution environment, providing access to the Maven project and session
     *                             and the plugin manager
     * @throws MojoExecutionException if executing the mojo fails
     */
    void execute(Plugin plugin, String goal, Xpp3Dom configuration,
                 MojoExecutor.ExecutionEnvironment executionEnvironment)
            throws MojoExecutionException;

    /**
     * Executes the specified mojo, after first merging the provided configuration with any configuration that has
     * been applied in the project.
     * <p>
     * In {@code org.twdata.maven:mojo-executor} 1.5.x, {@code MojoExecutor.executeMojo} would automatically merge
     * the supplied configuration into the project-level configuration, treating the supplied configuration as
     * <i>defaults</i> which could be overridden in the project. <i>That behavior was removed in 2.0.0</i>. Now
     * only the provided configuration is considered, and any project- level configuration is <i>ignored</i>.
     * <p>
     * This method essentially restores the 1.5.x behavior, taking the supplied configuration and merging it with
     * any project-level configuration prior to executing the mojo.
     *
     * @param plugin               the Maven plugin to execute
     * @param goal                 the goal (mojo) to execute
     * @param configuration        the <i>default configuration</i> to apply, which may be overridden by project-
     *                             level configuration loaded from the POM(s)
     * @param executionEnvironment the execution environment, providing access to the Maven project and session
     *                             and the plugin manager
     * @throws MojoExecutionException if the configuration cannot be merged, or if executing the mojo fails
     */
    void executeWithMergedConfig(Plugin plugin, String goal, Xpp3Dom configuration,
                                 MojoExecutor.ExecutionEnvironment executionEnvironment)
            throws MojoExecutionException;
}
