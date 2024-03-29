package com.atlassian.maven.plugins.amps.util;

import java.io.File;
import java.util.List;

import com.atlassian.maven.plugins.amps.MavenContext;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import org.apache.maven.model.Resource;

import static com.atlassian.maven.plugins.amps.util.FileUtils.file;

/**
 * Utility methods dealing with Maven projects
 *
 * @since 3.3
 */
public class ProjectUtils
{

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
}
