package com.atlassian.maven.plugins.amps.util;

import org.apache.maven.plugin.logging.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compares the current version of the SDK with the latest release on developer.atlassian.com.
 */
public class UpdateChecker {

    private static final String PREF_NAME = "last_update_check";
    private static final String SDK_DOWNLOAD_URL =
                "https://developer.atlassian.com/pages/viewpage.action?pageId=5668881";
    private static final int TIMEOUT = 30 * 1000;

    private final String currentVersion;
    private final Log logger;

    public UpdateChecker(String currentVersion, Log logger) {
        this.currentVersion = currentVersion;
        this.logger = logger;
    }

    public String check() {
        if (shouldCheck()) {
            logger.debug("Starting check for new SDK version. Current version: " + currentVersion);
            String latestVersion = getLatestVersion(currentVersion);
            logger.debug("Found latest SDK version: " + latestVersion);
            return latestVersion;
        } else {
            return currentVersion;
        }

    }

    private boolean shouldCheck() {
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

    private String getLatestVersion(String currentVersion) {
        URL url = null;
        try {
            url = new URL(SDK_DOWNLOAD_URL);
        } catch (MalformedURLException e) {
            // won't ever happen.
            // sigh.
        }

        Document doc = null;
        try {
            doc = Jsoup.parse(url, TIMEOUT);
        } catch (IOException e) {
            // timeout or network error
            return currentVersion;
        }

        Element sdkDownloadLink = doc.select("a[href*=/atlassian-plugin-sdk/").first();
        String link = sdkDownloadLink.attr("href");
        Pattern versionPattern = Pattern.compile(".*atlassian-plugin-sdk/(.*?)/atlassian.*");
        Matcher m = versionPattern.matcher(link);
        if (m.find()) {
            logger.debug("Scraped latest version: " + m.group(1));
            return m.group(1);
        } else {
            return currentVersion;
        }
    }
}