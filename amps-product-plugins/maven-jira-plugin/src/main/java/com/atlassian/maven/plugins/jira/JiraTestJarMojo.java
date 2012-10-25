package com.atlassian.maven.plugins.jira;

import com.atlassian.maven.plugins.amps.TestJarMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "test-jar",requiresDependencyResolution = ResolutionScope.TEST)
public class JiraTestJarMojo extends TestJarMojo
{
}
