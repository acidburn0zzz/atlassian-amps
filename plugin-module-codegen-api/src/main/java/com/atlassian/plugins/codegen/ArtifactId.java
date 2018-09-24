package com.atlassian.plugins.codegen;

import com.atlassian.fugue.Option;

import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Simple wrapper for a Maven artifact ID with optional group ID.
 */
public final class ArtifactId
{
    private final Option<String> groupId;
    private final String artifactId;
    
    public static ArtifactId artifactId(String artifactId)
    {
        return new ArtifactId(none(String.class), artifactId);
    }
    
    public static ArtifactId artifactId(String groupId, String artifactId)
    {
        return new ArtifactId(some(groupId), artifactId); 
    }
    
    public static ArtifactId artifactId(Option<String> groupId, String artifactId)
    {
        return new ArtifactId(groupId, artifactId); 
    }
    
    private ArtifactId(Option<String> groupId, String artifactId)
    {
        this.groupId = checkNotNull(groupId, "groupId");
        this.artifactId = checkNotNull(artifactId, "artifactId");
    }
    
    public Option<String> getGroupId()
    {
        return groupId;
    }
    
    public String getArtifactId()
    {
        return artifactId;
    }

    public String getCombinedId()
    {
        for (String g : groupId)
        {
            return g + ":" + artifactId;
        }
        return artifactId;
    }
    
    @Override
    public String toString()
    {
        return getCombinedId();
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (other instanceof ArtifactId)
        {
            ArtifactId c = (ArtifactId) other;
            return groupId.equals(c.groupId) && artifactId.equals(c.artifactId);
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return getCombinedId().hashCode();
    }
}
