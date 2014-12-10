package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.Product;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

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
    public void bundledPluginsLocationCorrectForFallback() throws MojoExecutionException
    {
        // Client does not set either bundledPluginsDir or bundledPluginsFile
        final File bundledPluginsDir = new File(tempHome.getRoot(), ATLASSIAN_BUNDLED_PLUGINS_ZIP);
        assertBundledPluginPath(tempHome.getRoot(), bundledPluginsDir, null, null);
    }

    @Test
    public void bundledPluginsLocationCorrectForDirVariable() throws MojoExecutionException
    {
        // Client set a valid bundledPluginsDir
        final String bundledPluginsDirVariable = "/ClientSpecifiedDir";
        final File bundledPluginsDir = tempHome.newFolder(bundledPluginsDirVariable);

        assertBundledPluginPath(tempHome.getRoot(), bundledPluginsDir, bundledPluginsDirVariable, null);
    }

    @Test
    public void bundledPluginsLocationCorrectForFileVariable() throws MojoExecutionException, IOException
    {
        // Client set a valid bundledPluginsFile
        final String bundledPluginsFileVariable = "/ClientSpecifiedFile.zip";
        final File bundledPluginsFile = tempHome.newFile(bundledPluginsFileVariable);

        assertBundledPluginPath(tempHome.getRoot(), bundledPluginsFile, null, bundledPluginsFileVariable);
    }

    @Test(expected = MojoExecutionException.class)
    public void bundledPluginsLocationErrorForNonExistingDirVariable() throws MojoExecutionException
    {
        // Client set some dir that does not exist.
        final String bundledPluginsDirVariable = "/ClientSpecifiedDir";
        assertBundledPluginPath(tempHome.getRoot(), null, bundledPluginsDirVariable, null);
    }

    @Test(expected = MojoExecutionException.class)
    public void bundledPluginsLocationErrorForNonExistingFileVariable() throws MojoExecutionException
    {
        // Client set some file that does not exist.
        final String bundledPluginsFileVariable = "/ClientSpecifiedFile.zip";
        assertBundledPluginPath(tempHome.getRoot(), null, null, bundledPluginsFileVariable);
    }

    @Test(expected = MojoExecutionException.class)
    public void bundledPluginsLocationErrorForBothVarsSet() throws MojoExecutionException, IOException
    {
        // Client set a valid bundledPluginsDir and a valid file
        final String bundledPluginsDirVariable = "/ClientSpecifiedDir";
        final File bundledPluginsDir = tempHome.newFolder(bundledPluginsDirVariable);
        final String bundledPluginsFileVariable = "/ClientSpecifiedFile.zip";
        final File bundledPluginsFile = tempHome.newFile(bundledPluginsFileVariable);

        assertBundledPluginPath(tempHome.getRoot(), null, bundledPluginsDirVariable, bundledPluginsFileVariable);
    }

    private void assertBundledPluginPath(final File appDir, final File expectedPath, final String bundledPluginsDirVar, final String bundledPluginsFileVar)
            throws MojoExecutionException
    {
        // Set up
        final Log mockLog = mock(Log.class);
        final MavenContext mockMavenContext = mock(MavenContext.class);
        when(mockMavenContext.getLog()).thenReturn(mockLog);
        final RefappProductHandler productHandler = new RefappProductHandler(mockMavenContext, null, null);
        final Product mockProduct = mock(Product.class);
        when(mockProduct.getBundledPluginsDir()).thenReturn(bundledPluginsDirVar);
        when(mockProduct.getBundledPluginsFile()).thenReturn(bundledPluginsFileVar);

        // Invoke
        final File bundledPluginPath = productHandler.getBundledPluginPath(mockProduct, appDir);

        // Check
        assertThat(bundledPluginPath, notNullValue());
        assertThat(expectedPath, equalTo(bundledPluginPath));
    }
}
