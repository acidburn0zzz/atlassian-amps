package com.atlassian.maven.plugins.amps.product;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;

import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.BUNDLED_PLUGINS_FROM_4_1;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.BUNDLED_PLUGINS_UNZIPPED;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.BUNDLED_PLUGINS_UPTO_4_0;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.FILENAME_DBCONFIG;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.INSTALLED_PLUGINS_DIR;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.PLUGINS_DIR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestJiraProductHandler
{
    static File tempHome;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

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
    public void deleteTemporaryHomeDirectoryAndContents() throws Exception
    {
        if (tempHome != null)
        {
            FileUtils.deleteDirectory(tempHome);
            tempHome = null;
        }
    }

    @Test
    public void dbconfigXmlCreatedWithCorrectPath() throws Exception
    {
        JiraProductHandler.createDbConfigXmlIfNecessary(tempHome);

        File f = new File(tempHome, FILENAME_DBCONFIG);
        assertTrue("The config file is created: " + FILENAME_DBCONFIG, f.exists());
        assertTrue("And it's a regular file", f.isFile());

        File dbFile = new File(tempHome, "database");

        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);

        XPathExpression xpe = XPathFactory.newInstance().newXPath().compile("/jira-database-config/jdbc-datasource/url");

        String x = xpe.evaluate(d);
        assertEquals("The JDBC URI for the embedded database is as expected",
                "jdbc:hsqldb:file:" + dbFile.toURI().getPath(), x);
    }

    @Test
    public void updateDBConfigXmlForOracle() throws Exception
    {
        testUpdateDbConfigXml(JiraDatabaseType.ORACLE);
    }

    @Test
    public void updateDBConfigXmlForMysql() throws Exception
    {
        testUpdateDbConfigXml(JiraDatabaseType.MYSQL);
    }

    @Test
    public void updateDBConfigXmlForPostgres() throws Exception
    {
        testUpdateDbConfigXml(JiraDatabaseType.POSTGRES);
    }

    @Test
    public void updateDBConfigXmlForMssql() throws Exception
    {
        testUpdateDbConfigXml(JiraDatabaseType.MSSQL);
    }

    @Test
    public void updateDBConfigXmlForMssqlJTDS() throws Exception
    {
        testUpdateDbConfigXml(JiraDatabaseType.MSSQL_JTDS);
    }

    @Test
    public void shouldByDefaultForceJiraSynchronousStartup() throws IOException {
        final MavenContext mockMavenContext = mock(MavenContext.class);
        MavenProject projectMock = mock(MavenProject.class);
        Build buildMock = mock(Build.class);
        when(buildMock.getDirectory()).thenReturn(temporaryFolder.newFolder("jira").getAbsolutePath());
        when(projectMock.getBuild()).thenReturn(buildMock);
        when(mockMavenContext.getProject()).thenReturn(projectMock);
        final JiraProductHandler productHandler = new JiraProductHandler(mockMavenContext, null, null);
        final Product product = new Product();
        product.setInstanceId("jira");
        product.setDataSources(Lists.<DataSource>newArrayList());

        // when
        product.setAwaitFullInitialization(null);
        final Map<String, String> systemProperties = productHandler.getSystemProperties(product);

        // then
        Assert.assertThat(
                systemProperties, hasEntry(
                        "com.atlassian.jira.startup.LauncherContextListener.SYNCHRONOUS", "true"
                ));
    }

    @Test
    public void shouldPassAwaitInitializationFlagFromProduct() throws IOException {
        final MavenContext mockMavenContext = mock(MavenContext.class);
        MavenProject projectMock = mock(MavenProject.class);
        Build buildMock = mock(Build.class);
        when(buildMock.getDirectory()).thenReturn(temporaryFolder.newFolder("jira").getAbsolutePath());
        when(projectMock.getBuild()).thenReturn(buildMock);
        when(mockMavenContext.getProject()).thenReturn(projectMock);
        final JiraProductHandler productHandler = new JiraProductHandler(mockMavenContext, null, null);
        final Product product = new Product();
        product.setInstanceId("jira");
        product.setDataSources(Lists.<DataSource>newArrayList());

        // when
        product.setAwaitFullInitialization(true);
        final Map<String, String> systemPropertiesWithAwait = productHandler.getSystemProperties(product);

        product.setAwaitFullInitialization(false);
        final Map<String, String> systemPropertiesNoWait = productHandler.getSystemProperties(product);

        // then
        assertThat(
                systemPropertiesWithAwait, hasEntry(
                        "com.atlassian.jira.startup.LauncherContextListener.SYNCHRONOUS", "true"
                ));
        assertThat(
                systemPropertiesNoWait, not(hasKey(
                        "com.atlassian.jira.startup.LauncherContextListener.SYNCHRONOUS"
                )));
    }

    private void testUpdateDbConfigXml(JiraDatabaseType dbType) throws Exception
    {
        // Create default dbconfig.xml
        JiraProductHandler.createDbConfigXmlIfNecessary(tempHome);
        // Setup
        final File f = new File(tempHome, FILENAME_DBCONFIG);
        final String schema = "test-schema";
        final SAXReader reader = new SAXReader();
        org.dom4j.Document dbConfigXml = reader.read(f);
        final MavenContext mockMavenContext = mock(MavenContext.class);
        final JiraProductHandler productHandler = new JiraProductHandler(mockMavenContext, null, null);
        // Check default db type
        assertEquals("hsql", getDbType(dbConfigXml));
        assertEquals("PUBLIC", getDbSchema(dbConfigXml));
        // Invoke: update dbconfig.xml
        productHandler.updateDbConfigXml(tempHome, dbType, schema);
        dbConfigXml = reader.read(f);
        // Check
        assertEquals(dbType.getDbType(), getDbType(dbConfigXml));
        if (dbType.hasSchema())
        {
            assertThat("Schema has to update", schema.equals(getDbSchema(dbConfigXml)), is(true));
        }
        else
        {
            assertThat("Schema has not to update", schema.equals(getDbSchema(dbConfigXml)), is(false));
        }
    }

    private String getDbType(org.dom4j.Document dbConfigXml) throws Exception
    {

        final Node dbTypeNode = dbConfigXml.selectSingleNode("//jira-database-config/database-type");
        return dbTypeNode == null ? "" : dbTypeNode.getStringValue();
    }

    private String getDbSchema(org.dom4j.Document dbConfigXml) throws Exception
    {
        final Node schemaNode = dbConfigXml.selectSingleNode("//jira-database-config/schema-name");
        return schemaNode == null ? "" : schemaNode.getStringValue();
    }

    @Test
    public void getDatabaseTypeWithNull() throws Exception
    {
        final JiraDatabaseType dbType = JiraDatabaseType.getDatabaseType(null, null);
        assertNull(dbType);


    }
    @Test
    public void getDatabaseTypePostgresUriAndDriver() throws Exception
    {
        final JiraDatabaseType dbType = JiraDatabaseType.getDatabaseType("jdbc:postgresql://localhost:5432/amps-test", "org.postgresql.Driver");
        assertNotNull(dbType);
        assertThat("Database type must be postgres72", dbType.getDbType(), equalTo(JiraDatabaseType.POSTGRES.getDbType()));
    }

    @Test
    public void getDatabaseTypeMssqlUriAndDriver() throws Exception
    {
        final JiraDatabaseType dbType = JiraDatabaseType.getDatabaseType("jdbc:sqlserver://amps-test", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        assertNotNull(dbType);
        assertEquals(dbType.getDbType(), JiraDatabaseType.MSSQL.getDbType());
    }

    @Test
    public void getDatabaseTypeMssqlUriAndAcrossDriver() throws Exception
    {
        final JiraDatabaseType dbType = JiraDatabaseType.getDatabaseType("jdbc:sqlserver://amps-test;user=MyUserName;password=*****;", "net.sourceforge.jtds.jdbc.Driver");
        assertNull(dbType);
    }

    @Test
    public void dbconfigXmlNotCreatedWhenAlreadyExists() throws MojoExecutionException, IOException
    {
        File f = new File(tempHome, FILENAME_DBCONFIG);
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

    @Test
    public void bundledPluginsShouldBeUnzippedIfPresent()
    {
        final File bundledPluginsDir = new File(tempHome, BUNDLED_PLUGINS_UNZIPPED);
        bundledPluginsDir.mkdirs();
        assertTrue(bundledPluginsDir.exists());
        assertBundledPluginPath("6.3", tempHome, bundledPluginsDir);
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
        final JiraProductHandler productHandler = new JiraProductHandler(mockMavenContext, null, null);
        final Product mockProduct = mock(Product.class);
        when(mockProduct.getVersion()).thenReturn(version);

        // Invoke
        final File bundledPluginPath = productHandler.getBundledPluginPath(mockProduct, appDir);

        // Check
        assertNotNull(bundledPluginPath);
        assertEquals(expectedPath, bundledPluginPath);
    }
}
