package com.atlassian.maven.plugins.bamboo;

import com.atlassian.maven.plugins.amps.cli.IdeaMojo;

import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "idea", requiresProject = false)
public class BambooIdeaMojo extends IdeaMojo
{
}
