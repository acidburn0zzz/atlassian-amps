package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.product.ProductHandler;
import com.atlassian.maven.plugins.amps.product.RefappProductHandler;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("Duplicates")
public class TestMavenGoalsHomeZip
{
    private static final String PRODUCT_ID = "noplacelike";
    private static final String INSTANCE_ID = "noplacelike1";
    private static final String TMP_RESOURCES = "tmp-resources";
    private static final String GENERATED_HOME = "generated-home";
    private static final String PLUGINS = "plugins";
    private static final String BUNDLED_PLUGINS = "bundled-plugins";
    private static final String ZIP_PREFIX = "generated-resources/" + PRODUCT_ID + "-home";
    private static final String SERVER = "server";

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    private ProductHandler productHandler;
    private File generatedHomeDir;
    private File pluginsDir;
    private File bundledPluginsDir;
    private Product product;

    @Before
    public void setup() throws IOException
    {
        generatedHomeDir = tempDir.newFolder(INSTANCE_ID, TMP_RESOURCES, GENERATED_HOME);
        pluginsDir = new File(generatedHomeDir, PLUGINS);
        bundledPluginsDir = new File(generatedHomeDir, BUNDLED_PLUGINS);

        // setup maven mocks
        Build build = mock(Build.class);
        when(build.getDirectory()).thenReturn(tempDir.getRoot().getAbsolutePath());

        MavenProject project = mock(MavenProject.class);
        when(project.getBuild()).thenReturn(build);

        MavenContext ctx = mock(MavenContext.class);
        when(ctx.getProject()).thenReturn(project);
        when(ctx.getLog()).thenReturn(new SystemStreamLog());
        when(ctx.getReactor()).thenReturn(Collections.emptyList());
        when(ctx.getSession()).thenReturn(null);

        // Mock the product
        product = mock(Product.class);
        when(product.getId()).thenReturn(PRODUCT_ID);
        when(product.getInstanceId()).thenReturn(INSTANCE_ID);
        when(product.getServer()).thenReturn(SERVER);

        // Mockito throws NoClassDefFoundError: org/apache/maven/project/ProjectBuilderConfiguration
        // when mocking the session
        // MavenSession session = mock(MavenSession.class);

        productHandler = new RefappProductHandler(ctx, null,null);
    }

    @Test
    public void skipNullHomeDir() throws Exception
    {
        File zip = new File(tempDir.getRoot(), "nullHomeZip.zip");

        productHandler.createHomeZip(null, zip, product);

        assertFalse("zip for null home should not exist", zip.exists());
    }

    @Test
    public void skipNonExistentHomeDir() throws Exception
    {
        File zip = new File(tempDir.getRoot(), "noExistHomeZip.zip");
        File fakeHomeDir = new File(tempDir.getRoot(), "this-folder-does-not-exist");

        productHandler.createHomeZip(fakeHomeDir, zip, product);

        assertFalse("zip for non-existent home should not exist", zip.exists());
    }

    @Test
    public void existingGeneratedDirGetsDeleted() throws IOException, MojoExecutionException
    {
        generatedHomeDir.mkdirs();

        File deletedFile = new File(generatedHomeDir, "should-be-deleted.txt");
        FileUtils.writeStringToFile(deletedFile, "This file should have been deleted!", StandardCharsets.UTF_8);

        File zip = tempDir.newFile("deleteGenHomeZip.zip");
        File homeDir = tempDir.newFolder("deleteGenHomeDir");

        productHandler.createHomeZip(homeDir, zip, product);

        assertFalse("generated text file should have been deleted", deletedFile.exists());
    }

    @Test
    public void pluginsNotIncluded() throws IOException, MojoExecutionException
    {
        pluginsDir.mkdirs();

        File pluginFile = new File(pluginsDir, "plugin.txt");
        FileUtils.writeStringToFile(pluginFile, "This file should have been deleted!", StandardCharsets.UTF_8);

        File zip = tempDir.newFile("deletePluginsHomeZip.zip");
        File homeDir = tempDir.newFolder("deletePluginsHomeDir");

        productHandler.createHomeZip(homeDir, zip, product);

        assertFalse("plugins file should have been deleted", pluginFile.exists());
    }

    @Test
    public void bundledPluginsNotIncluded() throws IOException, MojoExecutionException
    {
        bundledPluginsDir.mkdirs();

        File pluginFile = new File(bundledPluginsDir, "bundled-plugin.txt");
        FileUtils.writeStringToFile(pluginFile, "This file should have been deleted!", StandardCharsets.UTF_8);

        File zip = tempDir.newFile("deleteBundledPluginsHomeZip.zip");
        File homeDir = tempDir.newFolder("deleteBundledPluginsHomeDir");

        productHandler.createHomeZip(homeDir, zip, product);

        assertFalse("bundled-plugins file should have been deleted", pluginFile.exists());
    }

    @Test
    public void zipContainsProperPrefix() throws IOException, MojoExecutionException
    {
        File zipFile = tempDir.newFile("prefixedHomeZip.zip");
        File homeDir = tempDir.newFolder("prefixedHomeDir");
        tempDir.newFolder("prefixedHomeDir", "data");

        productHandler.createHomeZip(homeDir, zipFile, product);

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
                    assertEquals(ZIP_PREFIX, testPrefix);
                }
            }
        }
    }

    @Test
    public void zipContainsTestFile() throws IOException, MojoExecutionException
    {
        File zipFile = tempDir.newFile("fileHomeZip.zip");
        File homeDir = tempDir.newFolder("fileHomeDir");
        File dataDir = tempDir.newFolder("fileHomeDir", "data");

        File dataFile = new File(dataDir, "data.txt");
        FileUtils.writeStringToFile(dataFile, "This is some data.", StandardCharsets.UTF_8);

        productHandler.createHomeZip(homeDir, zipFile, product);

        boolean dataFileFound = false;
        try (ZipFile zip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements())
            {
                final ZipEntry zipEntry = entries.nextElement();
                String zipPath = zipEntry.getName();
                String fileName = zipPath.substring(zipPath.lastIndexOf("/") + 1);
                if (fileName.equals(dataFile.getName()))
                {
                    dataFileFound = true;
                    break;
                }
            }
        }

        assertTrue("data file not found in zip.", dataFileFound);
    }
}
