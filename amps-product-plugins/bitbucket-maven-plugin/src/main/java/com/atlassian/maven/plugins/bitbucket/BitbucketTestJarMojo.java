package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.TestJarMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 6.1.0
 */
@Mojo(name = "test-jar",requiresDependencyResolution = ResolutionScope.TEST)
public class BitbucketTestJarMojo extends TestJarMojo
{
}
