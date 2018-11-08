package com.atlassian.maven.plugins.amps.util;

import java.io.InputStream;
import java.io.IOException;

import java.util.Properties;

public class VersionUtils {

    private static final String RESOURCE_NAME = "META-INF/maven/com.atlassian.maven.plugins/amps-maven-plugin/pom.properties";

    public static String getVersion() {
        final ClassLoader classLoader = VersionUtils.class.getClassLoader();

        try (InputStream in = classLoader.getResourceAsStream(RESOURCE_NAME)) {
            if (in != null) {
                final Properties props = new Properties();
                props.load(in);
                return props.getProperty("version");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "RELEASE";
    }
}
