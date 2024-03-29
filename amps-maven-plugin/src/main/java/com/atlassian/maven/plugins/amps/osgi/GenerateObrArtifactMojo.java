package com.atlassian.maven.plugins.amps.osgi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.maven.plugins.amps.AbstractAmpsMojo;

import org.apache.commons.io.FileUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

/**
 * Generates the obr artifact, containing the plugin, its dependencies, and the obr XML file.  The OBR file looks like
 * this:
 * <p/>
 * <pre>
 * this-plugin.jar
 * obr.xml
 * dependencies/required-plugin.jar
 * </pre>
 * <p/>
 * All plugins in the root directory will be installed, while the ones in the "dependencies" directory will be installed
 * only if they are needed.
 */
@Mojo(name = "generate-obr-artifact")
public class GenerateObrArtifactMojo extends AbstractAmpsMojo
{
    @Parameter
    private List<PluginDependency> pluginDependencies = new ArrayList<PluginDependency>();

    /**
     * The Jar archiver.
     */
    @Component(role = Archiver.class, hint = "jar")
    private JarArchiver jarArchiver;

    /**
     * The archive configuration to use. See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven
     * Archiver Reference</a>.
     */
    @Parameter
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * Specifies whether or not to attach the artifact to the project
     */
    @Parameter(property = "attach", defaultValue = "true")
    private boolean attach;

    @Component
    private MavenProjectHelper projectHelper;

    /**
     * The directory where the generated archive file will be put.
     */
    @Parameter(property = "project.build.directory")
    protected File outputDirectory;

    /**
     * The filename to be used for the generated archive file.  The "-obr" suffix will be appended.
     */
    @Parameter(property = "project.build.finalName")
    protected String finalName;

    /**
     * Contains the full list of projects in the reactor.
     */
    @Parameter(property = "reactorProjects", readonly = true)
    protected List<MavenProject> reactorProjects;

    @Parameter
    private Map instructions = new HashMap();

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            if (!instructions.isEmpty()) {
                Build build = getMavenContext().getProject().getBuild();
                List<File> deps = resolvePluginDependencies();
                getLog().info("the file name is  : " +this.finalName);
                File obrDir = layoutObr(deps, new File(build.getDirectory(), finalName + ".jar"));

                generateObrZip(obrDir);
            } else {
                getLog().info("Skipping OBR generation... no OSGi bundle manifest instructions found in pom.xml");
            }
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * @param obrDir Directory containing the files to go into the obr zip
     * @throws MojoExecutionException If something goes wrong
     */
    private void generateObrZip(File obrDir) throws MojoExecutionException
    {
        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(jarArchiver);
        File outputFile = new File(outputDirectory, finalName + ".obr");
        final MavenProject mavenProject = getMavenContext().getProject();
        try
        {
            archiver.getArchiver().addDirectory(obrDir, "");
            archiver.setOutputFile(outputFile);

            archive.setAddMavenDescriptor(false);

            // todo: be smarter about when this is updated
            archive.setForced(true);

            archiver.createArchive(mavenProject, archive);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Error creating obr archive: " + e.getMessage(), e);
        }
        catch (ArchiverException e)
        {
            throw new MojoExecutionException("Error creating obr archive: " + e.getMessage(), e);
        }
        catch (DependencyResolutionRequiredException e)
        {
            throw new MojoExecutionException("Error creating obr archive: " + e.getMessage(), e);
        }
        catch (ManifestException e)
        {
            throw new MojoExecutionException("Error creating obr archive: " + e.getMessage(), e);
        }

        if (attach)
        {
            projectHelper.attachArtifact(mavenProject, getType(), outputFile);
        }
        else
        {
            getLog().info("NOT adding " + getType() + " to attached artifacts list, so it won't be installed or deployed.");
        }
    }

    /**
     * Creates a directory containing the files that will be in the obr artifact.
     *
     * @param deps The dependencies for this artifact
     * @param mainArtifact The main artifact file
     * @return The directory containing the future obr zip contents
     * @throws IOException If the files cannot be copied
     * @throws MojoExecutionException If the dependencies cannot be retrieved
     */
    private File layoutObr(List<File> deps, File mainArtifact) throws MojoExecutionException, IOException
    {
        // create directories
        File obrDir = new File(getMavenContext().getProject().getBuild().getDirectory(), "obr");
        obrDir.mkdir();
        File depDir = new File(obrDir, "dependencies");
        depDir.mkdir();

        // Copy in the dependency plugins for the obr generation
        for (File dep : deps)
        {
            FileUtils.copyFileToDirectory(dep, depDir, true);
        }

        // Generate the obr xml
        File obrXml = new File(obrDir, "obr.xml");
        for (File dep : depDir.listFiles())
        {
            getMavenGoals().generateObrXml(dep, obrXml);
        }

        // Copy the main artifact over
        File mainArtifactCopy = new File(obrDir, mainArtifact.getName());
        FileUtils.copyFile(mainArtifact, mainArtifactCopy);

        // Generate the obr xml for the main artifact
        // The File must be the one copied into the obrDir (see AMPS-300)
        getMavenGoals().generateObrXml(mainArtifactCopy, obrXml);

        return obrDir;
    }

    private List<File> resolvePluginDependencies()
    {
        List<File> deps = new ArrayList<File>();
        for (Artifact artifact : (Set<Artifact>) getMavenContext().getProject().getDependencyArtifacts())
        {
            if (pluginDependencies.contains(new PluginDependency(artifact.getGroupId(), artifact.getArtifactId())))
            {
                deps.add(artifact.getFile());
            }
        }
        return deps;
    }

    protected String getType()
    {
        return "obr";
    }
}
