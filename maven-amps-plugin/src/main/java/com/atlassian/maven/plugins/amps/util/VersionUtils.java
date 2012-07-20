package com.atlassian.maven.plugins.amps.util;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    private static final String SDK_DOWNLOAD_URL =
            "https://developer.atlassian.com/pages/viewpage.action?pageId=5668881";
    private static final int TIMEOUT = 30 * 1000;

    public static String getVersion() {
        InputStream in = null;
        final Properties props = new Properties();
        try {
            in = VersionUtils.class.getClassLoader()
                    .getResourceAsStream(
                            "META-INF/maven/com.atlassian.maven.plugins/maven-amps-plugin/pom.properties");
            if (in != null) {
                props.load(in);
                return props.getProperty("version");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        return "RELEASE";
    }

    public static String getLatestVersion(String currentVersion) {
        URL url = null;
        try {
            url = new URL(SDK_DOWNLOAD_URL);
        } catch (MalformedURLException e) {
            // won't ever happen.
            // sigh.
        }

        Document doc;
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
            return m.group(1);
        } else {
            return currentVersion;
        }
    }

    public static long versionFromString(String version) {
        String[] parts = version.split("\\.");
        long build = 0;
        for (int i = 0; i < parts.length; i++) {
            int part;
            try {
                part = Integer.parseInt(parts[i]);
            } catch (NumberFormatException nfe) {
                continue;
            }
            build += part * Math.pow(10, 10 - (i * 3));
        }
        return build;
    }

}
