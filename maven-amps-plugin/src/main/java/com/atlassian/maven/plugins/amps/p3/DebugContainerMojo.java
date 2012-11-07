package com.atlassian.maven.plugins.amps.p3;

import com.atlassian.maven.plugins.amps.AbstractProductAwareMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Runs the plugin via the plugins 3 container.
 *
 * <strong>This is an experimental goal and may be removed in future versions</strong>
 */
@Mojo(name = "debug-container")
@Execute(phase = LifecyclePhase.PACKAGE)
public class DebugContainerMojo extends AbstractProductAwareMojo
{
    @Parameter(property = "container.version", defaultValue = "LATEST")
    private String containerVersion;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        MavenProject project = getMavenContext().getProject();
        File pluginFile = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + ".jar");

        getMavenGoals().copyContainerToOutputDirectory(containerVersion);

        getMavenGoals().debugStandaloneContainer(pluginFile);
    }
}
