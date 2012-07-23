package com.atlassian.maven.plugins.updater;

import java.io.File;

/**
 * Interface for getting information about a SDK release.
 */
public interface SdkResource {

    public String getLatestSdkVersion();

    public File downloadLatestSdk();

    public File downloadSdk(String version);

}
