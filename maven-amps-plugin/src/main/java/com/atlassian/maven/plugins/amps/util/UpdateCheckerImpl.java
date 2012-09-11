package com.atlassian.maven.plugins.amps.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;

import com.atlassian.maven.plugins.amps.codegen.prompter.PrettyPrompter;
import com.atlassian.maven.plugins.updater.LocalSdk;
import com.atlassian.maven.plugins.updater.SdkPackageType;
import com.atlassian.maven.plugins.updater.SdkResource;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import jline.ANSIBuffer;

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
    private boolean useAnsiColor;
    
    public UpdateCheckerImpl() {
        String mavencolor = System.getenv("MAVEN_COLOR");
        if (mavencolor != null && !mavencolor.equals(""))
        {
            useAnsiColor = Boolean.parseBoolean(mavencolor);
        } else
        {
            useAnsiColor = false;
        }
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
                try
                {
                    if(useAnsiColor)
                    {
                        promptAnsi(latestVersion);
                    }
                    else
                    {
                        promptPlain(latestVersion);
                    }
                }
                catch (PrompterException e)
                {
                    getLogger().error("error prompting for update: " + e.getMessage());
                }
            }
        }
    }

    private void promptPlain(String latestVersion) throws PrompterException
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Version " + latestVersion + " of the Atlassian Plugin SDK is now available.\n")
          .append("Run the atlas-update command to install it.\n")
          .append("Press ENTER to continue");

        prompter.prompt(sb.toString());
    }

    private void promptAnsi(String latestVersion) throws PrompterException
    {
        ANSIBuffer ansiBuffer = new ANSIBuffer();
        ansiBuffer.append(ANSIBuffer.ANSICodes.attrib(PrettyPrompter.FG_YELLOW))
                  .append("Version " + latestVersion + " of the Atlassian Plugin SDK is now available.\n")
                  .append("Run the atlas-update command to install it.\n")
                  .append(ANSIBuffer.ANSICodes.attrib(PrettyPrompter.OFF))
                  .append("Press ENTER to continue");
        prompter.prompt(ansiBuffer.toString());
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
            getLogger().info("SDK update check forced by Maven property.");
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