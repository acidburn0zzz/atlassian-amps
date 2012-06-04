package com.atlassian.maven.plugins.jira;

import com.atlassian.maven.plugins.amps.cli.IdeaMojo;

import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "idea", requiresProject = false)
public class JiraIdeaMojo extends IdeaMojo
{
}
