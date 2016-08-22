package com.atlassian.maven.plugins.jira;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "fast-run", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.NONE)
public class JiraFastRunMojo extends JiraRunMojo
{
}

