package com.atlassian.maven.plugins.refapp;

import com.atlassian.maven.plugins.amps.CopyBundledDependenciesMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Copies bundled dependencies into META-INF/lib
 */
@Mojo(name = "copy-bundled-dependencies", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class RefappCopyBundledDependenciesMojo extends CopyBundledDependenciesMojo
{
}
