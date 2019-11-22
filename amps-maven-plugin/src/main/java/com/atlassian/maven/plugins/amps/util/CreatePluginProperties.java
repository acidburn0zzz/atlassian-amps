package com.atlassian.maven.plugins.amps.util;

import com.google.common.base.Strings;

/**
 * @since version
 */
public class CreatePluginProperties
{
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String thePackage;
    private final boolean useOsgiJavaConfig;

    public CreatePluginProperties(String groupId, String artifactId, String version, String thePackage, String useOsgiJavaConfig)
    {
        this(groupId, artifactId, version, thePackage, Strings.nullToEmpty(useOsgiJavaConfig).equalsIgnoreCase("Y"));
    }

    public CreatePluginProperties(String groupId, String artifactId, String version, String thePackage, boolean useOsgiJavaConfig) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.thePackage = thePackage;
        this.useOsgiJavaConfig = useOsgiJavaConfig;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public String getThePackage()
    {
        return thePackage;
    }

    public boolean isUseOsgiJavaConfig() {
        return useOsgiJavaConfig;
    }

    public String getUseOsgiJavaConfigInMavenInvocationFormat() {
        return useOsgiJavaConfig ? "Y" : "N";
    }
}
