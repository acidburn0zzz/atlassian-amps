package com.atlassian.maven.plugins.amps.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.google.common.collect.Lists;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import static com.atlassian.maven.plugins.amps.util.FileUtils.file;

/**
 * Utility methods dealing with Maven projects
 *
 * @since 3.3
 */
public class ProjectUtils
{
    /**
     * The name of the text file that store checksum of pom.xl files. By default, this file is saved in the project
     * build directory (target).
     */
    public static final String POM_CHECKSUM_FILE = "pom-checksum.txt";

    /**
     * @return If the test jar should be built based on atlassian-plugin.xml residing in src/test/resources
     */
    public static boolean shouldDeployTestJar(MavenContext context)
    {
        File testResources = file(context.getProject().getBasedir(),"src","test","resources");
        File pluginXml = new File(testResources,"atlassian-plugin.xml");

        return pluginXml.exists();
    }

    /**
     * Returns the first non null value. Use this to default values.
     * @return the first non null value of values
     * @throws NullPointerException if all values are null
     *
     * Note: this is a copy of Objects#firstNonNull in Guava release 03.
     */
    public static <T> T firstNotNull(T... values)
    {
        for (T value : values)
        {
            if (value != null)
            {
                return value;
            }
        }
        throw new NullPointerException("All values are null");
    }

    public final static File createDirectory(File dir)
    {
        if (!dir.exists() && !dir.mkdirs())
        {
            throw new RuntimeException("Failed to create directory " + dir.getAbsolutePath());
        }
        return dir;
    }

    /**
     * Attempt to retrieve an artifact matching the group ID and artifact ID requested.
     *
     * @return null if not found
     */
    public static Artifact getReactorArtifact(MavenContext context, String groupId, String artifactId)
    {
        MavenProject project = context.getProject();
        for (Artifact artifact : project.getArtifacts())
        {
            if (artifact.getGroupId().equals(groupId) && artifact.getArtifactId().equals(artifactId))
            {
                return artifact;
            }
        }
        return null;
    }


    /**
     * Calculates file checksum of the pom.xml file of the given project toghether with its parent pom.xml
     *
     * @see ProjectUtils#calculateAndWriteProjectPomFileChecksum(MavenProject)
     * @see ProjectUtils#isProjectPomFileChecksumChanged(MavenProject)
     * @see ProjectUtils#getProjectPomChecksumFile(MavenProject)
     */
    public static String calculateChecksumOfProjectPomFile(MavenProject project)
    {
        File pluginPomXml = project.getFile();
        File parentPomXml = project.getParentFile();

        try
        {
            ArrayList<File> files = Lists.newArrayList(pluginPomXml);
            if (parentPomXml != null)
            {
                files.add(parentPomXml);
            }

            return FileUtils.calculateFileChecksum(files);
        }
        catch (IOException e)
        {
            throw new RuntimeException("failed to calculate checksum for pom.xml files", e);
        }
    }

    /**
     * Checks that if the checksum of the pom.xml file of the given project is different from the checksum value stored
     * in the checksum file. The checksum file is the one returned by {@link ProjectUtils#getProjectPomChecksumFile(MavenProject)}
     * If the checksum file does not exist, it will be considered changed.
     *
     * @see ProjectUtils#calculateChecksumOfProjectPomFile(MavenProject)
     * @see ProjectUtils#calculateAndWriteProjectPomFileChecksum(MavenProject)
     * @see ProjectUtils#getProjectPomChecksumFile(MavenProject)
     */
    public static boolean isProjectPomFileChecksumChanged(MavenProject project)
    {
        File checksumFile = getProjectPomChecksumFile(project);
        String currentPomFileChecksum = calculateChecksumOfProjectPomFile(project);

        try
        {
            return !FileUtils.contentEquals(checksumFile, currentPomFileChecksum);
        }
        catch (IOException e)
        {
            throw new RuntimeException("failed to check checksum of pom.xml files", e);
        }
    }

    /**
     * @return the default file the store checksum of the project's pom.xml file
     */
    public static File getProjectPomChecksumFile(MavenProject project)
    {
        return new File(project.getBuild().getDirectory(), POM_CHECKSUM_FILE);
    }

    /**
     * Calculates POM checksum of the given project and write the value to a file.
     *
     * @see ProjectUtils#calculateChecksumOfProjectPomFile(MavenProject)
     * @see ProjectUtils#getProjectPomChecksumFile(MavenProject)
     */
    public static void calculateAndWriteProjectPomFileChecksum(MavenProject project)
    {
        File checksumFile = getProjectPomChecksumFile(project);
        try
        {
            org.apache.commons.io.FileUtils.writeStringToFile(checksumFile, calculateChecksumOfProjectPomFile(project));
        }
        catch (IOException e)
        {
            throw new RuntimeException("failed to write project pom.xml checksum to file", e);
        }
    }


}
