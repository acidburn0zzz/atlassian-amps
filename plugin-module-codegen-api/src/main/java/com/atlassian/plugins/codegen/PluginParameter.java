package com.atlassian.plugins.codegen;

/**
 * Describes an key-value pair that should be added to the &lt;pluginInfo&gt; element in
 * atlassian-plugin.XML.
 */
public class PluginParameter extends AbstractPropertyValue implements PluginProjectChange
{
    public static PluginParameter pluginParameter(String name, String value)
    {
        return new PluginParameter(name, value);
    }

    private PluginParameter(String name, String value)
    {
        super(name, value);
    }
    
    @Override
    public String toString()
    {
        return "[pluginInfo: " + super.toString() + "]";
    }
}
