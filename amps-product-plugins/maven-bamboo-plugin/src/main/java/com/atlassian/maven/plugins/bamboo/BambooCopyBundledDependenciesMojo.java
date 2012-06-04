package com.atlassian.maven.plugins.bamboo;

import com.atlassian.maven.plugins.amps.CopyBundledDependenciesMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "copy-bundled-dependencies", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class BambooCopyBundledDependenciesMojo extends CopyBundledDependenciesMojo
{
}
