package com.atlassian.maven.plugins.updater;

/**
 * Types of SDK packages available for download from Marketplace.
 */
public enum SdkPackageType {

    WINDOWS("windows", ""),
    MAC("mac", "./"),
    RPM("rpm", "rpm -i"),
    DEB("deb", "dpkg -i"),
    TGZ("tgz", "");

    private final String key;
    private final String installCommand;

    SdkPackageType(String key, String installCommand) {
        this.key = key;
        this.installCommand = installCommand;
    }

    public String key() {
        return key;
    }

    public String installCommand() {
        return installCommand;
    }

    public static SdkPackageType getType(String key) {
        if (key.equals("windows")) {
            return WINDOWS;
        } else if (key.equals("mac")) {
            return MAC;
        } else if (key.equals("rpm")) {
            return RPM;
        } else if (key.equals("deb")) {
            return DEB;
        } else if (key.equals("tgz")) {
            return TGZ;
        } else return TGZ;
    }
}
