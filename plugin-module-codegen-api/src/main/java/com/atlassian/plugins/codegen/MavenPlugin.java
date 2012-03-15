package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

import com.atlassian.fugue.Option;

/**
 * Describes a Maven plugin configuration element that should be added to the POM, specified
 * as a group/artifact/version and an XML fragment that provides all other elements within the
 * &lt;plugin&gt; element.  If the POM already contains a configuration for the same plugin,
 * then only the &lt;executions&gt; element will be modified, adding any &lt;execution&gt;
 * items whose IDs were not already present.
 */
public class MavenPlugin
{
    private final ArtifactId artifactId;
    private final Option<String> version;
    private final String xmlContent;
    
    public static MavenPlugin mavenPlugin(ArtifactId artifactId, Option<String> version, String xmlContent)
    {
        return new MavenPlugin(artifactId, version, xmlContent);
    }
    
    private MavenPlugin(ArtifactId artifactId, Option<String> version, String xmlContent)
    {
        this.artifactId = checkNotNull(artifactId, "artifactId");
        this.version = checkNotNull(version, "version");
        this.xmlContent = checkNotNull(xmlContent, "xmlContent");
    }

    public ArtifactId getGroupAndArtifactId()
    {
        return artifactId;
    }

    public Option<String> getVersion()
    {
        return version;
    }
    
    public String getXmlContent()
    {
        return xmlContent;
    }
}
