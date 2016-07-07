package com.atlassian.maven.plugins.amps.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Optional;

public class MavenProjectLoader
{
    /**
     * Resolve a given artifact with its dependencies
     *
     * @param session
     * @param project
     * @param pomArtifact
     * @return  Optional.of(MavenProject) if the maven project for the given artifact and session can be resolved.
     *          Optional.empty() otherwise
     */
    public Optional<MavenProject> loadMavenProject(MavenSession session, MavenProject project, Artifact pomArtifact)
            throws MojoExecutionException
    {
        try
        {
            ProjectBuilder projectBuilder = session.getContainer().lookup(ProjectBuilder.class);
            ProjectBuildingRequest request = new DefaultProjectBuildingRequest(project.getProjectBuildingRequest())
                    .setRemoteRepositories(project.getRemoteArtifactRepositories());
            return Optional.ofNullable(projectBuilder.build(pomArtifact, request).getProject());
        }
        catch (ComponentLookupException e)
        {
            throw new MojoExecutionException("Could not get the ProjectBuilder from the maven session", e);
        }
        catch (ProjectBuildingException e)
        {
            throw new MojoExecutionException(String.format("Could not build the MavenProject for %s", pomArtifact), e);
        }
    }
}
