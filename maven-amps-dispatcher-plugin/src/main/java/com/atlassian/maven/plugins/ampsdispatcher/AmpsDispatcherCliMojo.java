package com.atlassian.maven.plugins.ampsdispatcher;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "cli", requiresDependencyResolution = ResolutionScope.TEST)
public class AmpsDispatcherCliMojo extends AbstractAmpsDispatcherMojo
{
}
