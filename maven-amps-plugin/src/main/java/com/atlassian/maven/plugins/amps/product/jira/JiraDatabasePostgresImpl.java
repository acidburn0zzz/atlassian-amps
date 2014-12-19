package com.atlassian.maven.plugins.amps.product.jira;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;

import com.atlassian.maven.plugins.amps.DataSource;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public class JiraDatabasePostgresImpl extends AbstractJiraDatabase
{
    private static final String DROP_DATABASE = "DROP DATABASE IF EXISTS \"%s\";";
    private static final String DROP_USER = "DROP USER IF EXISTS \"%s\";";
    private static final String CREATE_DATABASE = "CREATE DATABASE \"%s\";";
    private static final String CREATE_USER = "CREATE USER \"%s\" WITH PASSWORD '%s' ;";
    private static final String GRANT_PERMISSION = "GRANT ALL PRIVILEGES ON DATABASE \"%s\" TO \"%s\";";

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
        return String.format(GRANT_PERMISSION, getDatabaseName(getDataSource().getUrl()), getDataSource().getUsername());
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
                        return driverPropertyInfo.value;
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new MojoExecutionException("");
        }
        return null;
    }

    @Override
    public Xpp3Dom getPluginConfiguration() throws MojoExecutionException
    {
        String sql = dropDatabase() + dropUser() + createDatabase() + createUser() + grantPermissionForUser();
        Xpp3Dom pluginConfiguration = baseConfiguration();
        pluginConfiguration.addChild(
                element(name("sqlCommand"), sql).toDom()
        );
        return pluginConfiguration;
    }
}
