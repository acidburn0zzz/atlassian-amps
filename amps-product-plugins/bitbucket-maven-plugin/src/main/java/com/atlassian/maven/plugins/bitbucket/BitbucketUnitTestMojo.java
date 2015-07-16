package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.UnitTestMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 6.1.0
 */
@Mojo(name = "unit-test", requiresDependencyResolution = ResolutionScope.TEST)
public class BitbucketUnitTestMojo extends UnitTestMojo
{
}
