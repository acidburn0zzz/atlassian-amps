package com.atlassian.maven.plugins.amps;

import org.apache.maven.plugins.annotations.Mojo;

/**
 * Debug the webapp without a plugin project
 */
@Mojo(name = "debug-standalone", requiresProject = false)
public class DebugStandaloneMojo extends RunStandaloneMojo
{
    @Override
    protected String getAmpsGoal()
    {
        return "debug";
    }
}
