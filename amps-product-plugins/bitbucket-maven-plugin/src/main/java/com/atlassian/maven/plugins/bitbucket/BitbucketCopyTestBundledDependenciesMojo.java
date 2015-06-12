package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.CopyTestBundledDependenciesMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 6.1.0
 */
@Mojo(name = "copy-test-bundled-dependencies", requiresDependencyResolution = ResolutionScope.TEST)
public class BitbucketCopyTestBundledDependenciesMojo extends CopyTestBundledDependenciesMojo
{
}
