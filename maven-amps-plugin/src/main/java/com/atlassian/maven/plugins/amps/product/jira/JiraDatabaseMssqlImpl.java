package com.atlassian.maven.plugins.amps.product.jira;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.product.ImportMethod;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public class JiraDatabaseMssqlImpl extends AbstractJiraDatabase
{
    private static final String DROP_DATABASE =
              "USE [master]; \n"
            + "GO \n"
            + "IF EXISTS(SELECT * FROM SYS.DATABASES WHERE name='%s') \n"
            + "DROP DATABASE [%s];\n";
    private static final String DROP_USER =
              "USE [master]; \n"
            + "GO \n"
            + "IF EXISTS(SELECT * FROM SYS.SERVER_PRINCIPALS WHERE name = '%s') \n"
            + "DROP LOGIN %s; \n";
    private static final String CREATE_DATABASE =
              "USE [master]; \n "
            + "GO \n "
            + "CREATE DATABASE [%s]; \n";
    private static final String CREATE_USER =
              "USE [master]; \n "
            + "GO \n "
            + "CREATE LOGIN %s WITH PASSWORD = '%s'; \n";
    private static final String GRANT_PERMISSION =
              "USE [%s];\n"
            + "GO \n"
            + "CREATE USER %s FROM LOGIN %s; \n"
            + "GO \n"
            + "EXEC SP_ADDROLEMEMBER 'DB_OWNER', '%s'; \n"
            + "GO \n"
            + "ALTER LOGIN %s WITH DEFAULT_DATABASE = [%s]; \n";

    public JiraDatabaseMssqlImpl(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    protected String dropDatabase() throws MojoExecutionException
    {
        final String databaseName = getDatabaseName(getDataSource().getUrl());
        return String.format(DROP_DATABASE, databaseName, databaseName);
    }

    @Override
    protected String dropUser()
    {
        final String username = getDataSource().getUsername();
        return String.format(DROP_USER, username, username );
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
        return String.format(GRANT_PERMISSION, getDatabaseName(getDataSource().getUrl()), getDataSource().getUsername());
    }

    @Override
    public Xpp3Dom getConfigDatabaseTool() throws MojoExecutionException
    {
        Xpp3Dom configDatabaseTool = null;
        if (ImportMethod.SQLCMD.toString().equals(getDataSource().getImportMethod()))
        {
            final String databaseName = getDatabaseName(getDataSource().getUrl());
            configDatabaseTool = configuration(
                    element(name("executable"), "sqlcmd"),
                    element(name("arguments"),
                            element(name("argument"), "-s localhost"),
                            element(name("argument"), "-Q \"RESTORE DATABASE "
                                    + "[" + databaseName + "] FROM DISK='" + getDataSource().getDumpFilePath() + "' "
                                    + " WITH MOVE '" + databaseName + "' TO 'C:\\" + databaseName + ".mdf', "
                                    + " WITH MOVE '" + databaseName + "_log' TO 'C:\\" + databaseName + "_log.ldf' \" ")
                    )
            );
        }
        return configDatabaseTool;
    }

    /**
     * reference jtds documentation url http://jtds.sourceforge.net/faq.html The URL format for jTDS is:
     * jdbc:jtds:<server_type>://<server>[:<port>][/<database>][;<property>=<value>[;...]]
     * @param url
     * @return database name
     */
    @Override
    protected String getDatabaseName(String url) throws MojoExecutionException
    {
        try
        {
            Class.forName(getDataSource().getDriver());
        }
        catch (ClassNotFoundException e)
        {
            throw new MojoExecutionException("Could not load JTDS MSSQL database library to classpath");
        }
        try
        {
            Driver driver = DriverManager.getDriver(url);
            DriverPropertyInfo[] driverPropertyInfos = driver.getPropertyInfo(url, null);
            if (null != driverPropertyInfos)
            {
                for(DriverPropertyInfo driverPropertyInfo : driverPropertyInfos)
                {
                    if ("DATABASENAME".equals(driverPropertyInfo.name))
                    {
                        return driverPropertyInfo.value;
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new MojoExecutionException("Could not detect database name from url: " + url);
        }
        return null;
    }

    @Override
    public Xpp3Dom getPluginConfiguration() throws MojoExecutionException
    {
        String sql = dropDatabase() + dropUser() + createDatabase() + createUser() + grantPermissionForUser();
        System.out.println(":::: MSSQL : " + sql);
        Xpp3Dom pluginConfiguration = systemDatabaseConfiguration();
        pluginConfiguration.addChild(
                element(name("sqlCommand"), sql).toDom()
        );
        return pluginConfiguration;
    }
}
