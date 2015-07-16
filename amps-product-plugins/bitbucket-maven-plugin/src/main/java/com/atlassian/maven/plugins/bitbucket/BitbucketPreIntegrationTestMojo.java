package com.atlassian.maven.plugins.bitbucket;

import com.atlassian.maven.plugins.amps.PreIntegrationTestMojo;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 6.1.0
 */
@Mojo (name = "pre-integration-test", requiresDependencyResolution = ResolutionScope.TEST)
@Execute (phase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class BitbucketPreIntegrationTestMojo extends PreIntegrationTestMojo
{
}
