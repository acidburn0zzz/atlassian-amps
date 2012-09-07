package com.atlassian.maven.plugins.updater;

/**
 * Types of SDK packages available for download from Marketplace.
 */
public enum SdkPackageType {

    WINDOWS("windows", "", ""),
    MAC("mac", "./", ""),
    RPM("rpm", "rpm", "-ivh"),
    DEB("deb", "dpkg", "-i"),
    TGZ("tgz", "", "");

    private final String key;
    private final String installCommand;
    private final String parameters;

    SdkPackageType(String key, String installCommand, String parameters) {
        this.key = key;
        this.installCommand = installCommand;
        this.parameters = parameters;
    }

    public String key() {
        return key;
    }

    public String installCommand() {
        return installCommand;
    }

    public String parameters() {
        return parameters;
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
