package com.atlassian.maven.plugins.updater;

/**
 * Types of SDK packages available for download from Marketplace.
 */
public enum SdkPackageType {

    WINDOWS("windows", new String[] {}),
    MAC("mac", new String[] {}),
    RPM("rpm", new String[] { "sudo", "rpm", "-ivh" }),
    DEB("deb", new String[] { "sudo", "dpkg", "-i" }),
    TGZ("tgz", new String[] {});

    private final String key;
    private final String[] installCommands;

    SdkPackageType(String key, String[] installCommands) {
        this.key = key;
        this.installCommands = installCommands;
    }

    public String key() {
        return key;
    }

    public String[] installCommands() {
        return installCommands;
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
