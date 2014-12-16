package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.Product;
import org.apache.maven.plugin.logging.Log;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static com.atlassian.maven.plugins.amps.product.RefappProductHandler.ATLASSIAN_BUNDLED_PLUGINS_ZIP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRefappProductHandler
{
    @Rule
    public TemporaryFolder tempHome = new TemporaryFolder();

    @Test
    public void bundledPluginsLocationCorrectForDirectory()
    {
        final File bundledPluginsDir = tempHome.newFolder(ATLASSIAN_BUNDLED_PLUGINS_ZIP);
        assertBundledPluginPath(tempHome.getRoot(), bundledPluginsDir);
    }

    @Test
    public void bundledPluginsLocationCorrectForFallback()
    {
        final File bundledPluginsZip = new File(tempHome.getRoot(), ATLASSIAN_BUNDLED_PLUGINS_ZIP);
        assertBundledPluginPath(tempHome.getRoot(), bundledPluginsZip);
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
