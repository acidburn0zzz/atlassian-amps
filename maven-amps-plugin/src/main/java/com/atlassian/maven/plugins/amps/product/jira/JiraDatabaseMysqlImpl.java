package com.atlassian.maven.plugins.amps.product.jira;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;

import com.atlassian.maven.plugins.amps.DataSource;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public class JiraDatabaseMysqlImpl extends AbstractJiraDatabase
{

    private static final Logger LOG = LoggerFactory.getLogger(JiraDatabaseMysqlImpl.class);
    private static final String DROP_DATABASE = "DROP DATABASE IF EXISTS `%s`;\n";
    private static final String DROP_USER = "GRANT USAGE ON *.* TO `%s`@localhost;\nDROP USER `%s`@localhost;\n";
    private static final String CREATE_DATABASE = "CREATE DATABASE `%s` CHARACTER SET utf8 COLLATE utf8_bin;\n";
    private static final String CREATE_USER = "CREATE USER `%s`@localhost IDENTIFIED BY '%s';\n";
    private static final String GRANT_PERMISSION = "GRANT ALL ON `%s`.* TO `%s`@localhost;\n";

    public JiraDatabaseMysqlImpl(DataSource dataSource)
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
        return String.format(DROP_USER, getDataSource().getUsername(), getDataSource().getUsername());
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
     * Reference mysql 5.1 documentation 5.1 Driver/Datasource Class Names, URL Syntax and Configuration Properties for
     * Connector/J http://dev.mysql.com/doc/connector-j/en/connector-j-reference-configuration-properties.html sample
     * connection URL: jdbc:mysql://localhost:3306/sakila?profileSQL=true
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
            throw new MojoExecutionException("Could not load Mysql database library to classpath");
        }
        try
        {
            Driver driver = DriverManager.getDriver(url);
            DriverPropertyInfo[] driverPropertyInfos = driver.getPropertyInfo(url, null);
            if (null != driverPropertyInfos)
            {
                for(DriverPropertyInfo driverPropertyInfo : driverPropertyInfos)
                {
                    if ("DBNAME".equals(driverPropertyInfo.name))
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
        LOG.debug("MYSQL initialization database sql: " + sql);
        Xpp3Dom pluginConfiguration = systemDatabaseConfiguration();
        pluginConfiguration.addChild(
                element(name("sqlCommand"), sql).toDom()
        );
        return pluginConfiguration;
    }
}
