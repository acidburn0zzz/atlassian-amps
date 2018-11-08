package com.atlassian.maven.plugins.amps.util;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("Duplicates")
public class TestZipUtils
{
    private static final int NUM_FILES = 2;
    private static final int NUM_FOLDERS = 4;
    private static final int NUM_FOLDERS_NESTED_PREFIX = NUM_FOLDERS + 1;

    private static final String ROOT_DIR = "test-zip-dir";
    private static final String FIRST_PREFIX = "prefix1";
    private static final String SECOND_PREFIX = "prefix2";
    private static final String NESTED_PREFIX = FIRST_PREFIX + "/" + SECOND_PREFIX;

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    private File sourceZipDir;

    @Before
    public void ensureDirsExist() throws IOException
    {
        // Create our test source tree
        sourceZipDir = tempDir.newFolder(ROOT_DIR);
        tempDir.newFolder(ROOT_DIR, "level2sub1");

        File level2sub2 = tempDir.newFolder(ROOT_DIR, "level2sub2");
        File level2TextFile = new File(level2sub2, "level2sub2.txt");
        FileUtils.writeStringToFile(level2TextFile, "level2sub2", StandardCharsets.UTF_8);

        File level3sub1 = tempDir.newFolder(ROOT_DIR, "level2sub2", "level3sub1");
        File level3TextFile = new File(level3sub1, "level3sub1.txt");
        FileUtils.writeStringToFile(level3TextFile, "level3sub1", StandardCharsets.UTF_8);
    }

    @Test
    public void zipChildrenHasNoPrefix() throws IOException
    {
        File zipFile = tempDir.newFile("zip-children.zip");
        ZipUtils.zipChildren(zipFile, sourceZipDir);

        try (ZipFile zip = new ZipFile(zipFile))
        {
            List<String> entries = Collections.list(zip.entries()).stream()
                    .map(ZipEntry::getName)
                    .collect(toList());

            // - Directories should have explicit entries
            // - Entries should be relative to _without including_ sourceZipDir (test-zip-dir)
            assertEquals(5, entries.size());
            assertEquals("level2sub1/", entries.get(0));
            assertEquals("level2sub2/", entries.get(1));
            assertEquals("level2sub2/level3sub1/", entries.get(2));
            assertEquals("level2sub2/level3sub1/level3sub1.txt", entries.get(3));
            assertEquals("level2sub2/level2sub2.txt", entries.get(4));
        }
    }

    @Test
    public void zipContainsSinglePrefix() throws IOException
    {
        File zipFile = tempDir.newFile("zip-with-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, FIRST_PREFIX);

        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                String zipPath = zipEntry.getName();
                String testPrefix = zipPath.substring(0, zipPath.indexOf("/"));

                assertEquals(FIRST_PREFIX, testPrefix);
            }
        }
    }

    @Test
    public void zipContainsNestedPrefix() throws IOException
    {
        File zipFile = tempDir.newFile("zip-nested-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, NESTED_PREFIX);

        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                String zipPath = zipEntry.getName();
                String[] segments = zipPath.split("/");
                if (segments.length > 1)
                {
                    String testPrefix = segments[0] + "/" + segments[1];

                    assertEquals(NESTED_PREFIX, testPrefix);
                }
            }
        }
    }

    @Test
    public void prefixedZipDoesNotContainRootDir() throws IOException
    {
        File zipFile = tempDir.newFile("zip-with-prefix-no-root.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, FIRST_PREFIX);

        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                String zipPath = zipEntry.getName();
                String[] segments = zipPath.split("/");
                if (segments.length > 1)
                {
                    String rootPath = segments[1];

                    assertThat(rootPath, not(equalTo(ROOT_DIR)));
                }
            }
        }
    }

    @Test
    public void nestedPrefixedZipDoesNotContainRootDir() throws IOException
    {
        File zipFile = tempDir.newFile("zip-nested-prefix-no-root.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, NESTED_PREFIX);

        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                String zipPath = zipEntry.getName();
                String[] segments = zipPath.split("/");
                if (segments.length > 2)
                {
                    String rootPath = segments[2];

                    assertThat(rootPath, not(equalTo(ROOT_DIR)));
                }
            }
        }
    }

    @Test
    public void emptyPrefixedZipContainsRootDir() throws IOException
    {
        File zipFile = tempDir.newFile("zip-empty-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, "");

        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                String zipPath = zipEntry.getName();
                String rootPath = zipPath.substring(0, zipPath.indexOf("/"));

                assertEquals(ROOT_DIR, rootPath);
            }
        }
    }

    @Test
    public void nullPrefixedZipContainsRootDir() throws IOException
    {
        File zipFile = tempDir.newFile("zip-null-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, null);

        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                String zipPath = zipEntry.getName();
                String rootPath = zipPath.substring(0, zipPath.indexOf("/"));

                assertEquals(ROOT_DIR, rootPath);
            }
        }
    }

    @Test
    public void emptyPrefixedZipFolderCountMatches() throws IOException
    {
        File zipFile = tempDir.newFile("zip-empty-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, "");

        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            int numFolders = 0;
            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.isDirectory())
                {
                    numFolders++;
                }
            }

            assertEquals(NUM_FOLDERS, numFolders);
        }
    }

    @Test
    public void singlePrefixedZipFolderCountMatches() throws IOException
    {
        File zipFile = tempDir.newFile("zip-single-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, FIRST_PREFIX);

        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            int numFolders = 0;
            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.isDirectory())
                {
                    numFolders++;
                }
            }

            assertEquals(NUM_FOLDERS, numFolders);
        }
    }

    @Test
    public void nestedPrefixedZipFolderCountMatches() throws IOException
    {
        File zipFile = tempDir.newFile("zip-nested-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, NESTED_PREFIX);

        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            int numFolders = 0;
            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.isDirectory())
                {
                    numFolders++;
                }
            }

            assertEquals(NUM_FOLDERS_NESTED_PREFIX, numFolders);
        }
    }

    @Test
    public void zipFileCountMatches() throws IOException
    {
        File zipFile = tempDir.newFile("zip-single-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, FIRST_PREFIX);

        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            int numFiles = 0;
            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                if (!zipEntry.isDirectory())
                {
                    numFiles++;
                }
            }

            assertEquals(NUM_FILES, numFiles);
        }
    }

    @Test
    public void unzipNonPrefixed() throws IOException
    {
        File zipFile = tempDir.newFile("zip-empty-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, "");

        File unzipDir = tempDir.newFolder("unzip-empty-prefix");
        ZipUtils.unzip(zipFile, unzipDir.getAbsolutePath());

        File rootUnzip = new File(unzipDir, ROOT_DIR);

        assertTrue("root folder in zip was not unzipped", (rootUnzip.exists() && rootUnzip.isDirectory()));
    }

    @Test
    public void unzipSinglePrefix() throws IOException
    {
        File zipFile = tempDir.newFile("zip-single-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, FIRST_PREFIX);

        File unzipDir = tempDir.newFolder("unzip-single-prefix");
        ZipUtils.unzip(zipFile, unzipDir.getAbsolutePath());

        File rootUnzip = new File(unzipDir, FIRST_PREFIX);

        assertTrue("single prefix folder in zip was not unzipped", (rootUnzip.exists() && rootUnzip.isDirectory()));
    }

    @Test
    public void unzipNestedPrefix() throws IOException
    {
        File zipFile = tempDir.newFile("zip-nested-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, NESTED_PREFIX);

        File unzipDir = tempDir.newFolder("unzip-nested-prefix");
        ZipUtils.unzip(zipFile, unzipDir.getAbsolutePath());

        File rootUnzip = new File(unzipDir, FIRST_PREFIX);
        File nestedUnzip = new File(rootUnzip, SECOND_PREFIX);

        assertTrue("nested prefix folder in zip was not unzipped", (nestedUnzip.exists() && nestedUnzip.isDirectory()));
    }

    @Test
    public void detectPrefix() throws IOException
    {
        File zipFile = tempDir.newFile("zip-single-prefix.zip");
        // zipDir will use the foldername as a prefix
        ZipUtils.zipDir(zipFile, sourceZipDir, "");

        int nestedRoots = ZipUtils.countNestingLevel(zipFile);

        assertEquals("One level of nesting should be detected", 1, nestedRoots);
    }

    @Test
    public void detectDoublePrefix() throws IOException
    {
        File zipFile = tempDir.newFile("zip-double-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, NESTED_PREFIX);

        int nestedRoots = ZipUtils.countNestingLevel(zipFile);

        assertEquals("Two levels of nesting should be detected", 2, nestedRoots);
    }

    @Test
    public void detectNoPrefix() throws IOException, URISyntaxException
    {
        // zip-no-root.zip is a zip with no root folder.
        // We can't use ZipUtils#zipDir() to create it (zipDir() always puts a root folder),
        // so we need to provide one in src/test/resources.
        URL zipPath = TestZipUtils.class.getResource("zip-no-root.zip");
        File zipFile = new File(zipPath.toURI());

        int nestedRoots = ZipUtils.countNestingLevel(zipFile);

        assertEquals("No nesting should be detected", 0, nestedRoots);
    }

    @Test
    public void unzipSinglePrefixTrimmed() throws IOException
    {
        File zipFile = tempDir.newFile("zip-single-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, FIRST_PREFIX);

        File unzipDir = tempDir.newFolder("unzip-single-prefix");
        ZipUtils.unzip(zipFile, unzipDir.getAbsolutePath(), 1);

        File rootUnzip = new File(unzipDir, FIRST_PREFIX);

        assertTrue("single prefix folder in zip should have been trimmed", !rootUnzip.exists());
    }

    @Test
    public void unzipNestedPrefixTrimmed() throws IOException
    {
        File zipFile = tempDir.newFile("zip-nested-prefix.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, NESTED_PREFIX);

        File unzipDir = tempDir.newFolder("unzip-nested-prefix");
        ZipUtils.unzip(zipFile, unzipDir.getAbsolutePath(), 2);

        File nestedUnzip = new File(unzipDir, SECOND_PREFIX);

        assertTrue("nested prefix folder in zip should have been trimmed", !nestedUnzip.exists());
    }

    @Test
    public void unzipAndFlatten() throws IOException
    {
        File zipFile = tempDir.newFile("zip-flatten.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, "");

        File unzipDir = tempDir.newFolder("unzip-flatten");
        ZipUtils.unzip(zipFile, unzipDir.getAbsolutePath(), 0, true, null);

        File level2TextFile = new File(unzipDir, "level2sub2.txt");
        File level3TextFile = new File(unzipDir, "level3sub1.txt");

        assertTrue(level2TextFile.exists());
        assertTrue(level3TextFile.exists());
    }

    @Test
    public void unzipPatternShouldMatch() throws IOException
    {
        File zipFile = tempDir.newFile("zip-pattern.zip");
        ZipUtils.zipDir(zipFile, sourceZipDir, "");

        File unzipDir = tempDir.newFolder("unzip-pattern");
        ZipUtils.unzip(zipFile, unzipDir.getAbsolutePath(), 0, false, null);

        File level2TextFile = new File(unzipDir, "test-zip-dir/level2sub2/level2sub2.txt");
        File level3TextFile = new File(unzipDir, "test-zip-dir/level2sub2/level3sub1/level3sub1.txt");

        assertTrue(level2TextFile.exists());
        assertTrue(level3TextFile.exists());

        unzipDir = tempDir.newFolder("unzip-pattern-2");
        ZipUtils.unzip(zipFile, unzipDir.getAbsolutePath(), 0, false, Pattern.compile(".+level2sub2.txt"));

        level2TextFile = new File(unzipDir, "test-zip-dir/level2sub2/level2sub2.txt");
        level3TextFile = new File(unzipDir, "test-zip-dir/level2sub2/level3sub1/level3sub1.txt");

        assertTrue(level2TextFile.exists());
        assertFalse(level3TextFile.exists());
    }

    @Test
    public void unzipExecutable() throws IOException
    {
        File zipFile = tempDir.newFile("zip-executable.zip");
        File executable = new File(sourceZipDir, "executable.sh");
        executable.createNewFile();

        // This won't work under Windows - not much we can do but ignore this test
        Assume.assumeTrue(executable.setExecutable(true));

        ZipUtils.zipDir(zipFile, sourceZipDir, "");

        File unzipDir = tempDir.newFolder("unzip-executable");
        ZipUtils.unzip(zipFile, unzipDir.getAbsolutePath(), 1);

        File nestedUnzip = new File(unzipDir, "executable.sh");

        assertTrue("Zip/Unzip should preserve executable permissions", nestedUnzip.canExecute());
    }

    @Test
    public void countNoNestingLevel()
    {
        List<String> filenames = Lists.newArrayList(
                "file1.txt",
                "file2.txt");

        int nestedRoots = ZipUtils.countNestingLevel(filenames);
        assertEquals("The number of nested roots should be detected", 0, nestedRoots);
    }

    @Test
    public void countOneNestingLevel()
    {
        List<String> filenames = Lists.newArrayList(
                "root/folder1/file.txt",
                "root/folder2/file.txt");

        int nestedRoots = ZipUtils.countNestingLevel(filenames);
        assertEquals("The number of nested roots should be detected", 1, nestedRoots);
    }

    @Test
    public void countNestingLevelWithEmptyList()
    {
        List<String> filenames = Lists.newArrayList();

        int nestedRoots = ZipUtils.countNestingLevel(filenames);
        assertEquals("Should work with an empty list", 0, nestedRoots);
    }

    @Test
    public void countTwoNestingLevel()
    {
        List<String> filenames = Lists.newArrayList(
                "root/otherRoot/file1.txt",
                "root/otherRoot/file2.txt");
        int nestedRoots = ZipUtils.countNestingLevel(filenames);
        assertEquals("The number of nested roots should be detected", 2, nestedRoots);
    }

    @Test
    public void countTwoNestingLevelWithEmptyDirs()
    {
        List<String> filenames = Lists.newArrayList(
                "root/",
                "root/otherRoot/",
                "root/otherRoot/file1.txt",
                "root/otherRoot/file2.txt");

        int nestedRoots = ZipUtils.countNestingLevel(filenames);
        assertEquals("The number of nested roots should be detected", 2, nestedRoots);
    }

    @Test
    public void countTwoNestingLevelWithEmptyDirsInReversedOrder()
    {
        List<String> filenames = Lists.newArrayList(
                "root/otherRoot/file1.txt",
                "root/otherRoot/file2.txt",
                "root/otherRoot/",
                "root/");

        int nestedRoots = ZipUtils.countNestingLevel(filenames);
        assertEquals("The number of nested roots should be detected", 2, nestedRoots);
    }

    @Test
    public void countOneNestingLevelWithEmptyDirs()
    {
        List<String> filenames = Lists.newArrayList(
                "root/folder1/file1.txt",
                "root/folder1/file2.txt",
                "root/folder1/",
                "root/folder2/",
                "root/");

        int nestedRoots = ZipUtils.countNestingLevel(filenames);
        assertEquals("The number of nested roots should be detected", 1, nestedRoots);
    }
}
