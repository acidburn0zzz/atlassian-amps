package com.atlassian.maven.plugins.bamboo;

import com.atlassian.maven.plugins.amps.PrepareDatabaseMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo (name = "prepare-database", requiresDependencyResolution = ResolutionScope.TEST)
public class BambooPrepareDatabaseMojo extends PrepareDatabaseMojo
{
}
