package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.util.OSUtils;
import com.atlassian.maven.plugins.amps.util.VersionUtils;
import com.atlassian.maven.plugins.amps.util.ZipUtils;
import com.atlassian.maven.plugins.updater.SdkResource;
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

    @Component
    private SdkResource sdkResource;

    /**
     * The version to update the SDK to (defaults to latest)
     */
    @Parameter(property = "update.version")
    private String updateVersion;

    /**
     * If present, use this file as the SDK archive instead of trying to download it from PAC.
     */
    @Parameter(property = "sdk.archive.path")
    private String sdkArchivePath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        checkUpdatePreconditions();

        File sdkArchive;
        if (StringUtils.isNotBlank(sdkArchivePath)) {
            // use local file for SDK update
            sdkArchive = new File(sdkArchivePath);
            if (!sdkArchive.isFile() || !sdkArchive.canRead()) {
                throw new MojoExecutionException("Can't read archive file at " + sdkArchivePath);
            }
            getLog().info("Using local file " + sdkArchive.getAbsolutePath() + " for SDK install.");
        } else {
            // determine which version to download from PAC
            String downloadVersion = StringUtils.isNotBlank(updateVersion) ?
                    updateVersion : sdkResource.getLatestSdkVersion();
            getLog().info("Downloading SDK version " + downloadVersion + " from marketplace.atlassian.com...");
            sdkArchive = sdkResource.downloadSdk(downloadVersion);
            getLog().info("Download complete.");
            getLog().debug("SDK download artifact at " + sdkArchive.getAbsolutePath());
        }

        getLog().info("Beginning upgrade of SDK.");
        installSdk(sdkArchive);
        getLog().info("SDK upgrade successful.");
    }

    private void installSdk(File sdkZip) throws MojoExecutionException {
        String sdkHome = getSdkHome();
        try {
            if (OSUtils.OS == OSUtils.OS.WINDOWS) {
                ZipUtils.unzip(sdkZip, sdkHome, 1); // skip first directory path of artifact name/version
            } else {
                ZipUtils.untargz(sdkZip, sdkHome, 1);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error extracting new SDK", e);
        }
    }

    private void checkUpdatePreconditions() throws MojoExecutionException {
        String sdkHome = getSdkHome();
        if (sdkHome == null) {
            throw new MojoExecutionException("SDK update must be run from the atlas-update script.");
        }
        File sdkHomeDir = new File(sdkHome);
        if (!sdkHomeDir.exists() || !sdkHomeDir.canWrite()) {
            throw new MojoExecutionException("To update successfully, SDK home directory " + sdkHome +
                " must be writable by the current user.");
        }
        getLog().debug("Detected current SDK install from ATLAS_HOME in " + sdkHomeDir.getAbsolutePath());
    }

    private String getSdkHome() {
        return System.getenv("ATLAS_HOME");
    }
}
