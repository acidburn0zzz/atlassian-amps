package com.atlassian.maven.plugins.confluence;

import com.atlassian.maven.plugins.amps.CopyBundledDependenciesMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "copy-bundled-dependencies", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ConfluenceCopyBundledDependenciesMojo extends CopyBundledDependenciesMojo
{
}
