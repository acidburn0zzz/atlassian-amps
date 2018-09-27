package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.MavenProjectLoader;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class BitbucketProductHandlerTest {

    @Mock
    private Product ctx;
    @Mock
    private MavenContext mavenContext;
    @Mock
    private MavenGoals mavenGoals;
    @Mock
    private ArtifactFactory artifactFactory;
    @Mock
    private MavenProjectLoader mavenProjectLoader;

    private BitbucketProductHandler bitbucketProductHandler;
    private static final String SEARCH_VERSION = "1.0.0";
    private static final String SEARCH_GROUP_ID = "com.atlassian.bitbucket.search";
    private static final String SEARCH_ARTIFACT_ID = "search-plugin";
    private static final String EMBEDDED_ARTIFACT_ID = "embedded-elasticsearch-plugin";
    private static final ProductArtifact embeddedBundle = new ProductArtifact(SEARCH_GROUP_ID, EMBEDDED_ARTIFACT_ID, SEARCH_VERSION);

    @Before
    public void setUp() throws Exception {
        bitbucketProductHandler = new BitbucketProductHandler(mavenContext, mavenGoals, artifactFactory, mavenProjectLoader);

        // Mocks for calling loadMavenProject
        MojoExecutor.ExecutionEnvironment executionEnvironment = mock(MojoExecutor.ExecutionEnvironment.class);
        MavenSession mavenSession = mock(MavenSession.class);

        when(mavenContext.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(executionEnvironment.getMavenSession()).thenReturn(mavenSession);

        // Mocks for the getting dependencies
        MavenProject mavenProject = mock(MavenProject.class);
        DependencyManagement dependencyManagement = mock(DependencyManagement.class);
        Dependency searchDependency = mock(Dependency.class);
        when(mavenContext.getProject()).thenReturn(mavenProject);

        when(mavenProjectLoader.loadMavenProject(any(MavenSession.class), any(MavenProject.class), any(Artifact.class)))
                .thenReturn(Optional.of(mavenProject));
        when(mavenProject.getDependencyManagement()).thenReturn(dependencyManagement);
        when(dependencyManagement.getDependencies()).thenReturn(Collections.singletonList(searchDependency));

        // Mocks for dependency attributes
        when(searchDependency.getGroupId()).thenReturn(SEARCH_GROUP_ID);
        when(searchDependency.getArtifactId()).thenReturn(SEARCH_ARTIFACT_ID);
        when(searchDependency.getVersion()).thenReturn(SEARCH_VERSION);

        Artifact parentArtifact = mock(Artifact.class);
        when(artifactFactory.createParentArtifact(anyString(), anyString(), anyString())).thenReturn(parentArtifact);
    }

    private boolean pluginFoundInPluginList(List<ProductArtifact> pluginList) {
        return pluginList.stream().anyMatch(p -> p.equals(embeddedBundle));
    }

    @Test
    public void testGetAdditionalPluginsBeforeFirstSearchVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.5.0");
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertFalse(pluginFoundInPluginList(pluginList));
    }

    @Test
    public void testGetAdditionalPluginsAtFirstSearchVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.6.0");
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertTrue(pluginFoundInPluginList(pluginList));
    }

    @Test
    public void testGetAdditionalPluginsAtSearchEAPVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.6.0-search-eap1");
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertTrue(pluginFoundInPluginList(pluginList));
    }

    @Test
    public void testGetAdditionalPluginsAtSearchMilestoneVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.6.0-m1");
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertTrue(pluginFoundInPluginList(pluginList));
    }

    @Test
    public void testGetAdditionalPluginsAtSearchReleaseCandidateVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.6.0-rc1");
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertTrue(pluginFoundInPluginList(pluginList));
    }

    @Test
    public void testGetAdditionalPluginsAtSearchAlphaVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.6.0-a0");
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertTrue(pluginFoundInPluginList(pluginList));
    }

    @Test
    public void testGetAdditionalPluginsAfterFirstSearchVersion() throws Exception {
        when(ctx.getVersion()).thenReturn("4.7.0");
        List<ProductArtifact> pluginList = bitbucketProductHandler.getAdditionalPlugins(ctx);
        assertTrue(pluginFoundInPluginList(pluginList));
    }
}