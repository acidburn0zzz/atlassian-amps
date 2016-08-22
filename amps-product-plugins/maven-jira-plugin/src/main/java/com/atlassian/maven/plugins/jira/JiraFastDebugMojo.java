package com.atlassian.maven.plugins.jira;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "fast-debug", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.NONE)
public class JiraFastDebugMojo extends JiraDebugMojo
{
}