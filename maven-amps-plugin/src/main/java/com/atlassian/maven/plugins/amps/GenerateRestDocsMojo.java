package com.atlassian.maven.plugins.amps;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Generates the files needed by Jersey at runtime to provide an extended WADL including docs
 * @since 3.6.1
 */
@Mojo(name = "generate-rest-docs", requiresDependencyResolution = ResolutionScope.TEST)
public class GenerateRestDocsMojo extends AbstractAmpsMojo
{
    @Parameter(property = "rest.docs.generation.skip")
    protected boolean restDocsGenerationSkip = false;

    @Parameter
    private String jacksonModules;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (restDocsGenerationSkip) {
            getLog().info("Skipping generation of the REST docs");
        } else {
            getMavenGoals().generateRestDocs(jacksonModules);
        }
    }
}
