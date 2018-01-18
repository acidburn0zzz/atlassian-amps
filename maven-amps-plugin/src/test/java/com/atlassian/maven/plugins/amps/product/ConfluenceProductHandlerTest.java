package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.XmlOverride;
import com.atlassian.maven.plugins.amps.util.ArtifactRetriever;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Collection;

import static com.atlassian.maven.plugins.amps.product.ConfluenceProductHandler.SYNCHRONY_PROXY_VERSION;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfluenceProductHandlerTest {
    private static final String LATEST_SYNCHRONY_PROXY_VERSION = "2.0.1";

    private ConfluenceProductHandler confluenceProductHandler;
    @Mock
    private MavenContext context;
    @Mock
    private MavenGoals goals;
    @Mock
    private ArtifactFactory artifactFactory;
    @Mock
    private org.apache.maven.plugin.logging.Log log;
    @Mock
    private Product ctx;
    @Mock
    private ArtifactRetriever artifactRetriever;
    @Captor
    ArgumentCaptor<Collection<XmlOverride>> expectedOverridesCaptor;

    private String oldSynchronyProxyEnv;

    @Before
    public void setUp() throws Exception {
        oldSynchronyProxyEnv = System.getProperty(SYNCHRONY_PROXY_VERSION);
        when(context.getLog()).thenReturn(log);
        confluenceProductHandler = new ConfluenceProductHandler(context, goals, artifactFactory);
    }

    @After
    public void tearDown() throws Exception {
        if(oldSynchronyProxyEnv != null) {
            System.setProperty(SYNCHRONY_PROXY_VERSION, oldSynchronyProxyEnv);
        } else {
            System.clearProperty(SYNCHRONY_PROXY_VERSION);
        }
    }

    @Test
    public void testCustomiseInstanceBefore6() throws Exception {
        when(ctx.getVersion()).thenReturn("5.10.8");
        confluenceProductHandler.customiseInstance(ctx, new File("./"), new File("./"));
        verify(goals, never()).copyWebappWar(anyString(), anyObject(), anyObject());
    }

    @Test
    public void testCustomiseInstanceBefore6_5() throws Exception {
        ConfluenceProductHandler spied = spy(confluenceProductHandler);
        when(ctx.getVersion()).thenReturn("6.4.0-SNAPSHOT");
        when(goals.copyWebappWar(anyString(), anyObject(), anyObject())).thenReturn(new File("./"));
        doReturn(new File("./")).when(spied).getBaseDirectory(ctx);

        spied.customiseInstance(ctx, new File("./"), new File("./"));

        ArgumentCaptor<ProductArtifact> synchronyProxyArtifactCaptor = ArgumentCaptor.forClass(ProductArtifact.class);
        verify(goals, times(1)).copyWebappWar(anyString(), any(File.class), synchronyProxyArtifactCaptor.capture());
        assertThat(synchronyProxyArtifactCaptor.getValue().getVersion(), is("1.0.17"));
    }

    @Test
    public void testCustomiseInstanceOverridden() throws Exception {
        System.setProperty(SYNCHRONY_PROXY_VERSION, "1.0.16");
        when(ctx.getVersion()).thenReturn("6.4.0-SNAPSHOT");
        when(goals.copyWebappWar(anyString(), anyObject(), anyObject())).thenReturn(new File("./"));
        ConfluenceProductHandler spied = spy(confluenceProductHandler);
        doReturn(new File("./")).when(spied).getBaseDirectory(ctx);

        spied.customiseInstance(ctx, new File("./"), new File("./"));

        ArgumentCaptor<ProductArtifact> synchronyProxyArtifactCaptor = ArgumentCaptor.forClass(ProductArtifact.class);
        verify(goals, times(1)).copyWebappWar(anyString(), any(File.class), synchronyProxyArtifactCaptor.capture());
        assertThat(synchronyProxyArtifactCaptor.getValue().getVersion(), is("1.0.16"));
    }

    @Test
    public void testCustomiseInstanceAfter6_5() throws Exception {
        when(ctx.getVersion()).thenReturn("6.5.0-SNAPSHOT");
        when(ctx.getArtifactRetriever()).thenReturn(artifactRetriever);
        when(artifactRetriever.getLatestStableVersion(anyObject())).thenReturn(LATEST_SYNCHRONY_PROXY_VERSION);
        when(goals.copyWebappWar(anyString(), anyObject(), anyObject())).thenReturn(new File("./"));
        ConfluenceProductHandler spied = spy(confluenceProductHandler);
        doReturn(new File("./")).when(spied).getBaseDirectory(ctx);

        spied.customiseInstance(ctx, new File("./"), new File("./"));

        ArgumentCaptor<ProductArtifact> synchronyProxyArtifactCaptor = ArgumentCaptor.forClass(ProductArtifact.class);
        verify(goals, times(1)).copyWebappWar(anyString(), any(File.class), synchronyProxyArtifactCaptor.capture());
        assertThat(synchronyProxyArtifactCaptor.getValue().getVersion(), is(LATEST_SYNCHRONY_PROXY_VERSION));
    }

    @Test
    public void testCustomiseInstanceForServerXmlOverrides() throws Exception {
        // Setup
        when(ctx.getVersion()).thenReturn("6.5.0-SNAPSHOT");
        when(ctx.getArtifactRetriever()).thenReturn(artifactRetriever);
        when(artifactRetriever.getLatestStableVersion(anyObject())).thenReturn(LATEST_SYNCHRONY_PROXY_VERSION);
        when(goals.copyWebappWar(anyString(), anyObject(), anyObject())).thenReturn(new File("./"));
        ConfluenceProductHandler spied = spy(confluenceProductHandler);
        doReturn(new File("./")).when(spied).getBaseDirectory(ctx);

        // Execute
        spied.customiseInstance(ctx, new File("./"), new File("./"));

        // Verify
        verify(ctx, times(1)).setCargoXmlOverrides(expectedOverridesCaptor.capture());
        assertThat(expectedOverridesCaptor.getValue().size(), is(1));
        XmlOverride xmlOverride = expectedOverridesCaptor.getValue().stream().findFirst().get();
        assertThat(xmlOverride.getFile(), is("conf/server.xml"));
        assertThat(xmlOverride.getAttributeName(), is("maxThreads"));
        assertThat(xmlOverride.getxPathExpression(), is("//Connector"));
        assertThat(xmlOverride.getValue(), is("48"));
    }
}