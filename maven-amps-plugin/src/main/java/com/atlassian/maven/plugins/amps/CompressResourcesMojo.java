package com.atlassian.maven.plugins.amps;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

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
            final Charset cs; 
            if (encoding == null)
            {
                cs = Charset.defaultCharset();
                getLog().warn( "File encoding has not been set, using platform encoding " + cs.name() + ", i.e. build is platform dependent!" );
            }
            else
            {
                try
                {
                    cs = Charset.forName(encoding);
                }
                catch (IllegalCharsetNameException ex)
                {
                    throw new MojoExecutionException("Failed to resolve charset: "+encoding, ex);
                }
                catch (UnsupportedCharsetException ex)
                {
                    throw new MojoExecutionException("Failed to resolve charset: "+encoding, ex);
                }
            }
            getMavenGoals().compressResources(compressJs, compressCss, closureJsCompiler, cs);
        }
        else
        {
            getLog().debug("Compressing resources disabled");
        }
    }
}
