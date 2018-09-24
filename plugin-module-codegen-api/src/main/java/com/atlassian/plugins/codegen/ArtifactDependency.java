package com.atlassian.plugins.codegen;

import static com.atlassian.plugins.codegen.ArtifactId.artifactId;
import static com.atlassian.plugins.codegen.VersionId.version;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.atlassian.fugue.Option.some;

/**
 * Describes a dependency on an external artifact that should be added to the POM.
 */
public final class ArtifactDependency implements PluginProjectChange
{
    public enum Scope
    {
        DEFAULT,
        COMPILE,
        PROVIDED,
        TEST
    };

    private ArtifactId artifactId;
    private VersionId versionId;
    private Scope scope;
    
    /**
     * Creates a dependency descriptor whose version string is provided explicitly.
     * @param groupId  Maven group ID
     * @param artifactId  Maven artifact ID
     * @param versionId  the version identifier
     * @param scope  dependency scope (use DEFAULT to omit scope declaration)
     */
    public static ArtifactDependency dependency(String groupId, String artifactId, VersionId versionId, Scope scope)
    {
        return new ArtifactDependency(artifactId(some(groupId), artifactId), versionId, scope);
    }

    /**
     * Creates a dependency descriptor whose version string is provided explicitly.
     * @param groupId  Maven group ID
     * @param artifactId  Maven artifact ID
     * @param version  the version string
     * @param scope  dependency scope (use DEFAULT to omit scope declaration)
     */
    public static ArtifactDependency dependency(String groupId, String artifactId, String version, Scope scope)
    {
        return new ArtifactDependency(artifactId(some(groupId), artifactId), version(version), scope);
    }

    /**
     * Creates a dependency descriptor whose version string is provided explicitly.
     * @param groupAndArtifactId  Maven group and artifact ID
     * @param versionId  the version identifier
     * @param scope  dependency scope (use DEFAULT to omit scope declaration)
     */
    public static ArtifactDependency dependency(ArtifactId groupAndArtifactId, VersionId versionId, Scope scope)
    {
        return new ArtifactDependency(groupAndArtifactId, versionId, scope);
    }

    /**
     * Creates a dependency descriptor whose version string is provided explicitly.
     * @param groupAndArtifactId  Maven group and artifact ID
     * @param version  the version string
     * @param scope  dependency scope (use DEFAULT to omit scope declaration)
     */
    public static ArtifactDependency dependency(ArtifactId groupAndArtifactId, String version, Scope scope)
    {
        return new ArtifactDependency(groupAndArtifactId, version(version), scope);
    }

    private ArtifactDependency(ArtifactId artifactId, VersionId versionId, Scope scope)
    {
        this.artifactId = checkNotNull(artifactId, "artifactId");
        if (!artifactId.getGroupId().isDefined())
        {
            throw new IllegalArgumentException("Group ID must be specified for dependency");
        }
        this.versionId = checkNotNull(versionId, "versionId");
        if (!versionId.getVersion().isDefined())
        {
            throw new IllegalArgumentException("Version must be specified for dependency");
        }
        this.scope = checkNotNull(scope, "scope");
    }
    
    public ArtifactId getGroupAndArtifactId()
    {
        return artifactId;
    }
    
    public VersionId getVersionId()
    {
        return versionId;
    }
    
    public Scope getScope()
    {
        return scope;
    }
    
    @Override
    public String toString()
    {
        return "[dependency: " + artifactId + "]";
    }
}
