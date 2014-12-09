package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.Product;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.atlassian.maven.plugins.amps.product.RefappProductHandler.ATLASSIAN_BUNDLED_PLUGINS_DIR;
import static com.atlassian.maven.plugins.amps.product.RefappProductHandler.ATLASSIAN_BUNDLED_PLUGINS_ZIP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRefappProductHandler
{
    private File tempHome;

    @Before
    public void createTemporaryHomeDirectory() throws IOException
    {
        final File f = File.createTempFile("temp-refapp-home-" + UUID.randomUUID(), null);
        if (!f.delete())
        {
            throw new IOException();
        }

        if (!f.mkdir())
        {
            throw new IOException();
        }

        tempHome = f;
    }

    @After
    public void deleteTemporaryHomeDirectoryAndContents() throws Exception
    {
        if (tempHome != null)
        {
            FileUtils.deleteDirectory(tempHome);
            tempHome = null;
        }
    }

    @Test
    public void bundledPluginsLocationCorrectForDirectory()
    {
        final File bundledPluginsDir = new File(tempHome, ATLASSIAN_BUNDLED_PLUGINS_DIR);
        assertThat(bundledPluginsDir.mkdirs(), is(true));

        assertBundledPluginPath(tempHome, bundledPluginsDir);
    }

    @Test
    public void bundledPluginsLocationCorrectForFallback()
    {
        final File bundledPluginsDir = new File(tempHome, ATLASSIAN_BUNDLED_PLUGINS_ZIP);

        assertBundledPluginPath(tempHome, bundledPluginsDir);
    }

    private void assertBundledPluginPath(final File appDir, final File expectedPath)
    {
        // Set up
        final Log mockLog = mock(Log.class);
        final MavenContext mockMavenContext = mock(MavenContext.class);
        when(mockMavenContext.getLog()).thenReturn(mockLog);
        final RefappProductHandler productHandler = new RefappProductHandler(mockMavenContext, null, null);
        final Product mockProduct = mock(Product.class);

        // Invoke
        final File bundledPluginPath = productHandler.getBundledPluginPath(mockProduct, appDir);

        // Check
        assertThat(bundledPluginPath, notNullValue());
        assertThat(expectedPath, equalTo(bundledPluginPath));
    }
}
