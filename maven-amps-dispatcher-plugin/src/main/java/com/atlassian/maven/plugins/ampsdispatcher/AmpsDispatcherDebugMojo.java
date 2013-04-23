package com.atlassian.maven.plugins.ampsdispatcher;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Debug the webapp
 */
@Mojo(name = "debug", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.PACKAGE)
public class AmpsDispatcherDebugMojo extends AbstractAmpsDispatcherMojo
{
}
