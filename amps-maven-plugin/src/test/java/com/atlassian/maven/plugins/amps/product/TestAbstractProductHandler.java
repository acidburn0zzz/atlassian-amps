package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ArtifactRetriever;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestAbstractProductHandler
{
    private final static String ARTIFACT_ID = "atlassian-refapp";
    private final static String GROUP_ID = "com.atlassian.refapp";

    @Mock
    private ArtifactFactory mockArtifactFactory;
    @Mock
    private ArtifactRetriever mockArtifactRetriever;
    @Mock
    private Log mockLog;
    @Mock
    private MavenContext mockMavenContext;
    @Mock
    private MavenGoals mockMavenGals;
    @Mock
    private MavenProject mockProject;
    @Mock
    private PluginProvider mockPluginProvider;
    @Mock
    private Product mockProduct;

    private AbstractProductHandler handlerUnderTest;

    @Before
    public void setup() throws Exception
    {
        when(mockMavenContext.getLog()).thenReturn(mockLog);
        when(mockMavenContext.getProject()).thenReturn(mockProject);
        when(mockProduct.getArtifactRetriever()).thenReturn(mockArtifactRetriever);
        when(mockArtifactRetriever.getLatestStableVersion(any(Artifact.class))).thenReturn("3.0.3");

        handlerUnderTest = new AbstractProductHandler(mockMavenContext, mockMavenGals, mockPluginProvider, mockArtifactFactory)
        {
            @Override
            protected File extractApplication(final Product ctx, final File homeDir)
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            protected int startApplication(final Product ctx, final File app, final File homeDir, final Map<String, String> properties)
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            protected boolean supportsStaticPlugins()
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            protected Collection<? extends ProductArtifact> getDefaultBundledPlugins()
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            protected Collection<? extends ProductArtifact> getDefaultLibPlugins()
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            protected File getBundledPluginPath(final Product ctx, final File appDir)
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            protected File getUserInstalledPluginsDirectory(final Product product, final File webappDir, final File homeDir)
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            protected Map<String, String> getSystemProperties(final Product ctx)
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public ProductArtifact getArtifact()
            {
                return new ProductArtifact(GROUP_ID, ARTIFACT_ID);
            }

            @Override
            public ProductArtifact getTestResourcesArtifact()
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public String getId()
            {
                return "refapp";
            }

            @Override
            public void stop(final Product ctx)
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public int getDefaultHttpPort()
            {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public int getDefaultHttpsPort()
            {
                throw new UnsupportedOperationException("Not implemented");
            }
        };
    }

    @Test
    public void testNullVersions() throws Exception
    {
        assertVersion(null);
    }

    @Test
    public void testLatestVersions() throws Exception
    {
        assertVersion("LATEST");
    }


    @Test
    public void testReleaseVersions() throws Exception
    {
        assertVersion("RELEASE");
    }

    private void assertVersion(String version) throws MojoExecutionException
    {
        final Artifact artifact = createArtifact(GROUP_ID, ARTIFACT_ID, version);
        when(mockArtifactFactory.createProjectArtifact(eq(GROUP_ID), eq(ARTIFACT_ID), anyString())).thenReturn(artifact);
        String containerId =  handlerUnderTest.getDefaultContainerId(mockProduct);
        verify(mockArtifactRetriever).getLatestStableVersion(artifact);
        assertThat(containerId, is("tomcat8x"));
    }


    private Artifact createArtifact(String groupId, String artifactId, String version)
    {
        ArtifactHandler handler = new DefaultArtifactHandler();
        VersionRange versionRange;
        if (version == null)
        {
            versionRange = VersionRange.createFromVersion( Artifact.RELEASE_VERSION );
        }
        else
        {
            versionRange = VersionRange.createFromVersion( version );
        }
        return new DefaultArtifact( groupId, artifactId, versionRange, Artifact.SCOPE_RUNTIME, "pom", null, handler, false);
    }
 }
