package com.atlassian.maven.plugins.stash;

import com.atlassian.maven.plugins.amps.GenerateRestDocsMojo;

import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoRequiresDependencyResolution;

/**
 * @since 3.10
 */
@MojoGoal("generate-rest-docs")
@MojoRequiresDependencyResolution("test")
public class StashGenerateRestDocsMojo extends GenerateRestDocsMojo
{
}
