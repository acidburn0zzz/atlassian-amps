package com.atlassian.maven.plugins.fecru;

import com.atlassian.maven.plugins.amps.RemoteTestMojo;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "remote-test", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
@Execute(phase = LifecyclePhase.PACKAGE)
public class FeCruRemoteTestMojo extends RemoteTestMojo
{

}
