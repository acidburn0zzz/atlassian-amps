package com.atlassian.maven.plugins.amps;

import java.util.HashSet;
import java.util.Set;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "validate-banned-dependencies", requiresDependencyResolution = ResolutionScope.COMPILE)
public class ValidateBannedDependenciesMojo extends AbstractAmpsMojo {

    @Parameter(property = "skipBanningDependencies", defaultValue = "false")
    private boolean skipBanningDependencies;

    @Parameter(property = "banningExcludes")
    private Set<String> banningExcludes = new HashSet<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipBanningDependencies) {
            getLog().info("dependencies validation skipped");
        } else {
            getMavenGoals().validateBannedDependencies(banningExcludes);
        }
    }
}
