package com.atlassian.maven.plugins.confluence;

import com.atlassian.maven.plugins.amps.cli.IdeaMojo;

import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "idea", requiresProject = false)
public class ConfluenceIdeaMojo extends IdeaMojo
{
}
