package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.Product;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.INSTALLED_PLUGINS_DIR;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.PLUGINS_DIR;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestJiraProductHandler
{
    static File tempHome;

    private static File createTempDir(final String subPath)
    {
        return new File(System.getProperty("java.io.tmpdir"), subPath);
    }
    
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
    public void pluginsShouldGoIntoLocalHomeIfNoSharedHomeIsSpecified()
    {
        final File localHome = createTempDir("jira-local");
        assertUserInstalledPluginsDirectory(localHome, null, localHome);
    }

    @Test
    public void pluginsShouldGoIntoSharedHomeIfOneIsSpecified()
    {
        final File sharedHome = createTempDir("jira-shared");
        assertUserInstalledPluginsDirectory(null, sharedHome.getPath(), sharedHome);
    }

    private void assertUserInstalledPluginsDirectory(
            final File localHome, final String sharedHomePath, final File expectedParentDir)
    {
        // Set up
        final MavenProject mockMavenProject = mock(MavenProject.class);
        final MavenContext mockMavenContext = mock(MavenContext.class);
        when(mockMavenContext.getProject()).thenReturn(mockMavenProject);
        final JiraProductHandler productHandler = new JiraProductHandler(mockMavenContext, null, null);
        final Product mockProduct = mock(Product.class);
        when(mockProduct.getSharedHome()).thenReturn(sharedHomePath);

        // Invoke
        final File userInstalledPluginsDirectory =
                productHandler.getUserInstalledPluginsDirectory(mockProduct, null, localHome);

        // Check
        assertNotNull(userInstalledPluginsDirectory);
        assertEquals(new File(new File(expectedParentDir, PLUGINS_DIR), INSTALLED_PLUGINS_DIR), userInstalledPluginsDirectory);
    }
}
