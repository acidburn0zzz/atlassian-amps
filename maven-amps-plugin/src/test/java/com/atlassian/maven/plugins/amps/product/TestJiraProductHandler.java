package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.XmlOverride;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.BUNDLED_PLUGINS_FROM_4_1;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.BUNDLED_PLUGINS_UNZIPPED;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.BUNDLED_PLUGINS_UPTO_4_0;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.FILENAME_DBCONFIG;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.INSTALLED_PLUGINS_DIR;
import static com.atlassian.maven.plugins.amps.product.JiraProductHandler.PLUGINS_DIR;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.MSSQL;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.MSSQL_JTDS;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.MYSQL;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.ORACLE_10G;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.ORACLE_12C;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.POSTGRES;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestJiraProductHandler
{
    private static File TEMP_HOME;
    
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    
    @Mock
    private MavenContext mavenContext;
    @Mock
    private MavenProject mavenProject;
    @Mock
    private Build build;
    @Captor
    ArgumentCaptor<Collection<XmlOverride>> expectedOverridesCaptor;

    private JiraProductHandler productHandler;

    private static File createTempDir(final String subPath)
    {
        return new File(System.getProperty("java.io.tmpdir"), subPath);
    }

    @Before
    public void setUp() throws Exception {
        when(build.getDirectory()).thenReturn(temporaryFolder.newFolder("jira").getAbsolutePath());
        when(mavenProject.getBuild()).thenReturn(build);
        when(mavenContext.getProject()).thenReturn(mavenProject);
        productHandler = new JiraProductHandler(mavenContext, null, null);

        createTemporaryHomeDirectory();
    }

    public void createTemporaryHomeDirectory() throws IOException
    {
        final File f = File.createTempFile("temp-jira-", "-home");
        if (!f.delete())
        {
            throw new IOException();
        }

        if (!f.mkdir())
        {
            throw new IOException();
        }

        TEMP_HOME = f;
    }

    @After
    public void deleteTemporaryHomeDirectoryAndContents() throws Exception
    {
        if (TEMP_HOME != null)
        {
            FileUtils.deleteDirectory(TEMP_HOME);
            TEMP_HOME = null;
        }
    }

    @Test
    public void itShouldSetNewJvmArgsForJira7_7_0AndHigher() throws Exception
    {
        newArrayList(
                newProduct("7.7.0-ALPHA"), newProduct("7.7.0-SNAPSHOT"), newProduct("7.7-EAP01"), newProduct("7.7.0"),
                newProduct("7.7.1-SNAPSHOT"), newProduct("7.7.2"), newProduct("7.8-rc1"), newProduct("7.8"))
        .forEach(product -> {
            productHandler.fixJvmArgs(product);
            assertThat(format("Jira version %s does not have the correct Xmx", product.getVersion()), product.getJvmArgs(), containsString("-Xmx768m"));
            assertThat(format("Jira version %s does not have the correct Xms", product.getVersion()), product.getJvmArgs(), containsString("-Xms384m"));
        });
    }

    @Test
    public void itShouldUseDefaultJvmArgsForLowerThanJira7_7_0() throws Exception
    {
        newArrayList(
                newProduct("7.6.0"), newProduct("7.6.19-SNAPSHOT"), newProduct("7.1"))
        .forEach(product -> {
            productHandler.fixJvmArgs(product);
            assertThat(format("Jira version %s does not have the correct Xmx", product.getVersion()), product.getJvmArgs(), containsString("-Xmx512m"));
            assertThat(format("Jira version %s has Xms set and should not", product.getVersion()), product.getJvmArgs(), not(containsString("-Xms384m")));
        });
    }

    @Test
    public void dbconfigXmlCreatedWithCorrectPath() throws Exception
    {
        JiraProductHandler.createDbConfigXmlIfNecessary(TEMP_HOME);

        File f = new File(TEMP_HOME, FILENAME_DBCONFIG);
        assertTrue("The config file is created: " + FILENAME_DBCONFIG, f.exists());
        assertTrue("And it's a regular file", f.isFile());

        File dbFile = new File(TEMP_HOME, "database");

        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);

        XPathExpression xpe = XPathFactory.newInstance().newXPath().compile("/jira-database-config/jdbc-datasource/url");

        String x = xpe.evaluate(d);
        assertEquals("The JDBC URI for the embedded database is as expected",
                "jdbc:hsqldb:file:" + dbFile.toURI().getPath(), x);
    }

    @Test
    public void updateDBConfigXmlForOracle10g() throws Exception
    {
        testUpdateDbConfigXml(ORACLE_10G);
    }

    @Test
    public void updateDBConfigXmlForOracle12c() throws Exception
    {
        testUpdateDbConfigXml(ORACLE_12C);
    }

    @Test
    public void updateDBConfigXmlForMysql() throws Exception
    {
        testUpdateDbConfigXml(MYSQL);
    }

    @Test
    public void updateDBConfigXmlForPostgres() throws Exception
    {
        testUpdateDbConfigXml(POSTGRES);
    }

    @Test
    public void updateDBConfigXmlForMssql() throws Exception
    {
        testUpdateDbConfigXml(MSSQL);
    }

    @Test
    public void updateDBConfigXmlForMssqlJTDS() throws Exception
    {
        testUpdateDbConfigXml(MSSQL_JTDS);
    }

    @Test
    public void shouldByDefaultForceJiraSynchronousStartup() throws IOException {
        final Product product = newProduct();
        final Map<String, String> systemProperties = productHandler.getSystemProperties(product);

        // then
        Assert.assertThat(
                systemProperties, hasEntry(
                        "com.atlassian.jira.startup.LauncherContextListener.SYNCHRONOUS", "true"
                ));
    }

    @Test
    public void shouldPassAwaitInitializationFlagFromProduct() throws IOException {
        final Product product = newProduct();
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
        JiraProductHandler.createDbConfigXmlIfNecessary(TEMP_HOME);
        // Setup
        final File f = new File(TEMP_HOME, FILENAME_DBCONFIG);
        final String schema = "test-schema";
        final SAXReader reader = new SAXReader();
        org.dom4j.Document dbConfigXml = reader.read(f);

        // Check default db type
        assertEquals("hsql", getDbType(dbConfigXml));
        assertEquals("PUBLIC", getDbSchema(dbConfigXml));
        // Invoke: update dbconfig.xml
        productHandler.updateDbConfigXml(TEMP_HOME, dbType, schema);
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
    public void dbconfigXmlNotCreatedWhenAlreadyExists() throws Exception
    {
        final File f = new File(TEMP_HOME, FILENAME_DBCONFIG);
        FileUtils.writeStringToFile(f, "Original contents");
        JiraProductHandler.createDbConfigXmlIfNecessary(TEMP_HOME);

        final String after = FileUtils.readFileToString(f);
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
        final File bundledPluginsDir = new File(TEMP_HOME, BUNDLED_PLUGINS_UNZIPPED);
        //noinspection ResultOfMethodCallIgnored
        bundledPluginsDir.mkdirs();
        assertTrue(bundledPluginsDir.exists());
        assertBundledPluginPath("6.3", TEMP_HOME, bundledPluginsDir);
    }

    @Test
    public void bundledPluginsLocationCorrectFor41()
    {
        final File bundledPluginsZip = new File(TEMP_HOME, BUNDLED_PLUGINS_FROM_4_1);
        assertBundledPluginPath("4.1", TEMP_HOME, bundledPluginsZip);
    }

    @Test
    public void bundledPluginsLocationCorrectFor40()
    {
        final File bundledPluginsZip = new File(TEMP_HOME, BUNDLED_PLUGINS_UPTO_4_0);
        assertBundledPluginPath("4.0", TEMP_HOME, bundledPluginsZip);
    }

    @Test
    public void bundledPluginsLocationCorrectForFallback()
    {
        final File bundledPluginsZip = new File(TEMP_HOME, BUNDLED_PLUGINS_FROM_4_1);
        assertBundledPluginPath("not.a.version", TEMP_HOME, bundledPluginsZip);
    }

    @Test
    public void testCustomiseInstanceForServerXmlOverridesForNewJiras() throws MojoExecutionException {
        final Product ctx = mock(Product.class);
        when(ctx.getVersion()).thenReturn("7.12.0");

        final JiraProductHandler spied = spy(productHandler);

        // Execute
        spied.customiseInstance(ctx, new File("./"), new File("./"));

        // Validate
        verify(ctx, times(1)).setCargoXmlOverrides(expectedOverridesCaptor.capture());
        Assert.assertThat(expectedOverridesCaptor.getValue().size(), is(2));
        final Iterator<XmlOverride> valueIterator = expectedOverridesCaptor.getValue().iterator();

        final XmlOverride first = valueIterator.next();
        Assert.assertThat(first.getAttributeName(), is("relaxedPathChars"));
        Assert.assertThat(first.getValue(), is("[]|"));

        final XmlOverride second = valueIterator.next();
        Assert.assertThat(second.getAttributeName(), is("relaxedQueryChars"));
        Assert.assertThat(second.getValue(), is("[]|{}^&#x5c;&#x60;&quot;&lt;&gt;"));
    }

    @Test
    public void testCustomiseInstanceForServerXmlOverridesForOlderJiras() throws MojoExecutionException {
        final Product ctx = mock(Product.class);
        when(ctx.getVersion()).thenReturn("7.11.0");

        final JiraProductHandler spied = spy(productHandler);

        // Execute
        spied.customiseInstance(ctx, new File("./"), new File("./"));

        // Validate we did not overide configuration
        // reason - older Jiras run older tomcat version that are less resticted
        verify(ctx, times(0)).setCargoXmlOverrides(Mockito.any());
    }

    private Product newProduct(String version) {
        final Product product = newProduct();
        product.setVersion(version);
        return product;
    }

    private Product newProduct() {
        final Product product = new Product();
        product.setInstanceId("jira");
        product.setDataSources(newArrayList());
        product.setAwaitFullInitialization(null);
        return product;
    }

    private void assertBundledPluginPath(final String version, final File appDir, final File expectedPath)
    {
        // Set up
        final Log mockLog = mock(Log.class);
        when(mavenContext.getLog()).thenReturn(mockLog);
        final JiraProductHandler productHandler = new JiraProductHandler(mavenContext, null, null);
        final Product mockProduct = mock(Product.class);
        when(mockProduct.getVersion()).thenReturn(version);

        // Invoke
        final File bundledPluginPath = productHandler.getBundledPluginPath(mockProduct, appDir);

        // Check
        assertNotNull(bundledPluginPath);
        assertEquals(expectedPath, bundledPluginPath);
    }
}
