package com.atlassian.maven.plugins.amps.util;

import static com.atlassian.maven.plugins.amps.util.FileUtils.copyDirectory;
import static com.atlassian.maven.plugins.amps.util.FileUtils.doesFileNameMatchArtifact;
import static com.atlassian.maven.plugins.amps.util.FileUtils.file;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TestFileUtils extends TestCase
{
    public void testFile()
    {
        File parent = new File("bob");
        assertEquals(new File(parent, "jim").getAbsolutePath(), file(parent, "jim").getAbsolutePath());

        assertEquals(new File(new File(parent, "jim"), "sarah").getAbsolutePath(),
                file(parent, "jim", "sarah").getAbsolutePath());
    }

    public void testDoesFileNameMatcheArtifact()
    {
        assertTrue(doesFileNameMatchArtifact("sal-crowd-plugin-2.0.7.jar", "sal-crowd-plugin"));
        assertFalse(doesFileNameMatchArtifact("sal-crowd-plugin-2.0.7.jar", "crowd-plugin"));
    }

    public void testCopyDirectory() throws IOException
    {
        File src = tempDirectory();
        File dest = tempDirectory();
        try
        {
            src.mkdirs();
            File file = new File(src, "something");
            file.createNewFile();
            // Ignore the executable assert on Windows
            boolean executable = file.setExecutable(true);
            new File(src, "a/b").mkdirs();
            new File(src, "a/b/c").createNewFile();
            new File(src, "a/d").createNewFile();
            copyDirectory(src, dest, true);
            assertTrue(new File(dest, "a/b/c").exists());
            assertTrue(new File(dest, "a/d").exists());
            assertEquals(executable, new File(dest, "a/d").canExecute());
            assertEquals(executable, new File(dest, file.getName()).canExecute());
        }
        finally
        {
            FileUtils.deleteDir(src);
            FileUtils.deleteDir(dest);
        }
    }

    private static File tempDirectory()
    {
        return new File(new File(System.getProperty("java.io.tmpdir")), UUID.randomUUID().toString());
    }
}
