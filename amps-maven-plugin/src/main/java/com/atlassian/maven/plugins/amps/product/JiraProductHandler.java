package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.XmlOverride;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils.Replacement;
import com.atlassian.maven.plugins.amps.util.JvmArgsFix;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.getDatabaseType;
import static com.atlassian.maven.plugins.amps.util.ConfigFileUtils.RegexReplacement;
import static com.atlassian.maven.plugins.amps.util.FileUtils.fixWindowsSlashes;
import static java.lang.String.format;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class JiraProductHandler extends AbstractWebappProductHandler
{
    @VisibleForTesting
    static final String INSTALLED_PLUGINS_DIR = "installed-plugins";

    @VisibleForTesting
    static final String PLUGINS_DIR = "plugins";

    @VisibleForTesting
    static final String BUNDLED_PLUGINS_UNZIPPED = "WEB-INF/atlassian-bundled-plugins";

    @VisibleForTesting
    static final String BUNDLED_PLUGINS_FROM_4_1 = "WEB-INF/classes/atlassian-bundled-plugins.zip";

    @VisibleForTesting
    static final String BUNDLED_PLUGINS_UPTO_4_0 = "WEB-INF/classes/com/atlassian/jira/plugin/atlassian-bundled-plugins.zip";

    @VisibleForTesting
    static final String FILENAME_DBCONFIG = "dbconfig.xml";

    private static final String JIRADS_PROPERTIES_FILE = "JiraDS.properties";

    private static final String JIRA_HOME_PLACEHOLDER = "${jirahome}";

    protected static final String H2_JDBC_URL_TEMPLATE = "jdbc:h2:file:%s;MV_STORE=FALSE;MVCC=TRUE";
    public static final String H2_SUFFIX = "database/h2db";

    private static void checkNotFile(final File sharedHomeDir)
    {
        if (sharedHomeDir.isFile())
        {
            final String error =
                    String.format("The specified shared home '%s' is a file, not a directory", sharedHomeDir);
            throw new IllegalArgumentException(error);
        }
    }

    private static void createIfNotExists(final File sharedHome)
    {
        sharedHome.mkdirs();
        if (!sharedHome.isDirectory())
        {
            final String error = String.format("The specified shared home '%s' cannot be created", sharedHome);
            throw new IllegalStateException(error);
        }
    }

    public JiraProductHandler(final MavenContext context, final MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, new JiraPluginProvider(), artifactFactory);
    }

    public String getId()
    {
        return "jira";
    }

    @Override
    protected void customiseInstance(Product ctx, File homeDir, File explodedWarDir) {
        // Jira 7.12.x has new tomcat version which requires additional characters to be whitelisted
        if (new ComparableVersion(ctx.getVersion()).compareTo(new ComparableVersion("7.12.0")) >= 0) {
            ctx.setCargoXmlOverrides(serverXmlJiraOverride());
        }
    }

    @Override
    protected void fixJvmArgs(Product ctx) {
        ComparableVersion productVersion = new ComparableVersion(ctx.getVersion());
        // Jira 8 raises memory requirements, to account for increased memory usage by Lucene
        if (productVersion.compareTo(new ComparableVersion("8.0.0-ALPHA")) >= 0)
        {
            ctx.setJvmArgs(JvmArgsFix.empty()
                    .with("-Xmx", "2g")
                    .with("-Xms", "1g")
                    .apply(ctx.getJvmArgs()));
        }
        // In Jira 7.7+ we have a HealthCheck that requires min / max memory to be set to a certain minimums or it can block startup.
        else if (productVersion.compareTo(new ComparableVersion("7.7.0-ALPHA")) >= 0)
        {
            ctx.setJvmArgs(JvmArgsFix.empty()
                    .with("-Xmx", "768m")
                    .with("-Xms", "384m")
                    .apply(ctx.getJvmArgs()));
        }
        else
        {
            super.fixJvmArgs(ctx);
        }
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.jira", "atlassian-jira-webapp", "RELEASE");
    }

    @Override
    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.jira.plugins", "jira-plugin-test-resources");
    }

    @Override
    public int getDefaultHttpPort()
    {
        return 2990;
    }

    @Override
    public int getDefaultHttpsPort()
    {
        return 8442;
    }

    // only neeeded for older versions of JIRA; 7.0 onwards will have JiraDS.properties
    protected static File getHsqlDatabaseFile(final File homeDirectory)
    {
        return new File(homeDirectory, "database");
    }

    @Override
    public String getDefaultContainerId()
    {
        return "tomcat7x";
    }

    @Nonnull
    @Override
    protected Collection<String> getExtraJarsToSkipWhenScanningForTldsAndWebFragments() {
        // Fixes AMPS-1429 by skipping these JARs
        return ImmutableList.of("jotm*.jar", "xapool*.jar");
    }

    @Override
    public Map<String, String> getSystemProperties(final Product ctx)
    {
        final ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
        properties.putAll(super.getSystemProperties(ctx));
        properties.put("jira.home", fixWindowsSlashes(getHomeDirectory(ctx).getPath()));
        properties.put("cargo.servlet.uriencoding", "UTF-8");
        final boolean forceAwaitFullInit = MoreObjects.firstNonNull(ctx.isAwaitFullInitialization(), Boolean.TRUE);
        if (forceAwaitFullInit) {
            properties.put("com.atlassian.jira.startup.LauncherContextListener.SYNCHRONOUS", "true");
        }
        return properties.build();
    }

    @Override
    protected DataSource getDefaultDataSource(Product ctx)
    {
        DataSource dataSource = new DataSource();

        final String jiraHome = fixWindowsSlashes(getHomeDirectory(ctx).getAbsolutePath());

        final File dsPropsFile = new File(getHomeDirectory(ctx), JIRADS_PROPERTIES_FILE);
        if (dsPropsFile.exists())
        {
            final Properties dsProps = new Properties();
            try
            {
                // read properties from the JiraDS.properties
                dsProps.load(new FileInputStream(dsPropsFile));
                dataSource.setJndi(dsProps.getProperty("jndi"));
                dataSource.setUrl(dsProps.getProperty("url").replace(JIRA_HOME_PLACEHOLDER, jiraHome));
                dataSource.setDriver(dsProps.getProperty("driver-class"));
                dataSource.setUsername(dsProps.getProperty("username"));
                dataSource.setPassword(dsProps.getProperty("password"));
                return dataSource;
            }
            catch (IOException e)
            {
                // nothing we can do here; fall back to defaults
                log.warn("failed to read " + dsPropsFile.getAbsolutePath(), e);
            }
        }

        // legacy JIRA without JiraDS.properties; still uses HSQL
        dataSource.setJndi("jdbc/JiraDS");
        dataSource.setUrl(format("jdbc:hsqldb:%s/database", jiraHome));
        dataSource.setDriver("org.hsqldb.jdbcDriver");
        //dataSource.setType("javax.sql.DataSource");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Override
    public File getUserInstalledPluginsDirectory(final Product product, final File webappDir, final File homeDir)
    {
        final File pluginHomeDirectory = getPluginHomeDirectory(product.getSharedHome(), homeDir);
        return new File(new File(pluginHomeDirectory, PLUGINS_DIR), INSTALLED_PLUGINS_DIR);
    }

    private File getPluginHomeDirectory(final String sharedHomePath, final File homeDir)
    {
        if (isBlank(sharedHomePath))
        {
            return homeDir;
        }

        // A shared home was specified
        final File sharedHomeDir = new File(sharedHomePath);
        checkNotFile(sharedHomeDir);
        createIfNotExists(sharedHomeDir);
        return sharedHomeDir;
    }

    @Override
    public List<ProductArtifact> getExtraContainerDependencies()
    {
        return Arrays.asList(
                new ProductArtifact("hsqldb", "hsqldb", "1.8.0.5"),
                new ProductArtifact("javax.transaction", "jta", "1.0.1B"),
                new ProductArtifact("ots-jts", "ots-jts", "1.0"),

                // for data source and transaction manager providers
                new ProductArtifact("jotm", "jotm", "1.4.3"),
                new ProductArtifact("jotm", "jotm-jrmp_stubs", "1.4.3"),
                new ProductArtifact("jotm", "jotm-iiop_stubs", "1.4.3"),
                new ProductArtifact("jotm", "jonas_timer", "1.4.3"),
                new ProductArtifact("jotm", "objectweb-datasource", "1.4.3"),
                new ProductArtifact("carol", "carol", "1.5.2"),
                new ProductArtifact("carol", "carol-properties", "1.0"),
                new ProductArtifact("xapool", "xapool", "1.3.1"),
                new ProductArtifact("commons-logging", "commons-logging", "1.1.1")
        );
    }

    @Override
    public File getBundledPluginPath(Product ctx, File appDir)
    {
        // the zip became a directory in 6.3, so if the directory exists and is a directory, use it,
        // otherwise fallback to the old behaviour.
        final File bundleDir = new File(appDir, BUNDLED_PLUGINS_UNZIPPED);

        if (bundleDir.exists() && bundleDir.isDirectory())
        {
            return bundleDir;
        }
        else
        {
            // this location used from 4.1 onwards (inclusive), until replaced by unzipped dir.
            String bundledPluginPluginsPath = BUNDLED_PLUGINS_FROM_4_1;
            String[] version = ctx.getVersion().split("-", 2)[0].split("\\.");
            try
            {
                long major = Long.parseLong(version[0]);
                long minor = (version.length > 1) ? Long.parseLong(version[1]) : 0;

                if (major < 4 || major == 4 && minor == 0)
                {
                    bundledPluginPluginsPath = BUNDLED_PLUGINS_UPTO_4_0;
                }
            }
            catch (NumberFormatException e)
            {
                log.debug(String.format("Unable to parse JIRA version '%s', assuming JIRA 4.1 or newer.", ctx.getVersion()), e);
            }
            return new File(appDir, bundledPluginPluginsPath);
        }
    }

    @Override
    public void processHomeDirectory(final Product ctx, final File homeDir) throws MojoExecutionException
    {
        super.processHomeDirectory(ctx, homeDir);
        createDbConfigXmlIfNecessary(homeDir);
        if (ctx.getDataSources().size() == 1)
        {
            final DataSource ds = ctx.getDataSources().get(0);
            final JiraDatabaseType dbType = getDatabaseType(ds).orElseThrow(
                    () -> new MojoExecutionException("Could not find database type for " + ds));
            updateDbConfigXml(homeDir, dbType, ds.getSchema());
        }
        else if (ctx.getDataSources().size() > 1)
        {
            throw new MojoExecutionException("JIRA does not support multiple data sources");
        }
    }

    /**
     * Update JIRA dbconfig.xml in case user provide their own database connection configuration in pom
     * Jira database type was detected by uri/url prefix and database driver
     * Jira database type defines database-type and schema or schema-less for specific Jira database
     * Please refer documentation url: http://www.atlassian.com/software/jira/docs/latest/databases/index.html
     * example:
     * <pre>
     * {@code
     * <dataSource>
     *   <jndi>${dataSource.jndi}</jndi>
     *   <url>${dataSource.url}</url>
     *   <driver>${dataSource.driver}</driver>
     *   <username>${dataSource.user}</username>
     *   <password>${dataSource.password}</password>
     *   <schema>${dataSource.schema}</schema>
     * </dataSource>
     * }
     * </pre>
     * @param homeDir the application's home directory
     * @param dbType  the database type in use
     * @param schema  the schema to use
     * @throws MojoExecutionException if {@code dbconfig.xml} can't be updated
     */
    @VisibleForTesting
    public void updateDbConfigXml(final File homeDir, final JiraDatabaseType dbType, final String schema)
            throws MojoExecutionException
    {
        final File dbConfigXml = new File(homeDir, FILENAME_DBCONFIG);
        if (!dbConfigXml.exists() || dbType == null)
        {
            return;
        }
        final SAXReader reader = new SAXReader();
        final Document dbConfigDoc;
        try
        {
            dbConfigDoc = reader.read(dbConfigXml);
        }
        catch (DocumentException de)
        {
            throw new MojoExecutionException("Cannot parse database configuration xml file", de);
        }
        
        final Node dbTypeNode = dbConfigDoc.selectSingleNode("//jira-database-config/database-type");
        final Node schemaNode = dbConfigDoc.selectSingleNode("//jira-database-config/schema-name");
        final Node jdbcUrl = dbConfigDoc.selectSingleNode("//jira-database-config/jdbc-datasource/url");

        boolean modified = false;
        // update database type
        if (null != dbTypeNode && StringUtils.isNotEmpty(dbTypeNode.getStringValue()))
        {
            String currentDbType = dbTypeNode.getStringValue();
            // check null and difference value from dbType
            if (!currentDbType.equals(dbType.getDbType()))
            {
                // update database type
                modified = true;
                dbTypeNode.setText(dbType.getDbType());
            }
        }

        switch (dbType) {
            case H2:
                if (jdbcUrl != null) {
                    jdbcUrl.setText(String.format(H2_JDBC_URL_TEMPLATE, getFile(homeDir, H2_SUFFIX)));
                    modified = true;
                } else {
                    log.warn("dbconfig.xml doesn't contain jdbc-url");
                }
                break;
            case HSQL: // HSQL seems not to be used in jira anymore
                log.warn("HSQL jdbc-url modification is not supported in this implementation");
        }


        // depend on database type which Jira supported schema or schema-less
        // please refer this Jira documentation
        // http://www.atlassian.com/software/jira/docs/latest/databases/index.html

        // postgres, mssql, hsql
        if (dbType.hasSchema())
        {
            if (StringUtils.isEmpty(schema))
            {
                throw new MojoExecutionException("Database configuration missed schema");
            }
            if (null == schemaNode)
            {
                // add schema-name node
                try
                {
                    dbConfigDoc.selectSingleNode("//jira-database-config").getDocument().addElement("schema-name").addText(schema);
                    modified = true;
                }
                catch (NullPointerException npe)
                {
                    throw new MojoExecutionException(npe.getMessage());
                }
            }
            else
            {
                if(StringUtils.isNotEmpty(schemaNode.getText()) && !schema.equals(schemaNode.getText()))
                {
                    schemaNode.setText(schema);
                    modified = true;
                }
            }
        }
        // mysql, oracle
        else
        {
            // remove schema node
            schemaNode.detach();
            modified = true;
        }
        if (modified)
        {
            try
            {
                writeDbConfigXml(dbConfigXml, dbConfigDoc);
            }
            catch (IOException ioe)
            {
                throw new MojoExecutionException("Could not write database configuration file: " + FILENAME_DBCONFIG, ioe);
            }
        }
    }

    private void writeDbConfigXml(final File dbConfigXml, final Document dbConfigDoc) throws IOException
    {
        // write dbconfig.xml
        try (FileOutputStream fos = new FileOutputStream(dbConfigXml))
        {
            XMLWriter writer = new XMLWriter(fos, OutputFormat.createPrettyPrint());
            writer.write(dbConfigDoc);
            writer.close();
        }
    }

    @Override
    public List<Replacement> getReplacements(Product ctx)
    {
        String contextPath = ctx.getContextPath();
        if (!contextPath.startsWith("/"))
        {
            contextPath = "/" + contextPath;
        }

        final String baseUrl = ctx.getProtocol() + "://" + ctx.getServer() +
                ":" + ctx.getHttpPort() + contextPath;

        List<Replacement> replacements = super.getReplacements(ctx);
        File homeDir = getHomeDirectory(ctx);
        // We don't rewrap snapshots with these values:
        replacements.add(0, new Replacement("http://localhost:8080", baseUrl, false));
        replacements.add(new Replacement("@project-dir@", homeDir.getParent(), false));
        replacements.add(new Replacement("/jira-home/", "/home/", false));
        replacements.add(new Replacement("@base-url@", baseUrl, false));
        replacements.add(new RegexReplacement("'[A-B]{1}[A-Z0-9]{3}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}'", "''")); // blank out the server ID
        return replacements;
    }

    @Override
    public List<File> getConfigFiles(Product product, File homeDir)
    {
        List<File> configFiles = super.getConfigFiles(product, homeDir);
        configFiles.add(new File(homeDir, "database.log"));
        configFiles.add(new File(homeDir, "database.script"));
        configFiles.add(new File(homeDir, FILENAME_DBCONFIG));
        return configFiles;
    }

    // only neeeded for older versions of JIRA; 7.0 onwards will have JiraDS.properties
    static void createDbConfigXmlIfNecessary(File homeDir) throws MojoExecutionException
    {
        File dbConfigXml = new File(homeDir, FILENAME_DBCONFIG);
        if (dbConfigXml.exists())
        {
            return;
        }

        InputStream templateIn = JiraProductHandler.class.getResourceAsStream("jira-dbconfig-template.xml");
        if (templateIn == null)
        {
            throw new MojoExecutionException("Missing internal resource: jira-dbconfig-template.xml");
        }

        try
        {
            String template = IOUtils.toString(templateIn, "utf-8");

            File dbFile = getHsqlDatabaseFile(homeDir);
            String jdbcUrl = "jdbc:hsqldb:file:" + dbFile.toURI().getPath();
            String result = template.replace("@jdbc-url@", jdbcUrl);
            FileUtils.writeStringToFile(dbConfigXml, result, "utf-8");
        }
        catch (IOException ioe)
        {
            throw new MojoExecutionException("Unable to create config file: " + FILENAME_DBCONFIG, ioe);
        }
    }

    @Override
    public List<ProductArtifact> getDefaultLibPlugins()
    {
        return Collections.emptyList();
    }

    @Override
    public List<ProductArtifact> getDefaultBundledPlugins()
    {
        return Collections.emptyList();
    }

    private static class JiraPluginProvider extends AbstractPluginProvider
    {

        @Override
        protected Collection<ProductArtifact> getSalArtifacts(String salVersion)
        {
            return Arrays.asList(
                    new ProductArtifact("com.atlassian.sal", "sal-api", salVersion),
                    new ProductArtifact("com.atlassian.sal", "sal-jira-plugin", salVersion));
        }

        @Override
        protected Collection<ProductArtifact> getPdkInstallArtifacts(String pdkInstallVersion)
        {
            List<ProductArtifact> plugins = new ArrayList<>(super.getPdkInstallArtifacts(pdkInstallVersion));
            plugins.add(new ProductArtifact("commons-fileupload", "commons-fileupload", "1.2.1"));
            return plugins;
        }
    }

    @Override
    public void cleanupProductHomeForZip(Product product, File snapshotDir) throws MojoExecutionException, IOException
    {
        super.cleanupProductHomeForZip(product, snapshotDir);

        FileUtils.deleteQuietly(new File(snapshotDir, "log/atlassian-jira.log"));
    }

    private Collection<XmlOverride> serverXmlJiraOverride() {
        return Collections.unmodifiableList(Arrays.asList(
                new XmlOverride("conf/server.xml", "//Connector", "relaxedPathChars", "[]|"),
                new XmlOverride("conf/server.xml", "//Connector", "relaxedQueryChars", "[]|{}^\\`\"<>")
        ));
    }
}
