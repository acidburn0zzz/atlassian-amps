package com.atlassian.maven.plugins.amps;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "unit-test", requiresDependencyResolution = ResolutionScope.TEST)
public class UnitTestMojo extends AbstractAmpsMojo
{
    /**
     * System Properties to pass to surefire using a more familiar syntax.
     *
     * @since 3.3
     */
    @Parameter
    protected Map<String, Object> systemPropertyVariables = new HashMap<String, Object>();

    /**
     * Sets the excludedGroups element in surefire. This will allow your unit tests to be excluded
     * depending on the value of this element. See {@link http://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html#excludedGroups}
     * Defaults to null.
     *
     * @since 3.9.4
     */
    @Parameter
    protected String excludedGroups;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getMavenGoals().runUnitTests(systemPropertyVariables, excludedGroups);
    }
}