package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.PrepareDatabaseMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 6.1.0
 */
@Mojo (name = "prepare-database", requiresDependencyResolution = ResolutionScope.TEST)
public class BitbucketPrepareDatabaseMojo extends PrepareDatabaseMojo
{
}
