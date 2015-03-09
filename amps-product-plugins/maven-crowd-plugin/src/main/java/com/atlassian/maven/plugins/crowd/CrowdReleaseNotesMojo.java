package com.atlassian.maven.plugins.crowd;

import com.atlassian.maven.plugins.amps.ReleaseNotesMojo;
import com.atlassian.maven.plugins.amps.pdk.TestInstallMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "release-notes", requiresDependencyResolution = ResolutionScope.TEST)
public class CrowdReleaseNotesMojo extends ReleaseNotesMojo
{
}
