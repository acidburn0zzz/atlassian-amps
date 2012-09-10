package com.atlassian.maven.plugins.amps.util;

import com.atlassian.maven.plugins.updater.SdkPackageType;
import com.atlassian.maven.plugins.updater.SdkResource;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;

/**
 * Compares the current version of the SDK with the latest release on developer.atlassian.com.
 */
public class UpdateChecker {

    private static final String PREF_NAME = "last_update_check";

    private final String currentVersion;
    private final Log logger;
    private final SdkResource sdkResource;
    private final boolean forceCheck;
    private static final String INSTALLTYPE_FILE_NAME = "installtype.txt" ;

    public UpdateChecker(String currentVersion, Log logger, SdkResource sdkResource, boolean forceCheck) {
        this.currentVersion = currentVersion;
        this.logger = logger;
        this.sdkResource = sdkResource;
        this.forceCheck = forceCheck;
    }

    public void check() {
        if (shouldCheck()) {
            File file = new File(getSdkHome(), INSTALLTYPE_FILE_NAME);
            SdkPackageType packageType = getSdkPackageType();
            String latestVersion = sdkResource.getLatestSdkVersion(packageType);
            if (canUpdate(currentVersion, latestVersion)) {
                logger.warn("Version " + latestVersion + " of the Atlassian Plugin SDK is now available.");
                logger.warn("Run the atlas-update command to install it.");
            }
        }
    }

    private boolean canUpdate(String currentVersion, String latestVersion) {
        return VersionUtils.versionFromString(latestVersion) >
               VersionUtils.versionFromString(currentVersion);
    }

    private boolean shouldCheck() {
        if (forceCheck) {
            logger.info("SDK update check forced by Maven property.");
            return true;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();

        Preferences prefs = Preferences.userNodeForPackage(getClass());
        String lastUpdateCheck = prefs.get(PREF_NAME, null);
        if (lastUpdateCheck == null) {
            prefs.put(PREF_NAME, dateFormat.format(now.getTime()));
            return true;
        }

        Date date;
        try {
            date = dateFormat.parse(lastUpdateCheck);
        } catch (ParseException pe) {
            logger.warn("Couldn't parse date value " + lastUpdateCheck + " from prefs; deleting");
            prefs.remove(PREF_NAME);
            prefs.put(PREF_NAME, dateFormat.format(now.getTime()));
            return true;
        }

        Calendar then = Calendar.getInstance();
        then.setTime(date);
        logger.debug("Last update check: " + then.getTime());
        then.roll(Calendar.DATE, true);

        if (then.before(now)) {
            logger.debug("Time for daily update check");
            prefs.put(PREF_NAME, dateFormat.format(now.getTime()));
            return true;
        } else {
            logger.debug("Already checked today");
            return false;
        }
    }

    // Move everything below to a common component for UpdateChecker and UpdateMojo!

    private SdkPackageType getSdkPackageType() {
        String sdkHome = getSdkHome();
        // look for a file installtype.txt which is delivered in the SDK packages;
        // it tells us what kind of install this is and, therefore, what package
        // to download from Marketplace.
        File installType = new File(sdkHome, INSTALLTYPE_FILE_NAME);
        SdkPackageType detectedType;
        if (installType.exists() && installType.canRead()) {
            logger.debug("found " + INSTALLTYPE_FILE_NAME + " in ATLAS_HOME");
            String packageType = getInstallType(installType);
            try {
                detectedType = SdkPackageType.getType(packageType);
                logger.debug("detected install type: " + detectedType);
            } catch (IllegalArgumentException e) {
                // no match found for package type, fall back to tar.gz
                logger.debug("no package type found for " + packageType + "; falling back to tgz");
                detectedType = SdkPackageType.TGZ;
            }
        } else {
            // assume it was installed from a tar.gz
            logger.debug("no file found at " + installType.getAbsolutePath()
                    + "; falling back to tgz");
            detectedType = SdkPackageType.TGZ;
        }
        return detectedType;
    }

    private String getInstallType(File file) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            return in.readLine().trim();
        } catch (FileNotFoundException e) {
            // shouldn't happen, as we did the check above
            throw new RuntimeException("file " + file.getAbsolutePath()
                    + " wasn't found, even though it exists");
        } catch (IOException e) {
            throw new RuntimeException("couldn't read from file " + file.getAbsolutePath()
                    + " even though we checked it for readability");
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private String getSdkHome() {
        return System.getenv("ATLAS_HOME");
    }

}