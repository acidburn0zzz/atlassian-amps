package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes an artifact that should be added to the &lt;bundledArtifacts&gt; or &lt;pluginArtifacts&gt;
 * list in the AMPS configuration.
 */
public final class PluginArtifact implements PluginProjectChange
{
    /**
     * Specifies whether to add the artifact to &lt;bundledArtifacts&gt; or &lt;pluginArtifacts&gt;.
     */
    public enum ArtifactType
    {
        BUNDLED_ARTIFACT("bundledArtifact"),
        PLUGIN_ARTIFACT("pluginArtifact");
        
        private final String elementName;
        
        private ArtifactType(String elementName)
        {
            this.elementName = elementName;
        }
        
        public String getElementName()
        {
            return elementName;
        }
    };

    private final ArtifactType type;
    private final ArtifactId artifactId;
    private final VersionId versionId;
    
    public static PluginArtifact pluginArtifact(ArtifactType type, ArtifactId groupAndArtifactId, VersionId versionId)
    {
        return new PluginArtifact(type, groupAndArtifactId, versionId);
    }

    private PluginArtifact(ArtifactType type, ArtifactId artifactId, VersionId versionId)
    {
        this.type = checkNotNull(type, "type");
        this.artifactId = checkNotNull(artifactId, "artifactId");
        this.versionId = checkNotNull(versionId, "versionId");
    }
    
    public ArtifactType getType()
    {
        return type;
    }
    
    public ArtifactId getGroupAndArtifactId()
    {
        return artifactId;
    }
    
    public VersionId getVersionId()
    {
        return versionId;
    }
    
    @Override
    public String toString()
    {
        return "[" + type.getElementName() + ": " + artifactId + "]";
    }
}
