package com.atlassian.maven.plugins.amps.osgi;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.maven.plugins.amps.AbstractAmpsMojo;

import com.google.common.collect.ImmutableMap;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import aQute.bnd.osgi.Constants;

import static com.atlassian.maven.plugins.amps.util.FileUtils.file;

@Mojo(name = "generate-test-manifest")
public class GenerateTestManifestMojo extends AbstractAmpsMojo
{
    private static final String BUILD_DATE_ATTRIBUTE = "Atlassian-Build-Date";

    private static final DateFormat BUILD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    /**
     * The BND instructions for the bundle.
     */
    @Parameter
    private Map<String, String> testInstructions = new HashMap<String, String>();

    @Parameter(property = "project.build.finalName")
    private String finalName;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if(shouldBuildTestPlugin())
        {
            final MavenProject project = getMavenContext().getProject();
    
            // The Atlassian-Build-Date manifest attribute is used by the Atlassian licensing framework to determine
            // chronological order of bundle versions.
            final String buildDateStr = String.valueOf(BUILD_DATE_FORMAT.format(new Date()));
            final Map<String, String> basicAttributes = ImmutableMap.of(BUILD_DATE_ATTRIBUTE, buildDateStr);
    
            if (!testInstructions.isEmpty())
            {
                getLog().info("Generating a manifest for this test plugin");

                if (!testInstructions.containsKey(Constants.BUNDLE_SYMBOLICNAME))
                {
                    testInstructions.put(Constants.BUNDLE_SYMBOLICNAME, finalName + "-tests");
                }
                else if(!testInstructions.get(Constants.BUNDLE_SYMBOLICNAME).endsWith("-tests"))
                {
                    testInstructions.put(Constants.BUNDLE_SYMBOLICNAME, testInstructions.get(Constants.BUNDLE_SYMBOLICNAME) + "-tests");
                }

                if (!testInstructions.containsKey(Constants.BUNDLE_NAME))
                {
                    testInstructions.put(Constants.BUNDLE_NAME, getMavenContext().getProject().getName() + " Tests");
                }
                
                if (!testInstructions.containsKey(Constants.EXPORT_PACKAGE))
                {
                    testInstructions.put(Constants.EXPORT_PACKAGE, "");
                }
    
                File metainfLib = file(project.getBuild().getTestOutputDirectory(), "META-INF", "lib");
                if (metainfLib.exists())
                {
                    StringBuilder sb = new StringBuilder(".");
                    for (File lib : metainfLib.listFiles())
                    {
                        sb.append(",").append("META-INF/lib/" + lib.getName());
                    }
                    testInstructions.put(Constants.BUNDLE_CLASSPATH, sb.toString());
                }
                getMavenGoals().generateTestBundleManifest(testInstructions, basicAttributes);
            }
            else
            {
                getMavenGoals().generateTestMinimalManifest(basicAttributes);
            }
        }
    }
}
