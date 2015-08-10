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
     * Denotes test category as defined by surefire/failsafe notion of groups. In JUnit4, this affects tests annotated
     * with {@link org.junit.experimental.categories.Category @Category} annotation
     */
    @Parameter
    protected String category;

    /**
     * Sets the excludedGroups element in surefire. This will allow your unit tests to be excluded
     * depending on the value of this element. See {@link http://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html#excludedGroups}
     * Defaults to null.
     *
     * @since 4.1.5
     */
    @Parameter
    protected String excludedGroups;

    /**
     * Skip the unit tests along with any product startups
     */
    @Parameter(property = "skipUTs", defaultValue = "false")
    private boolean skipUTs = false;

    public void execute() throws MojoExecutionException, MojoFailureException
    {

        if (skipUTs)
        {
            getLog().info("Unit tests skipped");
            return;
        }

        getMavenGoals().runUnitTests(systemPropertyVariables, excludedGroups, category);
    }
}