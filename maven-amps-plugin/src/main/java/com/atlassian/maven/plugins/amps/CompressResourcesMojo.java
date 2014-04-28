package com.atlassian.maven.plugins.amps;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Compresses JavaScript resources with the YUI compressor Maven plugin.  Creates compressed versions of all
 * JavaScript resources by attaching the '-min' suffix.
 *
 * @since 3.2
 */
@Mojo(name = "compress-resources")
public class CompressResourcesMojo extends AbstractAmpsMojo
{
    /**
     * Whether to compress the resources or not.  Defaults to true.
     */
    @Parameter(defaultValue = "true")
    private boolean compressResources;

    @Parameter(defaultValue = "true")
    private boolean compressJs;

    @Parameter(defaultValue = "true")
    private boolean compressCss;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (compressResources)
        {
            getMavenGoals().compressResources(compressJs, compressCss, closureJsCompiler);
        }
        else
        {
            getLog().debug("Compressing resources disabled");
        }
    }
}
