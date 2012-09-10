package com.atlassian.maven.plugins.updater;

/**
 * Provides information about the currently installed SDK.
 */
public interface LocalSdk {

    /**
     * Returns the type of package that was used to install this SDK.
     * @return a value of {@code SdkPackageType}
     */
    public SdkPackageType sdkPackageType();

    /**
     * Returns the path that the SDK lives in (set by the atlas-scripts).
     * @return the directory where the SDK was installed
     */
    public String sdkHomeDir();

}
