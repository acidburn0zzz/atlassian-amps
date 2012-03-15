package com.atlassian.plugins.codegen;

/**
 * Describes an key-value pair that should be added to the &lt;systemPropertyVariables&gt;
 * element in AMPS configuration.
 */
public class AmpsSystemPropertyVariable extends AbstractPropertyValue implements PluginProjectChange
{
    public static AmpsSystemPropertyVariable ampsSystemPropertyVariable(String name, String value)
    {
        return new AmpsSystemPropertyVariable(name, value);
    }

    private AmpsSystemPropertyVariable(String name, String value)
    {
        super(name, value);
    }
}
