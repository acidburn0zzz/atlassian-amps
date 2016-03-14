package com.atlassian.maven.plugins.amps.util;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.ProductArtifact;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.metadata.*;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.List;

public class ArtifactRetriever
{
    private final ArtifactResolver artifactResolver;
    private final ArtifactFactory artifactFactory;
    private final ArtifactRepository localRepository;
    private final List<ArtifactRepository> remoteRepositories;
    private final RepositoryMetadataManager repositoryMetadataManager;

    public ArtifactRetriever(ArtifactResolver artifactResolver, ArtifactFactory artifactFactory, ArtifactRepository localRepository, List<ArtifactRepository> remoteRepositories, RepositoryMetadataManager repositoryMetadataManager)
    {
        this.artifactResolver = artifactResolver;
        this.artifactFactory = artifactFactory;
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
        this.repositoryMetadataManager = repositoryMetadataManager;
    }

    public String resolve(ProductArtifact dependency) throws MojoExecutionException
    {
        Artifact artifact = this.artifactFactory.createArtifact(dependency.getGroupId(),
        dependency.getArtifactId(), dependency.getVersion(), "compile", "jar");
        try
        {
            this.artifactResolver.resolve(artifact, remoteRepositories, localRepository);
        }
        catch (ArtifactResolutionException e)
        {
            throw new MojoExecutionException("Cannot resolve artifact", e);
        }
        catch (ArtifactNotFoundException e)
        {
            throw new MojoExecutionException("Cannot find artifact", e);
        }
        return artifact.getFile().getPath();
    }

    public MavenProjectLoader getMavenProjectLoader(MavenContext context) throws MojoExecutionException {
        try {
            return new MavenProjectLoader(localRepository, remoteRepositories, context.getExecutionEnvironment().getMavenSession());
        } catch (ComponentLookupException e) {
            throw new MojoExecutionException("Error while performing a lookup on the PlexusContainer", e);
        } catch (SettingsBuildingException e) {
            throw new MojoExecutionException("Error while building the Maven settings", e);
        } catch (MavenExecutionRequestPopulationException e) {
            throw new MojoExecutionException("Error while populating the MavenExecutionRequest", e);
        }
    }

    public String getLatestStableVersion(Artifact artifact) throws MojoExecutionException
    {
        RepositoryMetadata metadata;
        
        if(!artifact.isSnapshot() || Artifact.LATEST_VERSION.equals(artifact.getBaseVersion()) || Artifact.RELEASE_VERSION.equals(artifact.getBaseVersion()))
        {
            metadata = new ArtifactRepositoryMetadata(artifact);
        }
        else
        {
            metadata = new SnapshotArtifactRepositoryMetadata(artifact);
        }

        try
        {
            repositoryMetadataManager.resolve( metadata, remoteRepositories, localRepository );
            artifact.addMetadata( metadata );

            Metadata repoMetadata = metadata.getMetadata();
            String version = null;

            if(repoMetadata != null && repoMetadata.getVersioning() != null)
            {
                version = constructVersion(repoMetadata.getVersioning());
            }

            if (version == null)
            {
                // use the local copy, or if it doesn't exist - go to the remote repo for it
                version = artifact.getBaseVersion();
            }
            
            return version;
        }
        catch (RepositoryMetadataResolutionException e)
        {
            throw new MojoExecutionException("Error resolving stable version", e);
        }

    }

    private String constructVersion(Versioning versioning)
    {
        List<String> versions = versioning.getVersions();
        DefaultArtifactVersion latestVersion = null;
        for(String version : versions)
        {
            DefaultArtifactVersion artifactVersion = new DefaultArtifactVersion(version);
            if(StringUtils.isNotBlank(artifactVersion.getQualifier()))
            {
                continue;
            }
            
            if(null == latestVersion)
            {
                latestVersion = artifactVersion;
            }
            else if(artifactVersion.compareTo(latestVersion) > 0)
            {
                latestVersion = artifactVersion;
            }
        }
        
        if(null != latestVersion)
        {
            return latestVersion.toString();
        }
        else
        {
            return null;
        }
    }
}
