package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a plugin module element that should be added to the plugin XML file.
 * This is provided as an arbitrary XML fragment string.
 */
public class ModuleDescriptor implements PluginProjectChange
{
    private final String content;
    
    public static ModuleDescriptor moduleDescriptor(String content)
    {
        return new ModuleDescriptor(content);
    }
    
    private ModuleDescriptor(String content)
    {
        this.content = checkNotNull(content, "content");
    }

    public String getContent()
    {
        return content;
    }
    
    @Override
    public String toString()
    {
        return "[module]";
    }
}
