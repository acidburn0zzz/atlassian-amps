package com.atlassian.plugins.codegen.modules;

import java.io.File;

/**
 *
 */
public class PluginModuleLocation
{
    private final File sourceDirectory;
    private final File resourcesDir;
    private final File testDirectory;
    private final File templateDirectory;
    private final File pluginXml;
    private final String groupId;
    private final String artifactId;

    private PluginModuleLocation(Builder builder)
    {
        this.sourceDirectory = builder.sourceDirectory;
        this.templateDirectory = builder.templateDirectory;
        this.resourcesDir = builder.resourcesDirectory;
        this.testDirectory = builder.testDirectory;
        this.groupId = builder.groupId;
        this.artifactId = builder.artifactId;
        this.pluginXml = new File(resourcesDir, "atlassian-plugin.xml");
    }

    public File getSourceDirectory()
    {
        return sourceDirectory;
    }

    public File getResourcesDir()
    {
        return resourcesDir;
    }

    public File getTestDirectory()
    {
        return testDirectory;
    }

    public File getTemplateDirectory()
    {
        return templateDirectory;
    }

    public File getPluginXml()
    {
        return pluginXml;
    }

    public String getGroupId()
    {
        return groupId;
    }
    
    public String getArtifactId()
    {
        return artifactId;
    }
    
    public static class Builder
    {
        private File sourceDirectory;
        private File resourcesDirectory;
        private File testDirectory;
        private File templateDirectory;
        private String groupId;
        private String artifactId;

        public Builder(File sourceDirectory)
        {
            this.sourceDirectory = sourceDirectory;
        }

        public Builder testDirectory(File testDirectory)
        {
            this.testDirectory = testDirectory;
            return this;
        }

        public Builder resourcesDirectory(File resourcesDirectory)
        {
            this.resourcesDirectory = resourcesDirectory;
            return this;
        }

        public Builder templateDirectory(File templateDirectory)
        {
            this.templateDirectory = templateDirectory;
            return this;
        }

        public Builder groupAndArtifactId(String groupId, String artifactId)
        {
            this.groupId = groupId;
            this.artifactId = artifactId;
            return this;
        }

        public PluginModuleLocation build()
        {
            return new PluginModuleLocation(this);
        }
    }
}
