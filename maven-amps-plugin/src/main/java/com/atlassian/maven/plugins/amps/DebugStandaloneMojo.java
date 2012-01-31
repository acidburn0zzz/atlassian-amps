package com.atlassian.maven.plugins.amps;

import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoRequiresProject;

/**
 * Debug the webapp without a plugin project
 */
@MojoGoal ("debug-standalone")
@MojoRequiresProject (false)
public class DebugStandaloneMojo extends RunStandaloneMojo
{
    @Override
    protected String getAmpsGoal()
    {
        return "debug";
    }
}
