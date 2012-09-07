package com.atlassian.maven.plugins.updater;

import java.io.File;

/**
 * Interface for getting information about a SDK release.
 */
public interface SdkResource {

    public String getLatestSdkVersion(SdkPackageType packageType);

    public File downloadLatestSdk(SdkPackageType packageType);

    public File downloadSdk(SdkPackageType packageType, String version);

}
