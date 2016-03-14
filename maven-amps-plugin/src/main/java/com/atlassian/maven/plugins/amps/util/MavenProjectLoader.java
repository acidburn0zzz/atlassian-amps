package com.atlassian.maven.plugins.amps.util;

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.project.*;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.building.*;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.io.File;
import java.util.List;
import java.util.Properties;

public class MavenProjectLoader {

    private final MavenExecutionRequest mer;
    private final ArtifactRepository localRepository;
    private final List<ArtifactRepository> remoteRepositories;
    private final MavenExecutionRequestPopulator executionRequestPopulator;
    private final DefaultMaven maven;
    private final RepositorySystem repositorySystem;
    private final SettingsBuilder settingsBuilder;
    private final ProjectBuilder projectBuilder;
    private static File settingsXml = new File(new File(System.getProperty("user.home")), ".m2/settings.xml");

    public MavenProjectLoader(ArtifactRepository localRepository, List<ArtifactRepository> remoteRepositories, PlexusContainer plexus) throws
            ComponentLookupException,
            SettingsBuildingException,
            MavenExecutionRequestPopulationException {

        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;

        executionRequestPopulator = plexus.lookup(MavenExecutionRequestPopulator.class);
        maven = (DefaultMaven) plexus.lookup(Maven.class);
        repositorySystem = plexus.lookup(RepositorySystem.class);
        settingsBuilder = plexus.lookup(SettingsBuilder.class);
        projectBuilder = plexus.lookup(ProjectBuilder.class);

        mer = createMavenExecutionRequest(settingsXml);
    }

    public MavenProject loadMavenProject(Artifact pomArtifact, boolean dependencies, boolean plugins) {
        ProjectBuildingRequest projectBuildingRequest = mer.getProjectBuildingRequest();
        projectBuildingRequest.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL);
        projectBuildingRequest.setResolveDependencies(dependencies);
        projectBuildingRequest.setProcessPlugins(plugins);
        projectBuildingRequest.setRepositoryMerging(ProjectBuildingRequest.RepositoryMerging.REQUEST_DOMINANT);
        projectBuildingRequest.setRepositorySession(maven.newRepositorySession(mer));
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

    private MavenExecutionRequest createMavenExecutionRequest(File settingsXmlLocation) throws SettingsBuildingException, MavenExecutionRequestPopulationException {
        MavenExecutionRequest req = new DefaultMavenExecutionRequest();

        req.setLocalRepository(localRepository);
        req.setLocalRepositoryPath(localRepository.getBasedir());
        req.setRemoteRepositories(remoteRepositories);
        req.setUserSettingsFile(settingsXmlLocation);
        req.setSystemProperties(System.getProperties());

        SettingsBuildingResult settingsResult = settings(req.getUserSettingsFile());
        executionRequestPopulator.populateFromSettings( req, settingsResult.getEffectiveSettings() );
        repositorySystem.injectAuthentication(remoteRepositories, req.getServers());
        return req;
    }

    private SettingsBuildingResult settings(File settings) throws SettingsBuildingException {
        SettingsBuildingRequest settingsRequest = new DefaultSettingsBuildingRequest();
        settingsRequest.setUserSettingsFile( settings );
        settingsRequest.setSystemProperties( System.getProperties() );
        settingsRequest.setUserProperties(new Properties());
        SettingsBuildingResult settingsResult = settingsBuilder.build( settingsRequest );
        return settingsResult;
    }
}
