package com.atlassian.maven.plugins.amps.product;

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

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.DatabaseType;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils.Replacement;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import static com.atlassian.maven.plugins.amps.util.ConfigFileUtils.RegexReplacement;
import static com.atlassian.maven.plugins.amps.util.FileUtils.fixWindowsSlashes;
import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang.StringUtils.isBlank;

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

    @Override
    public Map<String, String> getSystemProperties(final Product ctx)
    {
        final ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
        properties.putAll(super.getSystemProperties(ctx));
        properties.put("jira.home", fixWindowsSlashes(getHomeDirectory(ctx).getPath()));
        properties.put("cargo.servlet.uriencoding", "UTF-8");
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
        dataSource.setType("javax.sql.DataSource");
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
                new ProductArtifact("jta", "jta", "1.0.1"),
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
        if (ctx.getDataSources().size() > 0)
        {
            final DataSource ds = ctx.getDataSources().get(0);
            updateDbConfigXml(homeDir, DatabaseType.produceDatabaseType(ds.getUrl(), ds.getDriver()), ds.getSchema());
        }
    }

    @VisibleForTesting
    protected void updateDbConfigXml(final File homeDir, final DatabaseType dbType, final String schema)
            throws MojoExecutionException
    {
        final File dbConfigXml = new File(homeDir, FILENAME_DBCONFIG);
        if (!dbConfigXml.exists() || dbType == null)
        {
            return;
        }
        try
        {
            final SAXReader reader = new SAXReader();
            final Document dbConfigDoc = reader.read(dbConfigXml);
            final Node dbTypeNode = dbConfigDoc.selectSingleNode("//jira-database-config/database-type");
            final Node schemaNode = dbConfigDoc.selectSingleNode("//jira-database-config/schema-name");
            boolean modified = false;
            // update database type
            if (null != dbTypeNode && StringUtils.isNotEmpty(dbTypeNode.getStringValue()))
            {
                String currentDbType = dbTypeNode.getStringValue();
                // check null and difference value from dbType
                if (!currentDbType.equals(dbType))
                {
                    // update database type
                    modified = true;
                    dbTypeNode.setText(dbType.getDbType());
                }
            }
            if (null != schemaNode && StringUtils.isNotEmpty(schema))
            {
                if(dbType.isHaveSchema())
                {
                    schemaNode.setText(schema);
                }
                else
                {
                    // remove schema node
                    schemaNode.detach();
                }
            }
            if (modified)
            {
                // write dbconfig.xml
                FileOutputStream fos = new FileOutputStream(dbConfigXml);
                OutputFormat format = OutputFormat.createPrettyPrint();
                XMLWriter writer = new XMLWriter(fos, format);
                try
                {
                    writer.write(dbConfigDoc);
                }
                finally
                {
                    writer.close();
                    closeQuietly(fos);
                }
            }
        }
        catch (DocumentException de)
        {
            throw new MojoExecutionException("Unable parse file dbconfig.xml", de);
        }
        catch (IOException ie)
        {
            throw new MojoExecutionException("Unable to write dbconfig.xml", ie);
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
            throw new MojoExecutionException("Unable to create dbconfig.xml", ioe);
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
            List<ProductArtifact> plugins = new ArrayList<ProductArtifact>();
            plugins.addAll(super.getPdkInstallArtifacts(pdkInstallVersion));
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
}
