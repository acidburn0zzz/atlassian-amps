package com.atlassian.maven.plugins.amps.util;

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.project.*;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public class MavenProjectLoader {

    private final MavenExecutionRequest executionRequest;
    private final DefaultMaven maven;
    private final ProjectBuilder projectBuilder;

    public MavenProjectLoader(MavenSession session) throws ComponentLookupException {
        executionRequest = session.getRequest();
        PlexusContainer plexus = session.getContainer();
        maven = (DefaultMaven) plexus.lookup(Maven.class);
        projectBuilder = plexus.lookup(ProjectBuilder.class);
    }

    public MavenProject loadMavenProject(Artifact pomArtifact, boolean dependencies, boolean plugins) {
        ProjectBuildingRequest projectBuildingRequest = executionRequest.getProjectBuildingRequest();
        projectBuildingRequest.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL);
        projectBuildingRequest.setResolveDependencies(dependencies);
        projectBuildingRequest.setProcessPlugins(plugins);
        projectBuildingRequest.setRepositoryMerging(ProjectBuildingRequest.RepositoryMerging.REQUEST_DOMINANT);
        projectBuildingRequest.setRepositorySession(maven.newRepositorySession(executionRequest));
        ProjectBuildingResult ret;
        try {
            ret = projectBuilder.build(pomArtifact, projectBuildingRequest);
            MavenProject mp = ret.getProject();
            if (mp != null) {
                return mp;
            }
        } catch (ProjectBuildingException ex) {
            System.out.println("ERROR");
        }
        return null;
    }
}
