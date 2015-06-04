package com.atlassian.maven.plugins.amps.util;

public class CreateMicrosProperties extends CreatePluginProperties
{
    private String serviceName;
    public CreateMicrosProperties(String groupId, String artifactId, String version, String thePackage)
    {
        super(groupId, artifactId, version, thePackage);
    }
}
