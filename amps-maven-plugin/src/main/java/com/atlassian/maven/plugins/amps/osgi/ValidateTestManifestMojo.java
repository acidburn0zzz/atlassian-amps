package com.atlassian.maven.plugins.amps.osgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.maven.plugins.amps.AbstractAmpsMojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static com.atlassian.maven.plugins.amps.util.FileUtils.file;

@Mojo(name = "validate-test-manifest")
public class ValidateTestManifestMojo extends AbstractAmpsMojo
{
    /**
     * Whether to skip validation or not
     */
    @Parameter(property = "manifest.validation.skip")
    protected boolean skipManifestValidation = false;

    /**
     * The BND instructions for the bundle.  We'll only validate the import versions if there was an
     * explicit Import-Package list, not if we auto-generated the imports.
     */
    @Parameter
    private Map<String, String> testInstructions = new HashMap<>();

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (shouldBuildTestPlugin())
        {
            final File mfile = file(getMavenContext().getProject().getBuild().getTestOutputDirectory(), "META-INF", "MANIFEST.MF");
    
            // Only valid if the manifest exists
            if (!skipManifestValidation && mfile.exists())
            {
                getLog().info("Manifest found, validating...");
                try
                {
                    checkManifestEndsWithNewLine(mfile);
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException("Unable to read manifest", e);
                }
                getLog().info("Manifest validated");
            }
            else
            {
                getLog().info("No manifest found or validation skip flag specified, skipping validation");
            }
        }
    }

    private void checkManifestEndsWithNewLine(final File mfile)
            throws IOException, MojoExecutionException, MojoFailureException
    {
        try (InputStream is = new FileInputStream(mfile))
        {
            final long bytesToSkip = mfile.length() - 1;
            long bytesSkipped = is.skip(bytesToSkip);
            if (bytesSkipped != bytesToSkip)
            {
                throw new MojoExecutionException("Could not skip " + bytesToSkip + " bytes reading " + mfile.getAbsolutePath());
            }
            else if (is.read() != '\n')
            {
                throw new MojoFailureException("Manifests must end with a new line. " + mfile.getAbsolutePath() + " doesn't.");
            }
        }
    }
}
