package com.atlassian.maven.plugins.crowd;

import com.atlassian.maven.plugins.amps.TestJarMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "test-jar",requiresDependencyResolution = ResolutionScope.TEST)
public class CrowdTestJarMojo extends TestJarMojo
{
}
