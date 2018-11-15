package com.atlassian.maven.plugins.amps.util;

import com.atlassian.maven.plugins.updater.LocalSdk;
import com.atlassian.maven.plugins.updater.SdkPackageType;
import com.atlassian.maven.plugins.updater.SdkResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;

/**
 * Compares the current version of the SDK with the latest release on developer.atlassian.com.
 */
public class UpdateCheckerImpl extends AbstractLogEnabled implements UpdateChecker
{
    private static final String INSTALLTYPE_FILE_NAME = "installtype.txt" ;
    private static final String PREF_NAME = "last_update_check";

    //injected by plexus
    private SdkResource sdkResource;
    private Prompter prompter;
    private LocalSdk localSdk;
    
    private String currentVersion;
    private boolean forceCheck;
    private boolean skipCheck;

    public UpdateCheckerImpl() {
        this.currentVersion = "";
        this.forceCheck = false;
        this.skipCheck = false;
    }

    @Override
    public void check() {
        if (shouldCheck()) {
            SdkPackageType packageType = localSdk.sdkPackageType();
            String latestVersion = sdkResource.getLatestSdkVersion(packageType);
            if (canUpdate(currentVersion, latestVersion)) {
                String warning = MessageUtils.buffer()
                        .warning("************************************************************\n")
                        .warning("Version ").warning(latestVersion).warning(" of the Atlassian Plugin SDK is now available.\n")
                        .warning("Run the atlas-update command to update.\n")
                        .warning("************************************************************")
                        .toString();

                getLogger().warn(warning);
            } else {
                StringBuilder sb = new StringBuilder();
                if(StringUtils.isEmpty(latestVersion)) {
                    sb.append("Can not get the latest version from MPAC. ");
                    sb.append("Carry on with current version ").append(currentVersion);
                    sb.append(". No action taken.");
                } else {
                    sb.append("Current version ").append(currentVersion);
                    sb.append(" is more recent than MPAC version ").append(latestVersion);
                    sb.append(". No action taken.");
                }
                getLogger().warn(sb.toString());
            }
        }
    }

    private boolean canUpdate(String currentVersion, String latestVersion) {
        DefaultArtifactVersion sdkVersion = new DefaultArtifactVersion(currentVersion);
        DefaultArtifactVersion mpacVersion = new DefaultArtifactVersion(latestVersion);
        return sdkVersion.compareTo(mpacVersion) < 0;
    }

    private boolean shouldCheck() {
        if(skipCheck)
        {
            return false;
        }
        
        if (forceCheck) {
            getLogger().warn("SDK update check forced by Maven property.");
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
            getLogger().warn("Couldn't parse date value " + lastUpdateCheck + " from prefs; deleting");
            prefs.remove(PREF_NAME);
            prefs.put(PREF_NAME, dateFormat.format(now.getTime()));
            return true;
        }

        Calendar then = Calendar.getInstance();
        then.setTime(date);
        getLogger().debug("Last update check: " + then.getTime());
        then.roll(Calendar.DATE, true);

        if (then.before(now)) {
            getLogger().debug("Time for daily update check");
            prefs.put(PREF_NAME, dateFormat.format(now.getTime()));
            return true;
        } else {
            getLogger().debug("Already checked today");
            return false;
        }
    }

    @Override
    public void setCurrentVersion(String currentVersion)
    {
        this.currentVersion = currentVersion;
    }

    @Override
    public void setForceCheck(boolean forceCheck)
    {
        this.forceCheck = forceCheck;
    }

    @Override
    public void setSkipCheck(boolean skipCheck)
    {
        this.skipCheck = skipCheck;
    }
}