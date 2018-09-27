package com.atlassian.maven.plugins.amps;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import static com.atlassian.maven.plugins.amps.util.FileUtils.file;

@Mojo(name = "jar")
public class JarWithManifestMojo extends AbstractAmpsMojo
{
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        File mf = file(getMavenContext().getProject().getBuild().getOutputDirectory(), "META-INF", "MANIFEST.MF");
        getMavenGoals().jarWithOptionalManifest(mf.exists());
    }
}
