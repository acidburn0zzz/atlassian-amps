package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a dependency on an external artifact that should be added to the POM.
 */
public final class ArtifactDependency
{
    public enum Scope
    {
        DEFAULT,
        COMPILE,
        PROVIDED,
        TEST
    };

    private String groupId;
    private String artifactId;
    private String version;
    private Scope scope;
    
    public static ArtifactDependency dependency(String groupId, String artifactId, String version, Scope scope)
    {
        return new ArtifactDependency(groupId, artifactId, version, scope);
    }
    
    private ArtifactDependency(String groupId, String artifactId, String version, Scope scope)
    {
        this.groupId = checkNotNull(groupId, "groupId");
        this.artifactId = checkNotNull(artifactId, "artifactId");
        this.version = checkNotNull(version, "version");
        this.scope = checkNotNull(scope, "scope");
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
    
    public Scope getScope()
    {
        return scope;
    }
}
