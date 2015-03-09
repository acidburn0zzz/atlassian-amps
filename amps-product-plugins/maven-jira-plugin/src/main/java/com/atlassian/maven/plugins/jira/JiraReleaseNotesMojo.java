package com.atlassian.maven.plugins.jira;

import com.atlassian.maven.plugins.amps.ReleaseNotesMojo;
import com.atlassian.maven.plugins.amps.TestJarMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "release-notes",requiresDependencyResolution = ResolutionScope.TEST)
public class JiraReleaseNotesMojo extends ReleaseNotesMojo
{
}
