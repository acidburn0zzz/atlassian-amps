package com.atlassian.maven.plugins.fecru;

import com.atlassian.maven.plugins.amps.UnitTestMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "unit-test", requiresDependencyResolution = ResolutionScope.TEST)
public class FeCruUnitTestMojo extends UnitTestMojo
{
}
