package com.atlassian.maven.plugins.amps;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Copies bundled dependencies into META-INF/lib
 */
@Mojo(name = "copy-bundled-dependencies", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class CopyBundledDependenciesMojo extends AbstractAmpsMojo
{
    @Parameter(property = "extractDependencies", defaultValue = "true")
    private Boolean extractDependencies;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (!extractDependencies)
        {
            getMavenGoals().copyBundledDependencies();
        }
        else
        {
            getMavenGoals().extractBundledDependencies();
        }
    }
}
