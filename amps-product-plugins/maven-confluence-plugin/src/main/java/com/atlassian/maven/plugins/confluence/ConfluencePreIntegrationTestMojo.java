package com.atlassian.maven.plugins.confluence;

import com.atlassian.maven.plugins.amps.PreIntegrationTestMojo;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo (name = "pre-integration-test", requiresDependencyResolution = ResolutionScope.TEST)
@Execute (phase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class ConfluencePreIntegrationTestMojo extends PreIntegrationTestMojo
{
}
