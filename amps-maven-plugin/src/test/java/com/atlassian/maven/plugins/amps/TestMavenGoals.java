package com.atlassian.maven.plugins.amps;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.archetype.common.DefaultPomManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.RepositorySystemSession;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Manifest;

import static com.atlassian.maven.plugins.amps.MavenGoals.AJP_PORT_PROPERTY;
import static com.atlassian.maven.plugins.amps.util.FileUtils.file;
import static com.atlassian.maven.plugins.amps.util.ProductHandlerUtil.pickFreePort;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

@RunWith(MockitoJUnitRunner.class)
public class TestMavenGoals
{
    @Mock
    private Build build;
    @Mock
    private MavenContext ctx;
    @Mock
    private ExecutionEnvironment executionEnvironment;
    private MavenGoals goals;
    @Mock
    private Product product;
    @Mock
    private MavenProject project;
    @Mock
    private MavenSession session;

    @Before
    public void setUp()
    {
        when(ctx.getProject()).thenReturn(project);
        when(ctx.getLog()).thenReturn(new SystemStreamLog());
        when(ctx.getVersionOverrides()).thenReturn(new Properties());

        when(executionEnvironment.getMavenProject()).thenReturn(project);
        when(executionEnvironment.getMavenSession()).thenReturn(session);

        when(project.getBuild()).thenReturn(build);

        when(session.getCurrentProject()).thenReturn(project);

        goals = new MavenGoals(ctx);
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testGenerateMinimalManifest() throws Exception
    {
        final File tempDir = File.createTempFile("TestMavenGoals", "dir");
        tempDir.delete();
        tempDir.mkdir();

        when(build.getOutputDirectory()).thenReturn(tempDir.getAbsolutePath());

        final Map<String, String> attrs = ImmutableMap.of("Attribute-A", "aaa", "Attribute-B", "bbb");

        goals.generateMinimalManifest(attrs);

        final File mf = file(tempDir.getAbsolutePath(), "META-INF", "MANIFEST.MF");
        assertTrue(mf.exists());

        final Manifest m = new Manifest(new FileInputStream(mf));
        assertEquals("aaa", m.getMainAttributes().getValue("Attribute-A"));
        assertEquals("bbb", m.getMainAttributes().getValue("Attribute-B"));
        assertNull(m.getMainAttributes().getValue("Bundle-SymbolicName"));
    }

    @Test
    public void testHttpPortShouldBeReflectedInBaseUrl()
    {
        // Set up
        final int httpPort = 51935;

        // Invoke
        final String baseUrl = getBaseUrl(httpPort);

        // Check
        assertEquals("http://localhost:51935/confluence", baseUrl);
    }

    @Test
    public void testConfigurationPropertiesShouldIncludeAjpPortIfSet()
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
    public void testBndlibNotOverridden()
    {
        final Map<String, String> pluginArtifactIdToVersionMap = goals.defaultArtifactIdToVersionMap;
        assertThat("bndlib should not be overridden.", pluginArtifactIdToVersionMap.get("bndlib"), CoreMatchers.nullValue());
        assertThat("bndlib should not be overridden.", pluginArtifactIdToVersionMap.get("biz.aQute.bndlib"), CoreMatchers.nullValue());
    }

    private List<MojoExecutor.Element> getConfigurationProperties(final int ajpPort)
    {
        // Set up
        final int httpPort = 8086;
        final int rmiPort = 666;
        final Map<String, String> systemProperties = Collections.emptyMap();
        final Product mockProduct = mock(Product.class);
        final String protocol = "http";

        // Invoke
        final List<MojoExecutor.Element> configurationProperties =
                goals.getConfigurationProperties(systemProperties, mockProduct, rmiPort, httpPort, ajpPort, protocol);

        // Check
        assertNotNull(configurationProperties);
        return configurationProperties;
    }

    private String getBaseUrl(final int httpPort)
    {
        // Set up
        final Product mockProduct = mock(Product.class);
        when(mockProduct.getServer()).thenReturn("http://localhost");
        when(mockProduct.getContextPath()).thenReturn("/confluence");

        // Invoke
        final String baseUrl = MavenGoals.getBaseUrl(mockProduct, httpPort);

        // Check
        assertNotNull(baseUrl);
        verify(mockProduct, never()).getHttpPort();
        verify(mockProduct, never()).getHttpsPort();
        return baseUrl;
    }

    @Test
    public void testProcessCorrectCrlf() throws Exception
    {
        final DefaultPomManager pomManager = new DefaultPomManager();
        final URL originalPomPath = TestMavenGoals.class.getResource("originalPom.xml");
        final File sysTempDir = new File(System.getProperty("java.io.tmpdir"));

        File originalPomFile = new File(originalPomPath.toURI());
        File temp = new File(sysTempDir, "tempOriginalPom.xml");

        FileUtils.copyFile(originalPomFile, temp);
        String originalPomXml = FileUtils.readFileToString(temp, StandardCharsets.UTF_8);

        assertThat("Not expected file!", originalPomXml, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat("File contains \r\n before process correct CRLF", originalPomXml, containsString("\r\n"));

        goals.processCorrectCrlf(pomManager, temp);

        String processedPomXml = FileUtils.readFileToString(temp, StandardCharsets.UTF_8);
        assertThat("Not expected file after process correct CRLF!", processedPomXml, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat("File contains \n after process correct CRLF", processedPomXml, containsString("\n"));
        assertThat("File not contains \r\n after process correct CRLF", processedPomXml, not(containsString("\r\n")));
        temp.deleteOnExit();
    }

    /**
     * Tests that the application fails to start when a HTTP port is taken.
     * @throws Exception
     */
    @Test(expected = MojoExecutionException.class)
    public void testShouldFailWhenHttpPortTakenWhenStartWebapp() throws Exception
    {
        mockBuildPluginManager();
        final File war = mock(File.class);

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");
        when(product.isHttps()).thenReturn(false);

        try (final ServerSocket serverSocket = new ServerSocket(0))
        {
            when(product.getHttpPort()).thenReturn(serverSocket.getLocalPort());
            goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);
        }
    }

    /**
     * Tests that the application fails to start when a HTTPS port is taken.
     * @throws Exception
     */
    @Test(expected = MojoExecutionException.class)
    public void testShouldFailWhenHttpsPortTakenWhenStartWebapp() throws Exception
    {
        mockBuildPluginManager();
        final File war = mock(File.class);

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");
        when(product.isHttps()).thenReturn(true);

        try (final ServerSocket serverSocket = new ServerSocket(0))
        {
            when(product.getHttpsPort()).thenReturn(serverSocket.getLocalPort());
            goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);
        }
    }

    /**
     * Tests that the application fails to start when a RMI port is taken.
     * @throws Exception
     */
    @Test(expected = MojoExecutionException.class)
    public void testShouldFailWhenRmiPortTakenWhenStartWebapp() throws Exception
    {
        mockBuildPluginManager();
        final File war = mock(File.class);

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");

        try (final ServerSocket serverSocket = new ServerSocket(0))
        {
            when(product.getRmiPort()).thenReturn(serverSocket.getLocalPort());
            goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);
        }
    }

    /**
     * Tests that the application fails to start when a AJP port is taken.
     * @throws Exception
     */
    @Test(expected = MojoExecutionException.class)
    public void testShouldFailWhenAjpPortTakenWhenStartWebapp() throws Exception
    {
        mockBuildPluginManager();
        final File war = mock(File.class);

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");

        try (final ServerSocket serverSocket = new ServerSocket(0))
        {
            when(product.getAjpPort()).thenReturn(serverSocket.getLocalPort());
            goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);
        }
    }

    /**
     * Tests that the application starts when the specified ports are available.
     * @throws Exception
     */
    @Test
    public void testShouldStartWhenHttpPortNotTakenWhenStartWebapp() throws Exception
    {
        mockBuildPluginManager();
        final File war = mock(File.class);
        when(war.getPath()).thenReturn("/");

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");
        when(product.getServer()).thenReturn("server");
        when(product.getContextPath()).thenReturn("/context");

        final int freeHttpPort = pickFreePort(0);
        when(product.getHttpPort()).thenReturn(freeHttpPort);

        int httpPort = goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);
        assertEquals(httpPort, freeHttpPort);
    }

    @Test
    public void testShouldUseHttpPortsConfiguredFromProductWhenStartWebapp() throws Exception
    {
        mockBuildPluginManager();

        final File war = mock(File.class);
        when(war.getPath()).thenReturn("/");

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");
        when(product.getServer()).thenReturn("server");
        when(product.getContextPath()).thenReturn("/context");

        goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);

        verify(product).getRmiPort();
        verify(product).getAjpPort();
        verify(product, atLeastOnce()).getHttpPort();
    }

    @Test
    public void testShouldUseHttpsPortsConfiguredFromProductWhenStartWebapp() throws Exception
    {
        mockBuildPluginManager();

        final File war = mock(File.class);
        when(war.getPath()).thenReturn("/");

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");
        when(product.getServer()).thenReturn("server");
        when(product.getContextPath()).thenReturn("/context");
        when(product.isHttps()).thenReturn(true);

        goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);

        verify(product).getRmiPort();
        verify(product).getAjpPort();
        verify(product, atLeastOnce()).getHttpsPort();
    }

    private void mockBuildPluginManager() throws Exception
    {
        final MojoDescriptor mojoDescriptor = mock(MojoDescriptor.class);
        when(mojoDescriptor.getMojoConfiguration()).thenReturn(new DefaultPlexusConfiguration(""));

        final PluginDescriptor pluginDescriptor = mock(PluginDescriptor.class);
        when(pluginDescriptor.getMojo(anyString())).thenReturn(mojoDescriptor);

        final BuildPluginManager buildPluginManager = mock(BuildPluginManager.class);
        when(buildPluginManager.loadPlugin(any(Plugin.class), any(), any()))
                .thenReturn(pluginDescriptor);

        when(executionEnvironment.getPluginManager()).thenReturn(buildPluginManager);
    }
}
