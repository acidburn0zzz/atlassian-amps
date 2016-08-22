package com.atlassian.maven.plugins.jira;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "fast-stop")
@Execute(phase = LifecyclePhase.NONE)
public class JiraFastStopMojo extends JiraStopMojo
{
}