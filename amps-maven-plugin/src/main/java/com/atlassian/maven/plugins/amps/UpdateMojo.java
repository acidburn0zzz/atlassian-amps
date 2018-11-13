package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.util.ZipUtils;
import com.atlassian.maven.plugins.updater.LocalSdk;
import com.atlassian.maven.plugins.updater.SdkPackageType;
import com.atlassian.maven.plugins.updater.SdkResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Downloads the latest version of the SDK and installs it into ATLAS_HOME.
 */
@Mojo(name = "update", requiresProject = false)
public class UpdateMojo extends AbstractAmpsMojo {

    private static final String INSTALLTYPE_FILE_NAME = "installtype.txt";

    @Component
    private SdkResource sdkResource;

    @Component
    private LocalSdk localSdk;

    /**
     * The version to update the SDK to (defaults to latest)
     */
    @Parameter(property = "update.version")
    private String updateVersion;

    /**
     * If present, use this file as the SDK archive instead of trying to download it from MPAC.
     */
    @Parameter(property = "sdk.archive.path")
    private String sdkArchivePath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        SdkPackageType packageType = localSdk.sdkPackageType();
        checkUpdatePreconditions(packageType);

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
                    updateVersion : sdkResource.getLatestSdkVersion(packageType);
            String ourVersion = getAmpsPluginVersion();
            
            if(ourVersion.equals(downloadVersion))
            {
                getLog().info("SDK is already at the latest version: " + ourVersion);
                return;
            }
            
            getLog().info("Downloading SDK version " + downloadVersion + " from marketplace.atlassian.com...");
            sdkArchive = sdkResource.downloadSdk(packageType, downloadVersion);
            getLog().info("Download complete.");
            getLog().debug("SDK download artifact at " + sdkArchive.getAbsolutePath());
        }

        getLog().info("Beginning upgrade of SDK.");
        if (packageType == SdkPackageType.TGZ) {
            installSdkFromTarGz(sdkArchive);
        } else {
            installSdkFromExecutable(sdkArchive, packageType);
        }
        getLog().info("SDK upgrade successful.");
    }

    private void installSdkFromTarGz(File sdkZip) throws MojoExecutionException {
        String sdkHome = localSdk.sdkHomeDir();
        try {
            ZipUtils.untargz(sdkZip, sdkHome, 1); // skip first directory path of artifact name/version
        } catch (IOException e) {
            throw new MojoExecutionException("Error extracting new SDK", e);
        }
    }

    private void installSdkFromExecutable(File sdkInstaller, SdkPackageType packageType)
            throws MojoExecutionException {
        sdkInstaller.setExecutable(true);

        List<String> commands = new ArrayList<>();
        Collections.addAll(commands, packageType.installCommands());
        commands.add(sdkInstaller.getAbsolutePath());

        ProcessBuilder installer = new ProcessBuilder(commands);
        try {
            Process p = installer.start();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                 BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                String line;
                getLog().info("Output from installer process follow:");
                while ((line = in.readLine()) != null) {
                    getLog().info(line);
                }

                if (err.ready()) {
                    getLog().error("Errors from installer process follow:");
                    while ((line = err.readLine()) != null) {
                        getLog().error(line);
                    }
                }
            }

            try {
                if (p.waitFor() != 0) {
                    throw new MojoExecutionException("Installer failed; see above for errors.");
                }
            } catch (InterruptedException e) {
                throw new MojoExecutionException("Subprocess installer interrupted", e);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("error from installer subprocess", e);
        }
    }

    private void checkUpdatePreconditions(SdkPackageType packageType) throws MojoExecutionException {
        if (packageType == SdkPackageType.TGZ) {
            // we're about to overwrite an existing tar.gz. Make sure the directory
            // is defined by the atlas-update script and is writable.
            String sdkHome = localSdk.sdkHomeDir();
            if (sdkHome == null) {
                throw new MojoExecutionException("SDK update must be run from the atlas-update script.");
            }
            File sdkHomeDir = new File(sdkHome);
            if (!sdkHomeDir.exists() || !sdkHomeDir.canWrite()) {
                throw new MojoExecutionException("To update successfully, SDK home directory " + sdkHome +
                    " must be writable by the current user. The current user does not have appropriate permissions.");
            }
            getLog().debug("Detected current SDK install from ATLAS_HOME in " + sdkHomeDir.getAbsolutePath());
        }
    }

}
