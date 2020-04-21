package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CrowdProductHandlerTest
{
    @Mock
    private Product ctx;
    @Mock
    private MavenContext mavenContext;
    @Mock
    private MavenGoals mavenGoals;
    @Mock
    private MavenProject project;
    @Mock
    private ArtifactFactory artifactFactory;
    @Mock
    private Build build;
    private CrowdProductHandler crowdProductHandler;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File crowdHome;

    @Before
    public void setUp() throws Exception
    {
        when(mavenContext.getProject()).thenReturn(project);
        when(ctx.getInstanceId()).thenReturn("coolInstanceId");
        when(project.getBuild()).thenReturn(build);
        final File buildDirectory = temporaryFolder.newFolder();
        when(build.getDirectory()).thenReturn(buildDirectory.getAbsolutePath());
        when(ctx.getServer()).thenReturn("localhost");
        when(ctx.getHttpPort()).thenReturn(4990);
        when(ctx.getContextPath()).thenReturn("crowd");
        crowdHome = temporaryFolder.newFolder("crowdhome");
        when(ctx.getDataHome()).thenReturn(crowdHome.getCanonicalPath());
        crowdProductHandler = new CrowdProductHandler(mavenContext, mavenGoals, artifactFactory);
    }

    @Test
    public void crowdServerUriConvertedToUseLocalhost() throws URISyntaxException
    {
        String uri = "http://example.test:8080/prefix/crowd?query#fragment";
        assertEquals("http://localhost:8080/prefix/crowd?query#fragment", CrowdProductHandler.withLocalhostAsHostname(uri));
    }

    @Test
    public void crowdServerUriConvertedToUseLocalhostWithHttps() throws URISyntaxException
    {
        String uri = "https://example.test:8080/prefix/crowd?query#fragment";
        assertEquals("https://localhost:8080/prefix/crowd?query#fragment", CrowdProductHandler.withLocalhostAsHostname(uri));
    }

    @Test
    public void shouldReplaceDatabaseUriInSharedHomeConfig() throws Exception
    {
        final File sharedHome = initializeSharedHome();
        FileUtils.copyInputStreamToFile(CrowdProductHandlerTest.class.getResourceAsStream("crowd.cfg.xml"), new File(sharedHome, "crowd.cfg.xml"));

        crowdProductHandler.processHomeDirectory(ctx, crowdHome);

        assertThat(FileUtils.readFileToString(new File(sharedHome, "crowd.cfg.xml"), StandardCharsets.UTF_8),
                containsString(databasePathContains(crowdHome.getCanonicalPath())));
    }

    @Test
    public void shouldReplaceDatabaseUriInClassicHomeConfig() throws Exception
    {
        FileUtils.copyInputStreamToFile(CrowdProductHandlerTest.class.getResourceAsStream("crowd.cfg.xml"), new File(crowdHome, "crowd.cfg.xml"));

        crowdProductHandler.processHomeDirectory(ctx, crowdHome);

        assertThat(FileUtils.readFileToString(new File(crowdHome, "crowd.cfg.xml"), StandardCharsets.UTF_8),
                containsString(databasePathContains(crowdHome.getCanonicalPath())));
    }

    @Test
    public void shouldReplaceDatabaseUriInBothCrowdConfigs() throws Exception
    {
        final File sharedHome = initializeSharedHome();
        FileUtils.copyInputStreamToFile(CrowdProductHandlerTest.class.getResourceAsStream("crowd.cfg.xml"), new File(sharedHome, "crowd.cfg.xml"));
        FileUtils.copyInputStreamToFile(CrowdProductHandlerTest.class.getResourceAsStream("crowd.cfg.xml"), new File(crowdHome, "crowd.cfg.xml"));

        crowdProductHandler.processHomeDirectory(ctx, crowdHome);

        assertThat(FileUtils.readFileToString(new File(crowdHome, "crowd.cfg.xml"), StandardCharsets.UTF_8),
                containsString(databasePathContains(crowdHome.getCanonicalPath())));
        assertThat(FileUtils.readFileToString(new File(sharedHome, "crowd.cfg.xml"), StandardCharsets.UTF_8),
                containsString(databasePathContains(crowdHome.getCanonicalPath())));
    }

    private File initializeSharedHome()
    {
        final File sharedHome = new File(crowdHome, "shared");
        final boolean sharedHomeSuccessfullyCreated = sharedHome.mkdir();
        if (!sharedHomeSuccessfullyCreated)
        {
            throw new RuntimeException("Could not create shared home at " + sharedHome.getAbsolutePath());
        }
        return sharedHome;
    }

    private String databasePathContains(String path)
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            path = path.replace('\\', '/');
        }

        return "<property name=\"hibernate.connection.url\">" +
                "jdbc:hsqldb:" +
                path +
                "/database/defaultdb" +
                "</property";
    }

    @Test
    public void getExtraJarsToSkipWhenScanningForTldsAndWebFragments_shouldContainActivationJar() {
        // Invoke
        final Collection<String> extraJarsToSkip =
                crowdProductHandler.getExtraJarsToSkipWhenScanningForTldsAndWebFragments();

        // Check
        assertTrue(extraJarsToSkip.contains("mail-*.jar"));
    }
}
