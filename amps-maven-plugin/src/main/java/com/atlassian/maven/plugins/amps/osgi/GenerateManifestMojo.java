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
import aQute.bnd.osgi.Analyzer;

import static com.atlassian.maven.plugins.amps.util.FileUtils.file;

@Mojo(name = "generate-manifest")
public class GenerateManifestMojo extends AbstractAmpsMojo
{
    private static final String BUILD_DATE_ATTRIBUTE = "Atlassian-Build-Date";

    private static final DateFormat BUILD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    /**
     * The BND instructions for the bundle.
     */
    @Parameter
    private Map<String, String> instructions = new HashMap<String, String>();

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        final MavenProject project = getMavenContext().getProject();

        // The Atlassian-Build-Date manifest attribute is used by the Atlassian licensing framework to determine
        // chronological order of bundle versions.
        final String buildDateStr = String.valueOf(BUILD_DATE_FORMAT.format(new Date()));
        final Map<String, String> basicAttributes = ImmutableMap.of(BUILD_DATE_ATTRIBUTE, buildDateStr);

        if (!instructions.isEmpty())
        {
            getLog().info("Generating a manifest for this plugin");

            if (!instructions.containsKey(Constants.EXPORT_PACKAGE))
            {
                instructions.put(Constants.EXPORT_PACKAGE, "");
            }

            File metainfLib = file(project.getBuild().getOutputDirectory(), "META-INF", "lib");

            if (!instructions.containsKey(Analyzer.NOEE)) {
                instructions.put(Analyzer.NOEE, Boolean.TRUE.toString());
            }

            if (metainfLib.exists())
            {
                StringBuilder sb = new StringBuilder(".");
                for (File lib : metainfLib.listFiles())
                {
                    sb.append(",").append("META-INF/lib/" + lib.getName());
                }
                instructions.put(Constants.BUNDLE_CLASSPATH, sb.toString());
            }
            getMavenGoals().generateBundleManifest(instructions, basicAttributes);
        }
        else
        {
            getMavenGoals().generateMinimalManifest(basicAttributes);
        }
    }
}
