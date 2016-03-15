package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.MavenProjectLoader;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
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
    private static final String SEARCH_VERSION = "1.0.0";
    private static final String SEARCH_GROUP_ID = "com.atlassian.bitbucket.search";
    private static final String SEARCH_ARTIFACT_ID = "search-plugin";
    private static final String EMBEDDED_ARTIFACT_ID = "embedded-elasticsearch-plugin";
    private static final ProductArtifact embeddedBundle = new ProductArtifact(SEARCH_GROUP_ID, EMBEDDED_ARTIFACT_ID, SEARCH_VERSION);

    @Before
    public void setUp() throws Exception {
        this.bitbucketProductHandler = new BitbucketProductHandler(mavenContext, mavenGoals, artifactFactory);

        MavenProject mavenProject = mock(MavenProject.class);
        DependencyManagement dependencyManagement = mock(DependencyManagement.class);
        Dependency searchDependency = mock(Dependency.class);

        when(mock(MavenProjectLoader.class).loadMavenProject(any(Artifact.class), anyBoolean()))
                .thenReturn(Optional.of(mavenProject));
        when(mavenProject.getDependencyManagement()).thenReturn(dependencyManagement);
        when(dependencyManagement.getDependencies()).thenReturn(Collections.singletonList(searchDependency));

        when(searchDependency.getGroupId()).thenReturn(SEARCH_GROUP_ID);
        when(searchDependency.getArtifactId()).thenReturn(SEARCH_ARTIFACT_ID);
        when(searchDependency.getVersion()).thenReturn(SEARCH_VERSION);
    }

    private boolean pluginFoundInPluginList(List<ProductArtifact> pluginList) {
        return pluginList.stream().anyMatch(p -> p.equals(embeddedBundle));
    }

    @Test
    public void testGetAdditionalPluginsBeforeFirstSearchVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.4.0");
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertFalse(pluginFoundInPluginList(pluginList));
    }

    @Test
    public void testGetAdditionalPluginsAfterFirstSearchVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.6.0");
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertTrue(pluginFoundInPluginList(pluginList));
    }
}