package com.atlassian.maven.plugins.stash;

import com.atlassian.maven.plugins.amps.CopyBundledDependenciesMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 3.10
 */
@Mojo(name = "copy-bundled-dependencies", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class StashCopyBundledDependenciesMojo extends CopyBundledDependenciesMojo
{
}
