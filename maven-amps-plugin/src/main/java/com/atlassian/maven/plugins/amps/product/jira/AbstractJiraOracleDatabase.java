package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.product.ImportMethod;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Map;
import java.util.Properties;

import static com.atlassian.maven.plugins.amps.product.ImportMethod.IMPDP;
import static com.atlassian.maven.plugins.amps.util.MapUtils.join;
import static java.util.Collections.emptyMap;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public abstract class AbstractJiraOracleDatabase extends AbstractJiraDatabase
{
    protected static final String DATA_PUMP_DIR = "DATA_PUMP_DIR";

    protected AbstractJiraOracleDatabase(final DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    protected String dropDatabase()
    {
        return null;
    }

    @Override
    protected String dropUser()
    {
        return null;
    }

    @Override
    protected String createDatabase()
    {
        return null;
    }

    @Override
    protected String createUser()
    {
        return null;
    }

    @Override
    protected String grantPermissionForUser()
    {
        return null;
    }

    @Override
    protected String getDatabaseName(String url)
    {
        return getDataSource().getSchema();
    }

    @Override
    public Xpp3Dom getConfigDatabaseTool() throws MojoExecutionException
    {
        Xpp3Dom configDatabaseTool = null;
        if (IMPDP.equals(ImportMethod.getValueOf(getDataSource().getImportMethod())))
        {
            final File dumpFile = new File(getDataSource().getDumpFilePath());
            final File dumpFileDirectory = dumpFile.getParentFile();
            final String dumpFileName = dumpFile.getName();
            // grant read, write and executable on dump file and parent directory for Oracle to execute import - impdp
            dumpFile.setExecutable(true, false);
            dumpFile.setReadable(true, false);
            dumpFile.setWritable(true, false);
            dumpFileDirectory.setExecutable(true, false);
            dumpFileDirectory.setReadable(true, false);
            dumpFileDirectory.setWritable(true, false);
            configDatabaseTool = configuration(
                    element(name("executable"), "impdp"),
                    element(name("arguments"),
                            element(name("argument"), getDataSource().getUsername() + "/" + getDataSource().getPassword()),
                            element(name("argument"), "DUMPFILE=" + dumpFileName),
                            element(name("argument"), "DIRECTORY=" + DATA_PUMP_DIR)
                    )
            );
        }
        return configDatabaseTool;
    }

    @Override
    public Xpp3Dom getPluginConfiguration()
    {
        // In Oracle, "user" and "schema" are almost the same concept; create/drop user also creates/drops schema.
        final String sql = getSqlToDropAndCreateUser();
        getLog().info("Oracle initialization database SQL: " + sql);
        final Xpp3Dom sqlPluginConfiguration = systemDatabaseConfiguration();
        addChild(sqlPluginConfiguration, "sqlCommand", sql);
        addChild(sqlPluginConfiguration, "delimiter", "/");
        addChild(sqlPluginConfiguration, "delimiterType", "row");
        addChild(sqlPluginConfiguration, "driverProperties", join(getDriverProperties(), "=", ","));
        return sqlPluginConfiguration;
    }

    /**
     * Subclasses should return the SQL (or PL/SQL) command(s) necessary to
     * drop and recreate the JIRA user (i.e. schema). It can be assumed that
     * there is a SYSDBA session in progress.
     *
     * @return a valid SQL string
     */
    protected abstract String getSqlToDropAndCreateUser();

    /**
     * Adds a child node with the given name and value to the given DOM node.
     *
     * @param parentNode the node to receive a new child
     * @param childName the name of the new child node
     * @param childValue the value of the new child node
     */
    private static void addChild(final Xpp3Dom parentNode, final String childName, final String childValue)
    {
        parentNode.addChild(element(name(childName), childValue).toDom());
    }

    /**
     * Subclasses can override this method to set any desired driver
     * properties for the sql-maven-plugin to pass on to JDBC. This
     * implementation returns an empty map.
     *
     * @return the desired property names and values
     * @see java.sql.DriverManager#getConnection(String, Properties)
     */
    @Nonnull
    protected Map<String, String> getDriverProperties() {
        return emptyMap();
    }
}
