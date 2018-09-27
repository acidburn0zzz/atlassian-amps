package com.atlassian.maven.plugins.crowd;

import com.atlassian.maven.plugins.amps.UnitTestMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "unit-test", requiresDependencyResolution = ResolutionScope.TEST)
public class CrowdUnitTestMojo extends UnitTestMojo
{
}
