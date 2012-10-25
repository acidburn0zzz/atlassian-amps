package com.atlassian.maven.plugins.amps.util;

/**
 * @since version
 */
public class CreatePluginProperties
{
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String thePackage;

    public CreatePluginProperties(String groupId, String artifactId, String version, String thePackage)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.thePackage = thePackage;
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
}
