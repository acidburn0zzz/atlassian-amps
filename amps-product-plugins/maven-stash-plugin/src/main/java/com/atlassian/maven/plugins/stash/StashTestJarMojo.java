package com.atlassian.maven.plugins.stash;

import com.atlassian.maven.plugins.amps.TestJarMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 3.10
 */
@Mojo(name = "test-jar",requiresDependencyResolution = ResolutionScope.TEST)
public class StashTestJarMojo extends TestJarMojo
{
}
