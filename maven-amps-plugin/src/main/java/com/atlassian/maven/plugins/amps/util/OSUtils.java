package com.atlassian.maven.plugins.amps.util;

/**
 * Provides access to an OS for system-specific behavior.
 */
public class OSUtils {

    public static OS OS;

    static {
        String osProp = System.getProperty("os.name");
        if (osProp.contains("Windows")) {
            OS = OSUtils.OS.WINDOWS;
        } else if (osProp.contains("Mac OS")) {
            OS = OSUtils.OS.MAC;
        } else {
            OS = OSUtils.OS.LINUX;
        }
    }

    public static enum OS {
        WINDOWS("windows"),
        MAC("mac"),
        LINUX("linux");

        private final String id;

        OS(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }


}
