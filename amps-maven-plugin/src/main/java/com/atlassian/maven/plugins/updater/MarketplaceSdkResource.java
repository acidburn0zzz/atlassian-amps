package com.atlassian.maven.plugins.updater;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Implements an SdkResource for the Atlassian Marketplace.
 */
public class MarketplaceSdkResource extends AbstractLogEnabled implements SdkResource {

    private static final String SDK_DOWNLOAD_URL_ROOT =
            "https://marketplace.atlassian.com/rest/1.0/plugins/atlassian-plugin-sdk-";

    private final ObjectMapper mapper;

    public MarketplaceSdkResource() {
        mapper = new ObjectMapper();
    }

    @Override
    public File downloadLatestSdk(SdkPackageType packageType) {
        return downloadSdk(packageType, getLatestSdkVersion(packageType));
    }

    @Override
    public File downloadSdk(SdkPackageType packageType, String version) {
        Map<?, ?> rootAsMap = getPluginJsonAsMap(packageType);

        String versionDownloadPath = null;
        Map<?, ?> versionsElement = (Map<?, ?>) rootAsMap.get("versions");
        List<Map<?, ?>> versions = (List<Map<?, ?>>) versionsElement.get("versions");
        for (Map<?, ?> versionData: versions) {
            if (versionData.get("version").equals(version)) {
                List<Map<?, ?>> links = (List<Map<?, ?>>) versionData.get("links");
                for (Map<?, ?> link: links) {
                    if (link.get("rel").equals("binary")) {
                        versionDownloadPath = (String) link.get("href");
                        break;
                    }
                }
            }
        }

        if (versionDownloadPath == null) {
            throw new RuntimeException("Couldn't find SDK version for " + packageType.key()
                    + " on marketplace with version " + version);
        }

        File sdkDownloadTempFile;
        HttpURLConnection conn = null;
        try {
            String tempFileSuffix;
            if (packageType == SdkPackageType.WINDOWS) {
                tempFileSuffix = ".exe";
            } else if (packageType == SdkPackageType.MAC) {
                tempFileSuffix = ".pkg";
            } else if (packageType == SdkPackageType.RPM) {
                tempFileSuffix = ".rpm";
            } else if (packageType == SdkPackageType.DEB) {
                tempFileSuffix = ".deb";
            } else {
                tempFileSuffix = ".tar.gz";
            }
            sdkDownloadTempFile = File.createTempFile("atlassian-plugin-sdk-" + version, tempFileSuffix);

            URL url;
            try {
                url = new URL(versionDownloadPath);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            conn = (HttpURLConnection) url.openConnection();

            try (InputStream inputStream = conn.getInputStream()) {
                copyResponseStreamToFile(inputStream, sdkDownloadTempFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sdkDownloadTempFile;
    }

    @Override
    public String getLatestSdkVersion(SdkPackageType packageType) {
        Map<?, ?> rootAsMap = getPluginJsonAsMap(packageType);
        if (rootAsMap.containsKey("version")) {
            Map<?, ?> version = (Map<?, ?>) rootAsMap.get("version");
            return (String) version.get("version");
        } else {
            return "";
        }
    }

    private void copyResponseStreamToFile(InputStream stream, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            IOUtils.copy(stream, fos);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private Map<?, ?> getPluginJsonAsMap(SdkPackageType packageType) {
        URL url;
        try {
            url = new URL(SDK_DOWNLOAD_URL_ROOT + packageType.key());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String json;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            try (InputStream jsonStream = new BufferedInputStream(conn.getInputStream())) {
                json = IOUtils.toString(jsonStream, StandardCharsets.UTF_8);
            }
        } catch (UnknownHostException e) {
            this.getLogger().info("Unknown host " + url.getHost());
            json = "";
        } catch (ConnectException e) {
            this.getLogger().info("Fail to connect to host " + url.getHost());
            json = "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        Map<?, ?> rootAsMap;
        try {
            if (StringUtils.isNotEmpty(json)) {
                rootAsMap = mapper.readValue(json, Map.class);
            } else {
                rootAsMap = ImmutableMap.of();
            }
            return rootAsMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
