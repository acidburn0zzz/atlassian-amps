package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Holds the new AMPS version that should be updated in the pom.
 * If the POM already has an amps.version property, it will be updated. Otherwise a new property will be created.
 * Also, if the amps plugin is using a hardcoded version, it will be updated to use the amps.version property.
 */
public class AmpsVersionUpdate implements PluginProjectChange
{
    public static final String PLUGIN = "plugin";
    public static final String MANAGEMENT = "management";

    private final String version;
    private final String type;

    public static AmpsVersionUpdate ampsVersionUpdate(String version,String type)
    {
        return new AmpsVersionUpdate(version,type);
    }

    private AmpsVersionUpdate(String version, String type)
    {
        this.version = checkNotNull(version, "version");
        this.type = checkNotNull(type, "type");
    }

    public String getVersion()
    {
        return version;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return "[AMPS " + type + " Version Update: " + version + "]";
    }
}
