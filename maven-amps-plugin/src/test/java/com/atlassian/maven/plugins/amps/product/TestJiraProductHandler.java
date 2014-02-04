package com.atlassian.maven.plugins.amps.product;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.Product;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestJiraProductHandler
{
    static final String BUNDLED_PLUGINS_FROM_4_1 = "WEB-INF/classes/atlassian-bundled-plugins.zip";
    static final String BUNDLED_PLUGINS_UPTO_4_0 = "WEB-INF/classes/com/atlassian/jira/plugin/atlassian-bundled-plugins.zip";

    static File tempHome;
    
    @Before
    public void createTemporaryHomeDirectory() throws IOException
    {
        File f = File.createTempFile("temp-jira-", "-home");
        if (!f.delete())
        {
            throw new IOException();
        }
        
        if (!f.mkdir())
        {
            throw new IOException();
        }
        
        tempHome = f;
    }
    
    @After
    public void deleteFileAndTemporaryHomeDirectory() throws Exception
    {
        if (tempHome != null)
        {
            new File(tempHome, "dbconfig.xml").delete();
            tempHome.delete();
        }
    }

    @Test
    public void dbconfigXmlCreatedWithCorrectPath() throws Exception
    {
        JiraProductHandler.createDbConfigXmlIfNecessary(tempHome);
        
        File f = new File(tempHome, "dbconfig.xml");
        assertTrue("The dbconfig.xml is created", f.exists());
        assertTrue("And it's a regular file", f.isFile());

        File dbFile = new File(tempHome, "database");
        
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
        
        XPathExpression xpe = XPathFactory.newInstance().newXPath().compile("/jira-database-config/jdbc-datasource/url");
        
        String x = xpe.evaluate(d);
        assertEquals("The JDBC URI for the embedded database is as expected",
                "jdbc:hsqldb:file:" + dbFile.toURI().getPath(), x);
    }
    
    @Test
    public void dbconfigXmlNotCreatedWhenAlreadyExists() throws MojoExecutionException, IOException
    {
        File f = new File(tempHome, "dbconfig.xml");
        FileUtils.writeStringToFile(f, "Original contents");
        JiraProductHandler.createDbConfigXmlIfNecessary(tempHome);
        
        String after = FileUtils.readFileToString(f);
        assertEquals("Original contents", after);
    }

    @Test
    public void bundledPluginsLocationCorrectFor41()
    {
        final File bundledPluginsZip = new File(tempHome, BUNDLED_PLUGINS_FROM_4_1);
        assertBundledPluginPath("4.1", tempHome, bundledPluginsZip);
    }

    @Test
    public void bundledPluginsLocationCorrectFor40()
    {
        final File bundledPluginsZip = new File(tempHome, BUNDLED_PLUGINS_UPTO_4_0);
        assertBundledPluginPath("4.0", tempHome, bundledPluginsZip);
    }

    @Test
    public void bundledPluginsLocationCorrectForFallback()
    {
        final File bundledPluginsZip = new File(tempHome, BUNDLED_PLUGINS_FROM_4_1);
        assertBundledPluginPath("not.a.version", tempHome, bundledPluginsZip);
    }

    private void assertBundledPluginPath(final String version, final File appDir, final File expectedPath)
    {
        // Set up

        final Log mockLog = mock(Log.class);
        final MavenContext mockMavenContext = mock(MavenContext.class);
        when(mockMavenContext.getLog()).thenReturn(mockLog);
        final JiraProductHandler productHandler = new JiraProductHandler(mockMavenContext, null);
        final Product mockProduct = mock(Product.class);
        when(mockProduct.getVersion()).thenReturn(version);

        // Invoke
        final File bundledPluginPath = productHandler.getBundledPluginPath(mockProduct, appDir);

        // Check
        assertNotNull(bundledPluginPath);
        assertEquals(expectedPath, bundledPluginPath);
    }
}
