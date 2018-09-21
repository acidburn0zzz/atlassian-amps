package com.atlassian.maven.plugins.jira;

import com.atlassian.maven.plugins.amps.PluginInformation;

import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "create_v4", requiresProject = false)
public class JiraCreateV4Mojo extends JiraCreateMojo
{
    private static final String JIRA4 = "jira4";

    @Override
    protected String getDefaultProductId()
    {
        return JIRA4;
    }

    @Override
    protected PluginInformation getPluginInformation()
    {
        return new PluginInformation(JIRA4, super.getPluginInformation().getVersion());
    }
}
