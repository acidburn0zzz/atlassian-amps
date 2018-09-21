package com.atlassian.maven.plugins.refapp;

import com.atlassian.maven.plugins.amps.UnitTestMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "unit-test", requiresDependencyResolution = ResolutionScope.TEST)
public class RefappUnitTestMojo extends UnitTestMojo
{
}