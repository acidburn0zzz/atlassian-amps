package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.AbstractAmpsMojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Copies bundled dependencies into META-INF/lib for test plugin
 */
@Mojo(name = "copy-test-bundled-dependencies", requiresDependencyResolution = ResolutionScope.TEST)
public class CopyTestBundledDependenciesMojo extends AbstractProductAwareMojo
{
    @Parameter(property = "extractTestDependencies", defaultValue = "false")
    private Boolean extractTestDependencies;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if(shouldBuildTestPlugin())
        {
            if (!extractTestDependencies)
            {
                if(excludeAllTestDependencies)
                {
                    getMavenGoals().copyTestBundledDependenciesExcludingTestScope(testBundleExcludes);
                }
                else
                {
                    getMavenGoals().copyTestBundledDependencies(testBundleExcludes);
                }
                
            }
            else
            {
                if(excludeAllTestDependencies)
                {
                    getMavenGoals().extractTestBundledDependenciesExcludingTestScope(testBundleExcludes);
                }
                else
                {
                    getMavenGoals().extractTestBundledDependencies(testBundleExcludes);
                }
            }
        }
    }
}
