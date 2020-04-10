package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.util.MojoExecutorWrapper;
import com.atlassian.maven.plugins.amps.util.MojoExecutorWrapperImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.archetype.common.DefaultPomManager;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
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
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnitRunner;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import static com.atlassian.maven.plugins.amps.MavenGoals.AJP_PORT_PROPERTY;
import static com.atlassian.maven.plugins.amps.util.FileUtils.file;
import static com.atlassian.maven.plugins.amps.util.ProductHandlerUtil.pickFreePort;
import static com.atlassian.maven.plugins.amps.util.Xpp3DomMatchers.childMatching;
import static com.atlassian.maven.plugins.amps.util.Xpp3DomMatchers.childrenMatching;
import static com.atlassian.maven.plugins.amps.util.Xpp3DomMatchers.valueMatching;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

@RunWith(MockitoJUnitRunner.class)
public class TestMavenGoals {
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
    @Mock
    private MojoExecutorWrapper mojoExecutorWrapper;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        when(ctx.getProject()).thenReturn(project);
        when(ctx.getLog()).thenReturn(new SystemStreamLog());
        when(ctx.getVersionOverrides()).thenReturn(new Properties());

        when(executionEnvironment.getMavenProject()).thenReturn(project);
        when(executionEnvironment.getMavenSession()).thenReturn(session);

        when(project.getBuild()).thenReturn(build);

        when(session.getCurrentProject()).thenReturn(project);

        goals = new MavenGoals(ctx, new MojoExecutorWrapperImpl());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void shouldCorrectlyGenerateMinimalManifest() throws Exception {
        when(build.getOutputDirectory()).thenReturn(temporaryFolder.getRoot().getAbsolutePath());

        final Map<String, String> attrs = ImmutableMap.of("Attribute-A", "aaa", "Attribute-B", "bbb");

        goals.generateMinimalManifest(attrs);

        final File mf = file(temporaryFolder.getRoot().getAbsolutePath(), "META-INF", "MANIFEST.MF");
        assertTrue(mf.exists());

        final Manifest m = new Manifest(new FileInputStream(mf));
        assertEquals("aaa", m.getMainAttributes().getValue("Attribute-A"));
        assertEquals("bbb", m.getMainAttributes().getValue("Attribute-B"));
        assertNull(m.getMainAttributes().getValue("Bundle-SymbolicName"));
    }

    @Test
    public void shouldReflectHttpPortInBaseUrl() {
        // Set up
        final int httpPort = 51935;

        // Invoke
        final String baseUrl = getBaseUrl(httpPort);

        // Check
        assertEquals("http://localhost:51935/confluence", baseUrl);
    }

    @Test
    public void shouldIncludeAjpPortInConfigurationIfSet() {
        // Set up
        final int ajpPort = 8010;

        // Invoke
        final List<MojoExecutor.Element> configurationProperties = getConfigurationProperties(ajpPort);

        // Check
        for (final MojoExecutor.Element element : configurationProperties) {
            final Xpp3Dom elementDom = element.toDom();
            if (elementDom.getName().equals(AJP_PORT_PROPERTY)) {
                assertEquals(String.valueOf(ajpPort), elementDom.getValue());
                return;
            }
        }
        fail("No element called " + AJP_PORT_PROPERTY);
    }

    @Test
    public void shouldNotOverwriteBndLib() {
        final Map<String, String> pluginArtifactIdToVersionMap = goals.defaultArtifactIdToVersionMap;
        assertThat("bndlib should not be overridden.", pluginArtifactIdToVersionMap.get("bndlib"), CoreMatchers.nullValue());
        assertThat("bndlib should not be overridden.", pluginArtifactIdToVersionMap.get("biz.aQute.bndlib"), CoreMatchers.nullValue());
    }

    @Test
    public void shouldCorrectlyProcessFilesWithCrlf() throws Exception {
        final DefaultPomManager pomManager = new DefaultPomManager();
        final URL originalPomPath = TestMavenGoals.class.getResource("originalPom.xml");

        File originalPomFile = new File(originalPomPath.toURI());
        File temp = new File(temporaryFolder.getRoot(), "tempOriginalPom.xml");

        FileUtils.copyFile(originalPomFile, temp);
        String originalPomXml = readFileToString(temp, UTF_8);

        assertThat("Not expected file!", originalPomXml, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat("File contains \r\n before process correct CRLF", originalPomXml, containsString("\r\n"));

        goals.processCorrectCrlf(pomManager, temp);

        String processedPomXml = readFileToString(temp, UTF_8);
        assertThat("Not expected file after process correct CRLF!", processedPomXml, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat("File contains \n after process correct CRLF", processedPomXml, containsString("\n"));
        assertThat("File not contains \r\n after process correct CRLF", processedPomXml, not(containsString("\r\n")));
        temp.deleteOnExit();
    }

    /**
     * Tests that the application fails to start when a HTTP port is taken.
     *
     * @throws Exception
     */
    @Test(expected = MojoExecutionException.class)
    public void shouldFailWhenHttpPortIsOccupiedOnWebappStartup() throws Exception {
        mockBuildPluginManager();
        final File war = mock(File.class);

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");
        when(product.isHttps()).thenReturn(false);

        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            when(product.getHttpPort()).thenReturn(serverSocket.getLocalPort());
            goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);
        }
    }

    /**
     * Tests that the application fails to start when a HTTPS port is taken.
     *
     * @throws Exception
     */
    @Test(expected = MojoExecutionException.class)
    public void shouldFailWhenHttpsPortIsOccupiedOnWebappStartup() throws Exception {
        mockBuildPluginManager();
        final File war = mock(File.class);

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");
        when(product.isHttps()).thenReturn(true);

        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            when(product.getHttpsPort()).thenReturn(serverSocket.getLocalPort());
            goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);
        }
    }

    /**
     * Tests that the application fails to start when a RMI port is taken.
     *
     * @throws Exception
     */
    @Test(expected = MojoExecutionException.class)
    public void shouldFailWhenRmiPortIsOccupiedOnWebappStartup() throws Exception {
        mockBuildPluginManager();
        final File war = mock(File.class);

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");

        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            when(product.getRmiPort()).thenReturn(serverSocket.getLocalPort());
            goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);
        }
    }

    /**
     * Tests that the application fails to start when a AJP port is taken.
     *
     * @throws Exception
     */
    @Test(expected = MojoExecutionException.class)
    public void shouldFailWhenAjpPortIsOccupiedOnWebappStartup() throws Exception {
        mockBuildPluginManager();
        final File war = mock(File.class);

        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        when(product.getContainerId()).thenReturn("tomcat8x");

        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            when(product.getAjpPort()).thenReturn(serverSocket.getLocalPort());
            goals.startWebapp("", war, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), product);
        }
    }

    /**
     * Tests that the application starts when the specified ports are available.
     *
     * @throws Exception
     */
    @Test
    public void shouldStartWebappWhenAllPortsAreAvailable() throws Exception {
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
    public void shouldUseHttpPortsConfiguredFromProductWhenStartingWebapp() throws Exception {
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
    public void shouldUseHttpsPortsConfiguredFromProductWhenStartingWebapp() throws Exception {
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

    @Test
    public void shouldGracefullyHandleRestDocGenerationFailure() throws Exception {
        goals = new MavenGoals(ctx, mojoExecutorWrapper);
        createStructureInTempDirectory();
        copyPluginXmlWithRestModules();
        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        final Path buildOutputDirectory = temporaryFolder.getRoot().toPath().resolve("target");
        when(build.getOutputDirectory()).thenReturn(buildOutputDirectory.toAbsolutePath().toString());
        final Path sourceDirectory = temporaryFolder.getRoot().toPath().resolve("src/main/java");
        when(build.getSourceDirectory()).thenReturn(sourceDirectory.toAbsolutePath().toString());
        doThrow(new MojoExecutionException(" ")).when(mojoExecutorWrapper).executeWithMergedConfig(any(), eq("javadoc"), any(), eq(executionEnvironment));

        goals.generateRestDocs("");

        assertGeneratedFileEqualsExpectedFile(buildOutputDirectory, "application-doc.xml", "expected-application-doc.xml");
        assertGeneratedFileEqualsExpectedFile(buildOutputDirectory, "application-grammars.xml", "expected-application-grammars.xml");
    }

    @Test
    public void shouldNotReplaceRestDocFilesIfTheyWereGenerated() throws Exception {
        goals = new MavenGoals(ctx, mojoExecutorWrapper);
        createStructureInTempDirectory();
        copyPluginXmlWithRestModules();
        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        final Path buildOutputDirectory = temporaryFolder.getRoot().toPath().resolve("target");
        when(build.getOutputDirectory()).thenReturn(buildOutputDirectory.toAbsolutePath().toString());
        final Path sourceDirectory = temporaryFolder.getRoot().toPath().resolve("src/main/java");
        when(build.getSourceDirectory()).thenReturn(sourceDirectory.toAbsolutePath().toString());
        doAnswer(a -> {
            FileUtils.writeStringToFile(buildOutputDirectory.resolve("application-doc.xml").toFile(), "appDoc", UTF_8);
            FileUtils.writeStringToFile(buildOutputDirectory.resolve("application-grammars.xml").toFile(), "appGrammars", UTF_8);
            return null;
        }).when(mojoExecutorWrapper).executeWithMergedConfig(any(), eq("javadoc"), any(), eq(executionEnvironment));

        goals.generateRestDocs("");

        assertGeneratedFileEqualsString(buildOutputDirectory, "application-doc.xml", "appDoc");
        assertGeneratedFileEqualsString(buildOutputDirectory, "application-grammars.xml", "appGrammars");
    }

    @Test
    public void shouldCorrectlyBuildPaths() throws Exception {
        goals = new MavenGoals(ctx, mojoExecutorWrapper);
        createStructureInTempDirectory();
        copyPluginXmlWithRestModules();
        when(ctx.getExecutionEnvironment()).thenReturn(executionEnvironment);
        final Path buildOutputDirectory = temporaryFolder.getRoot().toPath().resolve("target");
        when(build.getOutputDirectory()).thenReturn(buildOutputDirectory.toAbsolutePath().toString());
        final Path sourceDirectory = temporaryFolder.getRoot().toPath().resolve("src/main/java");
        when(build.getSourceDirectory()).thenReturn(sourceDirectory.toAbsolutePath().toString());
        final String jacksonModules = "java.lang.Object";
        final Set<String> expectedClasspathElements = prepareClasspathElements(buildOutputDirectory.toString());

        goals.generateRestDocs(jacksonModules);

        verify(mojoExecutorWrapper).executeWithMergedConfig(any(), eq("javadoc"),
                MockitoHamcrest.argThat(allOf(
                        childMatching("sourcepath", valueMatching(sourceDirectory.resolve("com/atlassian/labs").toString())),
                        childMatching("docletPath", valueMatching(new ClasspathElementsMatcher(expectedClasspathElements))),
                        childMatching("docletPath", valueMatching(startsWith(":"))),
                        childMatching("additionalOptions", childrenMatching(
                                contains(
                                        valueMatching(String.format("-output \"%s\"",
                                                buildOutputDirectory
                                                        .resolve("resourcedoc.xml")
                                                        .toAbsolutePath()
                                                        .toString())
                                        ),
                                        valueMatching(String.format(" -modules \"%s\"", jacksonModules))
                                )
                        ))
                )),
                any());
    }

    @Test
    public void shouldNotExecuteJavadocPluginIfNoRestModulesDefined() throws Exception {
        goals = new MavenGoals(ctx, mojoExecutorWrapper);
        createStructureInTempDirectory();
        copyPluginXmlWithoutRestModules();
        final Path buildOutputDirectory = temporaryFolder.getRoot().toPath().resolve("target");
        when(build.getOutputDirectory()).thenReturn(buildOutputDirectory.toAbsolutePath().toString());

        goals.generateRestDocs("");

        verify(mojoExecutorWrapper, never()).executeWithMergedConfig(any(), eq("javadoc"), any(), any());
    }

    private List<MojoExecutor.Element> getConfigurationProperties(final int ajpPort) {
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

    private String getBaseUrl(final int httpPort) {
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

    private Set<String> prepareClasspathElements(String buildOutputDirectory) throws DependencyResolutionRequiredException {
        final ImmutableList<String> compileClasspathElements = ImmutableList.of("classpath1", "classpath2");
        when(project.getCompileClasspathElements()).thenReturn(compileClasspathElements);
        final ImmutableList<String> runtimeClasspathElements = ImmutableList.of("runtime1", "runtime2");
        when(project.getRuntimeClasspathElements()).thenReturn(runtimeClasspathElements);
        final ImmutableList<String> systemClasspathElements = ImmutableList.of("system1", "system2");
        when(project.getSystemClasspathElements()).thenReturn(systemClasspathElements);
        return Stream.of(
                Stream.of(""),
                Stream.of(buildOutputDirectory),
                compileClasspathElements.stream(),
                systemClasspathElements.stream(),
                runtimeClasspathElements.stream(),
                goals.getPluginClasspathElements().stream()
        )
                .flatMap(Function.identity())
                .collect(toSet());
    }

    private void createStructureInTempDirectory() {
        new File(temporaryFolder.getRoot(), "src/main/java").mkdirs();
        new File(temporaryFolder.getRoot(), "target").mkdirs();
    }

    private void copyPluginXmlWithRestModules() throws Exception {
        Files.copy(Paths.get(TestMavenGoals.class.getResource("plugin-xml-with-rest-modules.xml").toURI()),
                temporaryFolder.getRoot().toPath().resolve("target/atlassian-plugin.xml"));
    }

    private void copyPluginXmlWithoutRestModules() throws Exception {
        Files.copy(Paths.get(TestMavenGoals.class.getResource("plugin-xml-without-rest-modules.xml").toURI()),
                temporaryFolder.getRoot().toPath().resolve("target/atlassian-plugin.xml"));
    }

    private void assertGeneratedFileEqualsExpectedFile(Path buildOutputDirectory, String fileName, String expectedContent) throws Exception {
        assertGeneratedFileEqualsString(buildOutputDirectory, fileName,
                readFileToString(new File(TestMavenGoals.class.getResource(expectedContent).toURI()), UTF_8));
    }

    private void assertGeneratedFileEqualsString(Path buildOutputDirectory, String fileName, String expectedContent) throws Exception {
        assertThat(readFileToString(buildOutputDirectory.resolve(fileName).toFile(), UTF_8),
                is(expectedContent));
    }

    private void mockBuildPluginManager() throws Exception {
        final MojoDescriptor mojoDescriptor = mock(MojoDescriptor.class);
        when(mojoDescriptor.getMojoConfiguration()).thenReturn(new DefaultPlexusConfiguration(""));

        final PluginDescriptor pluginDescriptor = mock(PluginDescriptor.class);
        when(pluginDescriptor.getMojo(anyString())).thenReturn(mojoDescriptor);

        final BuildPluginManager buildPluginManager = mock(BuildPluginManager.class);
        when(buildPluginManager.loadPlugin(any(Plugin.class), any(), any()))
                .thenReturn(pluginDescriptor);

        when(executionEnvironment.getPluginManager()).thenReturn(buildPluginManager);
    }

    /**
     * A matcher that validates if a given classpath is equal to the specified set of classpath entries
     */
    private static class ClasspathElementsMatcher extends TypeSafeMatcher<String> {

        private final Matcher<Set<String>> matcher;

        ClasspathElementsMatcher(Set<String> classpathElements) {
            this.matcher = is(classpathElements);
        }

        @Override
        protected boolean matchesSafely(String item) {
            final Set<String> classpathElements = Arrays.stream(item.split(File.pathSeparator))
                    .collect(toSet());
            return matcher.matches(classpathElements);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Matcher for doclet path with elements: ")
                    .appendDescriptionOf(matcher);
        }
    }
}
