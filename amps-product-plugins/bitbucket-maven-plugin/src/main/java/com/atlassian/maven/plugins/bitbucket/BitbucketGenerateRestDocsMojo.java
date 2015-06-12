package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.GenerateRestDocsMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 6.1.0
 */
@Mojo(name = "generate-rest-docs", requiresDependencyResolution = ResolutionScope.TEST)
public class BitbucketGenerateRestDocsMojo extends GenerateRestDocsMojo
{
}
