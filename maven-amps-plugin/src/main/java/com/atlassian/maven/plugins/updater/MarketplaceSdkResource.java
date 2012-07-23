package com.atlassian.maven.plugins.updater;

import com.atlassian.maven.plugins.amps.util.OSUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Implements an SdkResource for the Atlassian Marketplace.
 */
public class MarketplaceSdkResource implements SdkResource {

    private static final String SDK_DOWNLOAD_URL_ROOT =
            "https://marketplace.atlassian.com/rest/1.0/plugins/atlassian-plugin-sdk-";
    private static final int CONNECT_TIMEOUT = 15 * 1000;
    private static final int READ_TIMEOUT = 15 * 1000;

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public MarketplaceSdkResource() {
        httpClient = new HttpClient();
        mapper = new ObjectMapper();

        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(READ_TIMEOUT);
    }

    @Override
    public File downloadLatestSdk() {
        return downloadSdk(getLatestSdkVersion());
    }

    @Override
    public File downloadSdk(String version) {
        Map<?, ?> rootAsMap = getPluginJsonAsMap();

        String versionDownloadPath = null;
        Map<?, ?> versionsElement = (Map<?, ?>) rootAsMap.get("versions");
        ArrayList<Map<?, ?>> versions = (ArrayList<Map<?, ?>>) versionsElement.get("versions");
        for (Map<?, ?> versionData: versions) {
            if (versionData.get("version").equals(version)) {
                Map<?, ?> links = (Map<?, ?>) versionData.get("links");
                versionDownloadPath = (String) links.get("binary");
            }
        }

        if (versionDownloadPath == null) {
            throw new RuntimeException("Couldn't find SDK version for " + OSUtils.OS.getId()
                    + " on marketplace with version " + version);
        }

        File sdkDownloadTempFile;
        try {
            String tempFileSuffix;
            if (OSUtils.OS == OSUtils.OS.WINDOWS) {
                tempFileSuffix = ".zip";
            } else {
                tempFileSuffix = ".tar.gz";
            }
            sdkDownloadTempFile = File.createTempFile("atlassian-plugin-sdk-" + version, tempFileSuffix);
            sdkDownloadTempFile.deleteOnExit();

            GetMethod method = new GetMethod(versionDownloadPath);
            httpClient.executeMethod(method);
            InputStream responseStream = method.getResponseBodyAsStream();
            copyResponseStreamToFile(responseStream, sdkDownloadTempFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sdkDownloadTempFile;
    }

    @Override
    public String getLatestSdkVersion() {
        Map<?, ?> rootAsMap = getPluginJsonAsMap();
        Map<?, ?> version = (Map<?, ?>) rootAsMap.get("version");
        return (String) version.get("version");
    }

    private void copyResponseStreamToFile(InputStream stream, File file) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException(fnfe);
        }
        try {
            IOUtils.copy(stream, fos);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            IOUtils.closeQuietly(stream);
            IOUtils.closeQuietly(fos);
        }

    }

    private Map<?, ?> getPluginJsonAsMap() {
        GetMethod method = new GetMethod(SDK_DOWNLOAD_URL_ROOT + OSUtils.OS.getId());
        String json;
        try {
            httpClient.executeMethod(method);
            json = method.getResponseBodyAsString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Map<?, ?> rootAsMap;
        try {
            rootAsMap = mapper.readValue(json, Map.class);
            return rootAsMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
