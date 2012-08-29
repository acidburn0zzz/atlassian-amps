package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Holds the new AMPS version that should be updated in the pom.
 * If the POM already has an amps.version property, it will be updated. Otherwise a new property will be created.
 * Also, if the amps plugin is using a hardcoded version, it will be updated to use the amps.version property.
 */
public class AmpsVersionUpdate implements PluginProjectChange
{
    private final String version;

    public static AmpsVersionUpdate ampsVersionUpdate(String version)
    {
        return new AmpsVersionUpdate(version);
    }

    private AmpsVersionUpdate(String version)
    {
        this.version = checkNotNull(version, "version");
    }

    public String getVersion()
    {
        return version;
    }

    @Override
    public String toString()
    {
        return "[AMPS Version Update: " + version + "]";
    }
}
