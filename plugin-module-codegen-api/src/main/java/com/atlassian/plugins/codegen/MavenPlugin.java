package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a Maven plugin configuration element that should be added to the POM, specified
 * as a group/artifact/version and an XML fragment that provides all other elements within the
 * &lt;plugin&gt; element.  If the POM already contains a configuration for the same plugin,
 * then only the &lt;executions&gt; element will be modified, adding any &lt;execution&gt;
 * items whose IDs were not already present.
 */
public class MavenPlugin implements PluginProjectChange
{
    private final ArtifactId artifactId;
    private final VersionId versionId;
    private final String xmlContent;
    
    public static MavenPlugin mavenPlugin(ArtifactId artifactId, VersionId versionId, String xmlContent)
    {
        return new MavenPlugin(artifactId, versionId, xmlContent);
    }
    
    private MavenPlugin(ArtifactId artifactId, VersionId versionId, String xmlContent)
    {
        this.artifactId = checkNotNull(artifactId, "artifactId");
        this.versionId = checkNotNull(versionId, "versionId");
        this.xmlContent = checkNotNull(xmlContent, "xmlContent");
    }

    public ArtifactId getGroupAndArtifactId()
    {
        return artifactId;
    }

    public VersionId getVersionId()
    {
        return versionId;
    }
    
    public String getXmlContent()
    {
        return xmlContent;
    }
    
    @Override
    public String toString()
    {
        return "[plugin: " + artifactId + "]"; 
    }
}
