package com.atlassian.maven.plugins.amps.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Optional;

public class MavenProjectLoader {

    private final MavenExecutionRequest executionRequest;
    private final ProjectBuilder projectBuilder;

    public MavenProjectLoader(MavenSession session) throws ComponentLookupException {
        executionRequest = session.getRequest();
        projectBuilder = session.getContainer().lookup(ProjectBuilder.class);
    }

    public Optional<MavenProject> loadMavenProject(Artifact pomArtifact, boolean dependencies) throws MojoExecutionException {
        ProjectBuildingRequest projectBuildingRequest = executionRequest.getProjectBuildingRequest()
                // The validation level is currently "VALIDATION_LEVEL_MAVEN_3_0". Setting it to minimal might speed things up a bit
                .setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL)
                // ResolveDependencies is currently "false". If the calling function needs dependencies, we need to tell maven about it
                .setResolveDependencies(dependencies);
        try {
            MavenProject mp = projectBuilder.build(pomArtifact, projectBuildingRequest).getProject();
            if (mp != null) {
                return Optional.of(mp);
            }
        } catch (ProjectBuildingException e) {
            throw new MojoExecutionException(String.format("Couldn't build project for %s:%s:%s", pomArtifact.getGroupId(), pomArtifact.getArtifactId(), pomArtifact.getVersion()));
        }
        return Optional.empty();
    }
}
