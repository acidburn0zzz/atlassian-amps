package com.atlassian.maven.plugins.amps.util;

import com.atlassian.maven.plugins.updater.SdkResource;
import org.apache.maven.plugin.logging.Log;

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

    public UpdateChecker(String currentVersion, Log logger, SdkResource sdkResource, boolean forceCheck) {
        this.currentVersion = currentVersion;
        this.logger = logger;
        this.sdkResource = sdkResource;
        this.forceCheck = forceCheck;
    }

    public void check() {
        if (shouldCheck()) {
            String latestVersion = sdkResource.getLatestSdkVersion();
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

}