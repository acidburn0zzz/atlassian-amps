package com.atlassian.maven.plugins.amps.util;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.ProjectRewriter;

/**
 * @since version
 */
public class NOOPProjectRewriter implements ProjectRewriter
{

    @Override
    public void applyChanges(PluginProjectChangeset changes) throws Exception
    {
        //do nothing
    }
}
