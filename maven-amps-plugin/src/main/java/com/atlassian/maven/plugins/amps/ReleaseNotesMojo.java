package com.atlassian.maven.plugins.amps;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "release-notes",requiresDependencyResolution = ResolutionScope.TEST)
public class ReleaseNotesMojo extends AbstractAmpsMojo
{

    @Parameter(property = "release.project.key")
    private String projectKey;

    @Parameter(property = "release.space")
    private String space;

    @Parameter(property = "release.endpoint.url")
    private String urlEndpoint;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getMavenGoals().releaseNotes(projectKey, space, urlEndpoint);
    }
}