package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.cli.IdeaMojo;

import org.apache.maven.plugins.annotations.Mojo;

/**
 * @since 6.1.0
 */
@Mojo(name = "idea", requiresProject = false)
public class BitbucketIdeaMojo extends IdeaMojo
{
}
