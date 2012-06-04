package com.atlassian.maven.plugins.stash;

import com.atlassian.maven.plugins.amps.GenerateRestDocsMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 3.10
 */
@Mojo(name = "generate-rest-docs", requiresDependencyResolution = ResolutionScope.TEST)
public class StashGenerateRestDocsMojo extends GenerateRestDocsMojo
{
}
