package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BitbucketProductHandlerTest {

    @Mock
    private Product ctx;

    @Mock
    private MavenContext mavenContext;

    @Mock
    private MavenGoals mavenGoals;

    @Mock
    private ArtifactFactory artifactFactory;

    private BitbucketProductHandler bitbucketProductHandler;

    @Before
    public void setUp() {
        this.bitbucketProductHandler = new BitbucketProductHandler(mavenContext, mavenGoals, artifactFactory);
    }

    private boolean pluginFoundInPluginList(List<ProductArtifact> pluginList, ProductArtifact searchBundle) {
        return pluginList.stream().anyMatch(p -> p.equals(searchBundle));
    }

    @Test
    public void testGetAdditionalPluginsBeforeFirstSearchVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.4.0");
        ProductArtifact searchBundle = new ProductArtifact("com.atlassian.bitbucket.server", "bitbucket-distribution-search-bundle", ctx.getVersion());
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertFalse(pluginFoundInPluginList(pluginList, searchBundle));
    }

    @Test
    public void testGetAdditionalPluginsAfterFirstSearchVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.6.0");
        ProductArtifact searchBundle = new ProductArtifact("com.atlassian.bitbucket.server", "bitbucket-distribution-search-bundle", ctx.getVersion());
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertTrue(pluginFoundInPluginList(pluginList, searchBundle));
    }
}