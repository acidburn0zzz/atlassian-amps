package com.atlassian.maven.plugins.ampsdispatcher;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Dispatches pre-integration-test to the appropriate amps product-specific
 */
@Mojo (name = "pre-integration-test", requiresDependencyResolution = ResolutionScope.TEST)
@Execute (phase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class AmpsDispatcherPreIntegrationTestMojo extends AbstractAmpsDispatcherMojo
{
}
