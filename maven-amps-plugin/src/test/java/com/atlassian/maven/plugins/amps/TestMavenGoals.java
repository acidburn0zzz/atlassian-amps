package com.atlassian.maven.plugins.amps;

import com.google.common.collect.ImmutableMap;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import static com.atlassian.maven.plugins.amps.MavenGoals.AJP_PORT_PROPERTY;
import static com.atlassian.maven.plugins.amps.util.FileUtils.file;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestMavenGoals
{
    @Mock private MavenContext ctx;
    @Mock private MavenProject project;
    @Mock private Build build;
    private MavenGoals goals;
    
    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        when(project.getBuild()).thenReturn(build);
        when(ctx.getProject()).thenReturn(project);
        
        goals = new MavenGoals(ctx);
    }
    
    @Test
    public void testPickFreePort() throws IOException
    {
        ServerSocket socket = null;
        try
        {
            socket = new ServerSocket(16829);

            // Pick any
            int port = goals.pickFreePort(0);
            assertTrue(16829 != port);
            assertTrue(port > 0);

            // Pick taken
            port = goals.pickFreePort(16829);
            assertTrue(16829 != port);
            assertTrue(port > 0);

            // Pick free
            assertEquals(16828, goals.pickFreePort(16828));
        }
        finally
        {
            socket.close();
        }
    }
    
    @Test
    public void testGenerateMinimalManifest() throws Exception
    {
        File tempDir = File.createTempFile("TestMavenGoals", "dir");
        tempDir.delete();
        tempDir.mkdir();
        
        when(build.getOutputDirectory()).thenReturn(tempDir.getAbsolutePath());

        Map<String, String> attrs = ImmutableMap.of("Attribute-A", "aaa", "Attribute-B", "bbb");
        
        goals.generateMinimalManifest(attrs);
        
        File mf = file(tempDir.getAbsolutePath(), "META-INF", "MANIFEST.MF");
        assertTrue(mf.exists());
        
        Manifest m = new Manifest(new FileInputStream(mf));
        assertEquals("aaa", m.getMainAttributes().getValue("Attribute-A"));
        assertEquals("bbb", m.getMainAttributes().getValue("Attribute-B"));
        assertNull(m.getMainAttributes().getValue("Bundle-SymbolicName"));
    }

    @Test
    public void configurationPropertiesShouldIncludeAjpPortIfSet()
    {
        // Set up
        final int ajpPort = 8010;

        // Invoke
        final List<MojoExecutor.Element> configurationProperties = getConfigurationProperties(ajpPort);

        // Check
        for (final MojoExecutor.Element element : configurationProperties)
        {
            final Xpp3Dom elementDom = element.toDom();
            if (elementDom.getName().equals(AJP_PORT_PROPERTY))
            {
                assertEquals(String.valueOf(ajpPort), elementDom.getValue());
                return;
            }
        }
        fail("No element called " + AJP_PORT_PROPERTY);
    }

    @Test
    public void configurationPropertiesShouldNotIncludeAjpPortIfSetToZero()
    {
        // Invoke
        final List<MojoExecutor.Element> configurationProperties = getConfigurationProperties(0);

        // Check
        for (final MojoExecutor.Element element : configurationProperties)
        {
            final Xpp3Dom elementDom = element.toDom();
            if (elementDom.getName().equals(AJP_PORT_PROPERTY))
            {
                fail("Found an element called " + AJP_PORT_PROPERTY);
            }
        }
    }

    private List<MojoExecutor.Element> getConfigurationProperties(final int ajpPort)
    {
        // Set up
        final int httpPort = 8086;
        final int rmiPort = 666;
        final Map<String, String> systemProperties = Collections.emptyMap();
        final Product mockProduct = mock(Product.class);
        when(mockProduct.getAjpPort()).thenReturn(ajpPort);
        final String protocol = "http";

        // Invoke
        final List<MojoExecutor.Element> configurationProperties =
                goals.getConfigurationProperties(systemProperties, mockProduct, rmiPort, httpPort, protocol);

        // Check
        assertNotNull(configurationProperties);
        return configurationProperties;
    }
}
