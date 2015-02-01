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

public class JiraDatabaseMssqlImpl extends AbstractJiraDatabase
{
    private static final String DROP_DATABASE = "DROP DATABASE %s;";
    private static final String DROP_USER = ";";
    private static final String CREATE_DATABASE = "CREATE DATABASE %s;";
    private static final String CREATE_USER = "";
    private static final String GRANT_PERMISSION = "";

    public JiraDatabaseMssqlImpl(DataSource dataSource)
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
