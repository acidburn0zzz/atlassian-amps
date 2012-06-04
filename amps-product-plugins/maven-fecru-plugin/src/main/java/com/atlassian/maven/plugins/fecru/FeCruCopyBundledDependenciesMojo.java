package com.atlassian.maven.plugins.fecru;

import com.atlassian.maven.plugins.amps.CopyBundledDependenciesMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "copy-bundled-dependencies", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class FeCruCopyBundledDependenciesMojo extends CopyBundledDependenciesMojo
{
}
