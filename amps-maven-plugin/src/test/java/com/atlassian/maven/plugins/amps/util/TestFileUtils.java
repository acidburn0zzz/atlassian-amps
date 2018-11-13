package com.atlassian.maven.plugins.amps.util;

import static com.atlassian.maven.plugins.amps.util.FileUtils.copyDirectory;
import static com.atlassian.maven.plugins.amps.util.FileUtils.doesFileNameMatchArtifact;
import static com.atlassian.maven.plugins.amps.util.FileUtils.file;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestFileUtils
{
    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testFile()
    {
        File parent = new File("bob");
        assertEquals(new File(parent, "jim").getAbsolutePath(), file(parent, "jim").getAbsolutePath());

        assertEquals(new File(new File(parent, "jim"), "sarah").getAbsolutePath(),
                file(parent, "jim", "sarah").getAbsolutePath());
    }

    @Test
    public void testDoesFileNameMatchArtifact()
    {
        assertTrue(doesFileNameMatchArtifact("sal-crowd-plugin-2.0.7.jar", "sal-crowd-plugin"));
        assertFalse(doesFileNameMatchArtifact("sal-crowd-plugin-2.0.7.jar", "crowd-plugin"));
    }

    @Test
    public void testCopyDirectory() throws IOException
    {
        File src = tempDir.newFolder("src");
        File dest = tempDir.newFolder("dst");
        try
        {
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
            if (SystemUtils.IS_OS_WINDOWS)
            {
                assertEquals(executable, new File(dest, "a/d").canExecute());
            }
            else
            {
                assertFalse(new File(dest, "a/d").canExecute());
            }
            assertEquals(executable, new File(dest, file.getName()).canExecute());
        }
        finally
        {
            FileUtils.deleteDir(src);
            FileUtils.deleteDir(dest);
        }
    }

    @Test
    public void testReadFileToString()
    {
        // Invoke
        final String actualText = FileUtils.readFileToString("TestFileUtils.txt", getClass(), UTF_8);

        // Check
        final String expectedText = "\nThis file is for reading by TestFileUtils#testReadFileToString.\n" +
                "It has multiple lines, and leading/trailing whitespace.\n";
        assertThat(actualText, is(expectedText));
    }
}
