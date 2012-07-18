package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.util.VersionUtils;
import com.atlassian.maven.plugins.amps.util.ZipUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Downloads the latest version of the SDK and installs it into ATLAS_HOME.
 */
@Mojo(name = "update", requiresProject = false)
public class UpdateMojo extends AbstractAmpsMojo {

    private static final String SDK_GROUPID = "com.atlassian.amps";
    private static final String SDK_ARTIFACTID = "atlassian-plugin-sdk";
    private static final String RETRIEVE_SCOPE = "compile";
    private static final String ARTIFACT_TYPE = "zip";

    /**
     * Used to retrieve SDK artifacts.
     */
    @Component
    protected ArtifactResolver resolver;

    /**
     * The local Maven repository.
     */
    @Parameter(property = "localRepository")
    protected ArtifactRepository localRepository;

    /**
     * The remote Maven repositories used by the artifact resolver to look for SDKs.
     */
    @Parameter(property = "project.remoteArtifactRepositories")
    protected List repositories;

    /**
     * The version to update the SDK to (defaults to latest)
     */
    @Parameter(property = "update.version")
    protected String updateVersion;

    /**
     * The artifact factory is used to create valid Maven
     * {@link org.apache.maven.artifact.Artifact} objects. These objects are passed to the
     * Resolver.
     */
    @Component
    protected ArtifactFactory factory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        checkUpdatePreconditions();

        String currentVersion = getPluginInformation().getVersion();
        String latestVersion = !StringUtils.isBlank(updateVersion) ? updateVersion : VersionUtils.getLatestVersion(currentVersion);
        if (VersionUtils.versionFromString(latestVersion) >
            VersionUtils.versionFromString(currentVersion)) {
            File sdkZip = downloadLatestSdk(latestVersion);
            installSdk(sdkZip);
        }

        getLog().info("SDK upgrade successful. New version: " + latestVersion);
    }

    private File downloadLatestSdk(String latestVersion) throws MojoExecutionException {
        Artifact artifact = factory.createArtifact(
                SDK_GROUPID, SDK_ARTIFACTID, latestVersion, RETRIEVE_SCOPE, ARTIFACT_TYPE
        );
        try {
            resolver.resolve(artifact, repositories, localRepository);
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Cannot resolve SDK artifact", e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Cannot find version " + latestVersion + " of SDK", e);
        }
        return artifact.getFile();
    }

    private void installSdk(File sdkZip) throws MojoExecutionException {
        String sdkHome = System.getenv("ATLAS_HOME");
        try {
            ZipUtils.unzip(sdkZip, sdkHome, 1); // skip first directory path of artifact name/version
        } catch (IOException e) {
            throw new MojoExecutionException("Error extracting new SDK", e);
        }
    }

    private void checkUpdatePreconditions() throws MojoExecutionException {
        String sdkHome = System.getenv("ATLAS_HOME");
        if (sdkHome == null) {
            throw new MojoExecutionException("SDK update must be run from the atlas-update script.");
        }
        File sdkHomeDir = new File(sdkHome);
        if (!sdkHomeDir.exists() || !sdkHomeDir.canWrite()) {
            throw new MojoExecutionException("To update successfully, SDK home directory " + sdkHome +
                " must be writable by the current user.");
        }
    }
}
