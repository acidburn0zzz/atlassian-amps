package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.IOException;

import com.atlassian.maven.plugins.amps.util.ProjectUtils;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static com.atlassian.maven.plugins.amps.util.FileUtils.file;


/**
 * Jars the tests into an OSGi bundle.  Only builds the jar if the {@link #buildTestPlugin} flag is set or it detects
 * an atlassian-plugin.xml file in the target/test-classes directory.
 *
 * Note, this test jar will not have its resources filtered or a manifest generated for it.  If no manifest is present,
 * a dummy manifest that does a dynamic package import on everything will be used.
 *
 * @since 3.3
 */
@Mojo(name = "test-jar")
public class TestJarMojo extends AbstractAmpsMojo
{

    /**
     * Whether the test plugin should be built or not.  If not specified, it detects an atlassian-plugin.xml in the
     * test classes directory and builds if exists.
     */
    @Parameter
    private Boolean buildTestPlugin;

    /**
     * The final name for the test plugin, without the "-tests" suffix.
     */
    @Parameter(property = "project.build.finalName")
    private String finalName;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        
        if (shouldBuildTestPlugin())
        {
            File mf = file(getMavenContext().getProject().getBuild().getTestOutputDirectory(), "META-INF", "MANIFEST.MF");
            if (!mf.exists())
            {
                try
                {
                    FileUtils.writeStringToFile(mf,
                           "Manifest-Version: 1.0\n" +
                           "Bundle-SymbolicName: plugin-tests\n" +
                           "Bundle-Version: 1.0\n" +
                           "Bundle-Name: " + finalName + "-tests\n" +
                           "DynamicImport-Package: *\n");
                }
                catch (IOException e)
                {
                    throw new MojoFailureException("Unable to write manifest");
                }
            }
            getMavenGoals().jarTests(finalName);
        }
    }
}

