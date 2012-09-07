package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.util.ZipUtils;
import com.atlassian.maven.plugins.updater.SdkPackageType;
import com.atlassian.maven.plugins.updater.SdkResource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
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
        SdkPackageType packageType = getSdkPackageType();

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
        String sdkHome = getSdkHome();
        try {
            ZipUtils.untargz(sdkZip, sdkHome, 1); // skip first directory path of artifact name/version
        } catch (IOException e) {
            throw new MojoExecutionException("Error extracting new SDK", e);
        }
    }

    private void installSdkFromExecutable(File sdkInstaller, SdkPackageType packageType)
            throws MojoExecutionException {
        sdkInstaller.setExecutable(true);

        List<String> commands = new ArrayList<String>();
        Collections.addAll(commands, packageType.installCommands());
        commands.add(sdkInstaller.getAbsolutePath());
        ProcessBuilder installer = new ProcessBuilder(commands);
        BufferedReader in = null, err = null;
        try {
            Process p = installer.start();
            in  = new BufferedReader(new InputStreamReader(p.getInputStream()));
            err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line;
            getLog().debug("Returned input from installer process follows:");
            while ((line = in.readLine()) != null) {
                getLog().info(line);
            }

            if (err.ready()) {
                getLog().info("Errors returned by subprocess installer:");
                while ((line = err.readLine()) != null) {
                    getLog().error(line);
                }
            }

            if (p.exitValue() != 0) {
                throw new MojoExecutionException("Installer failed; see above for errors.");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("error from installer subprocess", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(err);
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

    private SdkPackageType getSdkPackageType() throws MojoExecutionException {
        String sdkHome = getSdkHome();
        // look for a file installtype.txt which is delivered in the SDK packages;
        // it tells us what kind of install this is and, therefore, what package
        // to download from Marketplace.
        File installType = new File(sdkHome, INSTALLTYPE_FILE_NAME);
        SdkPackageType detectedType;
        if (installType.exists() && installType.canRead()) {
            getLog().debug("found " + INSTALLTYPE_FILE_NAME + " in ATLAS_HOME");
            String packageType = getInstallType(installType);
            try {
                detectedType = SdkPackageType.getType(packageType);
                getLog().debug("detected install type: " + detectedType);
            } catch (IllegalArgumentException e) {
                // no match found for package type, fall back to tar.gz
                getLog().debug("no package type found for " + packageType + "; falling back to tgz");
                detectedType = SdkPackageType.TGZ;
            }
        } else {
            // assume it was installed from a tar.gz
            getLog().debug("no file found at " + installType.getAbsolutePath()
                    + "; falling back to tgz");
            detectedType = SdkPackageType.TGZ;
        }
        return detectedType;
    }

    private String getInstallType(File file) throws MojoExecutionException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            return in.readLine().trim();
        } catch (FileNotFoundException e) {
            // shouldn't happen, as we did the check above
            throw new MojoExecutionException("file " + file.getAbsolutePath()
                    + " wasn't found, even though it exists");
        } catch (IOException e) {
            throw new MojoExecutionException("couldn't read from file " + file.getAbsolutePath()
                    + " even though we checked it for readability");
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private String getSdkHome() {
        return System.getenv("ATLAS_HOME");
    }
}
