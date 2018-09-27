package com.atlassian.maven.plugins.refapp;

import com.atlassian.maven.plugins.amps.cli.IdeaMojo;

import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "idea", requiresProject = false)
public class RefappIdeaMojo extends IdeaMojo
{
}