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
    private final boolean applyConfig;
    private final boolean applyProp;
    

    public static AmpsVersionUpdate ampsVersionUpdate(String version,String type,boolean applyConfig,boolean applyProp)
    {
        return new AmpsVersionUpdate(version,type,applyConfig,applyProp);
    }

    private AmpsVersionUpdate(String version, String type,boolean applyConfig,boolean applyProp)
    {
        this.version = checkNotNull(version, "version");
        this.type = checkNotNull(type, "type");
        this.applyConfig = applyConfig;
        this.applyProp = applyProp;
    }

    public String getVersion()
    {
        return version;
    }

    public String getType()
    {
        return type;
    }

    public boolean isApplyConfig()
    {
        return applyConfig;
    }

    public boolean isApplyProp()
    {
        return applyProp;
    }

    @Override
    public String toString()
    {
        return "[AMPS " + type + " Version Update: " + version + "]";
    }
}
