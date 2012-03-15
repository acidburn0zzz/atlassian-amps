package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a resource file that should be added to the project.
 */
public class ResourceFile implements PluginProjectChange
{
    private final String relativePath;
    private final String name;
    private final String content;
    
    public static ResourceFile resourceFile(String relativePath, String name, String content)
    {
        return new ResourceFile(relativePath, name, content);
    }
    
    private ResourceFile(String relativePath, String name, String content)
    {
        this.relativePath = normalizePath(checkNotNull(relativePath, "relativePath"));
        this.name = checkNotNull(name, "name");
        this.content = checkNotNull(content, "content");
    }

    public String getRelativePath()
    {
        return relativePath;
    }

    public String getName()
    {
        return name;
    }
    
    public String getContent()
    {
        return content;
    }
    
    private String normalizePath(String path)
    {
        return (path.endsWith("/")) ? path.substring(0, path.length() - 1) : path;
    }
        
    @Override
    public String toString()
    {
        return "[resource: " + name + "]";
    }
}
