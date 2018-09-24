package com.atlassian.maven.plugins.amps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.archetype.common.DefaultPomManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Manifest;

import static com.atlassian.maven.plugins.amps.MavenGoals.AJP_PORT_PROPERTY;
import static com.atlassian.maven.plugins.amps.util.FileUtils.file;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironmentM3;
import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

public class TestMavenGoals
{
    @Mock private MavenContext ctx;
    @Mock private MavenProject project;
    @Mock private MavenSession session;
    @Mock private Build build;
    @Mock private Product product;
    private MavenGoals goals;
    @Mock private MojoExecutor.ExecutionEnvironment executionEnvironment;
    @Mock private MavenProject mavenProject;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        when(project.getBuild()).thenReturn(build);
        when(ctx.getProject()).thenReturn(project);
        when(ctx.getSession()).thenReturn(session);
        when(ctx.getLog()).thenReturn(new SystemStreamLog());
        when(ctx.getVersionOverrides()).thenReturn(new Properties());

        goals = new MavenGoals(ctx);
    }

    @Test
    public void testCargoExecution() throws Exception
    {
        // Setup
        final Plugin globalCargo = plugin(groupId("org.codehaus.cargo"), artifactId("cargo-maven2-plugin"), version("1.2.4"));
        final Plugin internalCargo = goals.cargo(null);
        // Maven build object for storing global cargo plugin
        final Build build = new Build();
        final Xpp3Dom internalConfig = configuration(
                element(name("container"),
                        element(name("containerId"), "tomcat8x"),
                        element(name("timeout"), String.valueOf(120000))
                )
        );
        final Xpp3Dom globalConfig = configuration(
                element(name("deployables"),
                        element(name("deployable"),
                                element(name("groupId"), "foo"),
                                element(name("artifactId"), "bar"),
                                element(name("type"), "war")
                        )
                )
        );
        // Mock objects
        final ExecutionEnvironmentM3 executionEnvironment = mock(ExecutionEnvironmentM3.class);
        final BuildPluginManager buildPluginManager = mock(BuildPluginManager.class);
        final PluginDescriptor pluginDescriptor = mock(PluginDescriptor.class);
        final MojoDescriptor mojoDescriptor = mock(MojoDescriptor.class);
        // Setup Cargo mojo descriptor parameters for merging child node configuration
        final List<Parameter> params = ImmutableList.of(
                createParamByName("container"),
                createParamByName("deployables")
        );

        globalCargo.setConfiguration(globalConfig);
        build.addPlugin(globalCargo);
        // Mock methods
        when(project.getBuild()).thenReturn(build);
        when(project.getBuildPlugins()).thenReturn(build.getPlugins());
        when(project.getPlugin("org.codehaus.cargo:cargo-maven2-plugin")).thenReturn(globalCargo);
        when(executionEnvironment.getMavenProject()).thenReturn(project);
        when(executionEnvironment.getMavenSession()).thenReturn(session);
        when(executionEnvironment.getBuildPluginManager()).thenReturn(buildPluginManager);
        when(buildPluginManager.loadPlugin(any(Plugin.class), anyListOf(RemoteRepository.class), any(RepositorySystemSession.class)))
                .thenReturn(pluginDescriptor);
        when(pluginDescriptor.getMojo(anyString())).thenReturn(mojoDescriptor);
        when(mojoDescriptor.getMojoConfiguration()).thenReturn(new DefaultPlexusConfiguration(""));
        when(mojoDescriptor.getParameters()).thenReturn(params);

        Mockito.doCallRealMethod().when(executionEnvironment).executeMojo(any(Plugin.class), any(String.class), any(Xpp3Dom.class));
        Mockito.doCallRealMethod().when(project).getGoalConfiguration(anyString(), anyString(), anyString(), anyString());
        Mockito.doAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            if (null != args[1])
            {
                final Xpp3Dom finalConfig = ((MojoExecution) args[1]).getConfiguration();
                if (null != finalConfig && finalConfig != internalConfig)
                {
                    throw new RuntimeException("Global Cargo is still there");
                }
            }
            return "ok";
        }).when(buildPluginManager).executeMojo(any(MavenSession.class), any(MojoExecution.class));
        // Invoke
        goals.executeMojoExcludeProductCargoConfig(internalCargo, "start", internalConfig, executionEnvironment);
        // Check
        assertThat(build.getPlugins(), hasItem(globalCargo));
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
    public void bndlibNotOverridden()
    {
        final Map<String, String> pluginArtifactIdToVersionMap = goals.pluginArtifactIdToVersionMap;
        assertThat("Bndlib should not be overridden.", pluginArtifactIdToVersionMap.get("bndlib"), CoreMatchers.nullValue());
        assertThat("Bndlib should not be overridden.", pluginArtifactIdToVersionMap.get("biz.aQute.bndlib"), CoreMatchers.nullValue());
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
                goals.getConfigurationProperties(systemProperties, mockProduct, rmiPort, httpPort, ajpPort, protocol);

        // Check
        assertNotNull(configurationProperties);
        return configurationProperties;
    }

    private Parameter createParamByName(final String name)
    {
        final Parameter p = new Parameter();
        p.setName(name);
        return p;
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
        String originalPomXml = FileUtils.readFileToString(temp);

        assertThat("Not expected file!", originalPomXml, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat("File contains \r\n before process correct CRLF", originalPomXml, containsString("\r\n"));

        goals.processCorrectCrlf(pomManager, temp);

        String processedPomXml = FileUtils.readFileToString(temp);
        assertThat("Not expected file after process correct CRLF!", processedPomXml, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat("File contains \n after process correct CRLF", processedPomXml, containsString("\n"));
        assertThat("File not contains \r\n after process correct CRLF", processedPomXml, not(containsString("\r\n")));
        temp.deleteOnExit();
    }

    @Test
    public void shouldUsePortsConfiguredFromProductWhenStartWebapp() throws MojoExecutionException
    {
        final String productInstanceId = "";
        final File war = Mockito.mock(File.class);
        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(executionEnvironment.getMavenProject()).thenReturn(mavenProject);
        when(mavenProject.getBuild()).thenReturn(build);
        when(product.getContainerId()).thenReturn("tomcat8x");
        when(product.getServer()).thenReturn("server");
        when(product.getContextPath()).thenReturn("/context");
        when(war.getPath()).thenReturn("/");

        goals.startWebapp(productInstanceId, war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);

        verify(product).getRmiPort();
        verify(product).getAjpPort();
        verify(product, atLeastOnce()).getHttpPort();
    }
}
