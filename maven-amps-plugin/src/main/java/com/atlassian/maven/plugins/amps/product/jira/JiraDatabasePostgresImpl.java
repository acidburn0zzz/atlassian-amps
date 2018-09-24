package com.atlassian.maven.plugins.amps.product.jira;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.product.ImportMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public class JiraDatabasePostgresImpl extends AbstractJiraDatabase
{
    private static final String DROP_DATABASE = "DROP DATABASE IF EXISTS \"%s\";";
    private static final String DROP_USER = "DROP USER IF EXISTS \"%s\";";
    private static final String CREATE_DATABASE = "CREATE DATABASE \"%s\";";
    private static final String CREATE_USER = "CREATE USER \"%s\" WITH PASSWORD '%s' ;";
    private static final String GRANT_PERMISSION = "ALTER ROLE \"%s\" superuser; ALTER DATABASE \"%s\" OWNER TO \"%s\";";

    public JiraDatabasePostgresImpl(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    protected String dropDatabase() throws MojoExecutionException
    {
        return String.format(DROP_DATABASE, getDatabaseName(getDataSource().getUrl()));
    }

    @Override
    protected String dropUser()
    {
        return String.format(DROP_USER, getDataSource().getUsername());
    }

    @Override
    protected String createDatabase() throws MojoExecutionException
    {
        return String.format(CREATE_DATABASE, getDatabaseName(getDataSource().getUrl()));
    }

    @Override
    protected String createUser()
    {
        return String.format(CREATE_USER, getDataSource().getUsername(), getDataSource().getPassword());
    }

    @Override
    protected String grantPermissionForUser() throws MojoExecutionException
    {
        return String.format(GRANT_PERMISSION, getDataSource().getUsername(), getDatabaseName(getDataSource().getUrl()), getDataSource().getUsername());
    }

    @Override
    public Xpp3Dom getConfigDatabaseTool() throws MojoExecutionException
    {
        Xpp3Dom configDatabaseTool = null;
        if (ImportMethod.PSQL.equals(ImportMethod.getValueOf(getDataSource().getImportMethod())))
        {
            configDatabaseTool = configuration(
                    element(name("executable"), "psql"),
                    element(name("arguments"),
                            element(name("argument"), "-f" + getDataSource().getDumpFilePath()),
                            element(name("argument"), "-U" + getDataSource().getUsername()),
                            element(name("argument"), getDatabaseName(getDataSource().getUrl()))
                    )
            );
        }
        return configDatabaseTool;
    }

    /**
     * reference postgres 9.3 documentation: Connecting to the Database http://jdbc.postgresql.org/documentation/93/connect.html
     * With JDBC, a database is represented by a URL (Uniform Resource Locator) takes one of the following forms:
     * jdbc:postgresql:database jdbc:postgresql://host/database jdbc:postgresql://host:port/database
     *
     * @return database name
     */
    @Override
    protected String getDatabaseName(String url) throws MojoExecutionException
    {
        String databaseName = StringUtils.EMPTY;
        try
        {
            Class.forName(getDataSource().getDriver());
        }
        catch (ClassNotFoundException e)
        {
            throw new MojoExecutionException("Could not load Postgresql database library to classpath");
        }
        try
        {
            Driver driver = DriverManager.getDriver(url);
            DriverPropertyInfo[] driverPropertyInfos = driver.getPropertyInfo(url, null);
            if (null != driverPropertyInfos)
            {
                for(DriverPropertyInfo driverPropertyInfo : driverPropertyInfos)
                {
                    if ("PGDBNAME".equals(driverPropertyInfo.name))
                    {
                        databaseName = driverPropertyInfo.value;
                        break;
                    }
                }
            }
            // bug from postgresql JDBC <= 9.3
            if(null == databaseName)
            {
                // apply local monkey-patch
                Properties driverProps = new Properties();
                driverProps = parseURL(url, driverProps);
                if (null != driverProps)
                {
                    databaseName = driverProps.getProperty("PGDBNAME");
                }
            }
        }
        catch (SQLException e)
        {
            throw new MojoExecutionException("No suitable driver");
        }
        return databaseName;
    }

    @Override
    public Xpp3Dom getPluginConfiguration() throws MojoExecutionException
    {
        String sql = dropDatabase() + dropUser() + createDatabase() + createUser() + grantPermissionForUser();
        getLog().info("Postgres initialization database sql: " + sql);
        Xpp3Dom pluginConfiguration = systemDatabaseConfiguration();
        pluginConfiguration.addChild(
                element(name("sqlCommand"), sql).toDom()
        );
        return pluginConfiguration;
    }


    /**
     * Local monkey-path from org.postgresql:postgresql:9.3-1102-jdbc41.jar!/org/postgresql/Driver.java
     * Constructs a new DriverURL, splitting the specified URL into its
     * component parts
     * @param url JDBC URL to parse
     * @param defaults Default properties
     * @return Properties with elements added from the url
     * @exception SQLException
     */
    public Properties parseURL(String url, Properties defaults) throws SQLException
    {
        Properties urlProps = new Properties(defaults);
        String l_urlServer = url;
        String l_urlArgs = "";
        int l_qPos = url.indexOf('?');
        if (l_qPos != -1)
        {
            l_urlServer = url.substring(0, l_qPos);
            l_urlArgs = url.substring(l_qPos + 1);
        }
        if (!l_urlServer.startsWith("jdbc:postgresql:")) {
            return null;
        }
        l_urlServer = l_urlServer.substring("jdbc:postgresql:".length());
        if (l_urlServer.startsWith("//")) {
            l_urlServer = l_urlServer.substring(2);
            int slash = l_urlServer.indexOf('/');
            if (slash == -1) {
                return null;
            }
            urlProps.setProperty("PGDBNAME", l_urlServer.substring(slash + 1));
            String[] addresses = l_urlServer.substring(0, slash).split(",");
            StringBuffer hosts = new StringBuffer();
            StringBuffer ports = new StringBuffer();
            for (int addr = 0; addr < addresses.length; ++addr) {
                String address = addresses[addr];

                int portIdx = address.lastIndexOf(':');
                if (portIdx != -1 && address.lastIndexOf(']') < portIdx) {
                    String portStr = address.substring(portIdx + 1);
                    try {
                        Integer.parseInt(portStr);
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                    ports.append(portStr);
                    hosts.append(address.subSequence(0, portIdx));
                } else {
                    ports.append("5432");
                    hosts.append(address);
                }
                ports.append(',');
                hosts.append(',');
            }
            ports.setLength(ports.length() - 1);
            hosts.setLength(hosts.length() - 1);
            urlProps.setProperty("PGPORT", ports.toString());
            urlProps.setProperty("PGHOST", hosts.toString());
        } else {
            urlProps.setProperty("PGPORT", "5432");
            urlProps.setProperty("PGHOST", "localhost");
            urlProps.setProperty("PGDBNAME", l_urlServer);
        }
        //parse the args part of the url
        String[] args = l_urlArgs.split("&");
        for (int i = 0; i < args.length; ++i)
        {
            String token = args[i];
            if (token.length() ==  0) {
                continue;
            }
            int l_pos = token.indexOf('=');
            if (l_pos == -1)
            {
                urlProps.setProperty(token, "");
            }
            else
            {
                urlProps.setProperty(token.substring(0, l_pos), token.substring(l_pos + 1));
            }
        }
        return urlProps;
    }
}
