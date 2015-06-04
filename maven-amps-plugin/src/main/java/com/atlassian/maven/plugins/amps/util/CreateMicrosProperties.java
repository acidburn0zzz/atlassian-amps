package com.atlassian.maven.plugins.amps.util;

public class CreateMicrosProperties extends CreatePluginProperties
{
    private String name;
    private String description;
    private String organization;
    private String sourceUrl;
    private String ownerEmail;
    private String notificationEmail;

    public CreateMicrosProperties(String name, String description, String organization,
                                  String groupId, String artifactId, String version, String thePackage,
                                  String sourceUrl, String ownerEmail, String notificationEmail)
    {
        super(groupId, artifactId, version, thePackage);
        this.name = name;
        this.description = description;
        this.organization = organization;
        this.sourceUrl = sourceUrl;
        this.ownerEmail = ownerEmail;
        this.notificationEmail = notificationEmail;
    }

    public CreateMicrosProperties(String groupId, String artifactId, String version, String aPackage)
    {
        super(groupId, artifactId, version, aPackage);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOrganization() {
        return organization;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }
}
