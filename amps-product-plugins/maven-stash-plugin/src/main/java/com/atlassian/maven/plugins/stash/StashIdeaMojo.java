package com.atlassian.maven.plugins.stash;

import com.atlassian.maven.plugins.amps.cli.IdeaMojo;

import org.apache.maven.plugins.annotations.Mojo;

/**
 * @since 3.10
 */
@Mojo(name = "idea", requiresProject = false)
public class StashIdeaMojo extends IdeaMojo
{
}
