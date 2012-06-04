package com.atlassian.maven.plugins.stash;

import com.atlassian.maven.plugins.amps.UnitTestMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 3.10
 */
@Mojo(name = "unit-test", requiresDependencyResolution = ResolutionScope.TEST)
public class StashUnitTestMojo extends UnitTestMojo
{
}
