package com.atlassian.maven.plugins.amps;

/**
 * Represents a plugin artifact to be retrieved
 */
public class ProductArtifact
{
    private String groupId, artifactId, version, type;

    public ProductArtifact() {
    }

    public ProductArtifact(final String groupId, final String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.type = "jar";
    }

    public ProductArtifact(final String groupId, final String artifactId, final String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = "jar";
    }

    public ProductArtifact(final String groupId, final String artifactId, final String version, final String type) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(groupId).append(":").append(artifactId).append(":").append(version).append(":").append(type).toString();
    }
}
