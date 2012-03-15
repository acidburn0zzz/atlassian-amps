package com.atlassian.plugins.codegen;

import com.atlassian.fugue.Option;

import static com.atlassian.fugue.Option.none;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes an artifact that should be added to the &lt;bundledArtifacts&gt; or &lt;pluginArtifacts&gt;
 * list in the AMPS configuration.
 */
public final class PluginArtifact
{
    private ArtifactId artifactId;
    private Option<String> version;
    private Option<String> propertyName;
    
    public static PluginArtifact pluginArtifact(ArtifactId groupAndArtifactId, Option<String> version, Option<String> propertyName)
    {
        return new PluginArtifact(groupAndArtifactId, version, propertyName);
    }

    public static PluginArtifact pluginArtifact(ArtifactId groupAndArtifactId, Option<String> version)
    {
        return new PluginArtifact(groupAndArtifactId, version, none(String.class));
    }

    private PluginArtifact(ArtifactId artifactId, Option<String> version, Option<String> propertyName)
    {
        this.artifactId = checkNotNull(artifactId, "artifactId");
        this.version = checkNotNull(version, "version");
        this.propertyName = checkNotNull(propertyName, "propertyName");
    }
    
    public ArtifactId getGroupAndArtifactId()
    {
        return artifactId;
    }
    
    public Option<String> getVersion()
    {
        return version;
    }
    
    public Option<String> getPropertyName()
    {
        return propertyName;
    }
    
    @Override
    public String toString()
    {
        StringBuilder ret = new StringBuilder();
        ret.append("(");
        ret.append(artifactId.getCombinedId());
        ret.append(",");
        for (String p : propertyName)
        {
            ret.append("${");
            ret.append(p);
            ret.append("}=");
        }
        ret.append(version.getOrElse(""));
        ret.append(")");
        return ret.toString();
    }
}
