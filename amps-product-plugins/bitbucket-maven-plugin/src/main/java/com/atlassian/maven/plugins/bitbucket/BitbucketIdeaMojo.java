package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.cli.IdeaMojo;

import org.apache.maven.plugins.annotations.Mojo;

/**
 * @since 3.10
 */
@Mojo(name = "idea", requiresProject = false)
public class BitbucketIdeaMojo extends IdeaMojo
{
}
