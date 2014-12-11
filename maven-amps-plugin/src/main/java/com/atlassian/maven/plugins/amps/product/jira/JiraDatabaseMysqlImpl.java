package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public class JiraDatabaseMysqlImpl extends AbstractJiraDatabase
{

    private static final String DROP_DATABASE = "DROP DATABASE IF EXISTS `%s`;\n";
    private static final String DROP_USER = "DROP USER `%s`@localhost;\n";
    private static final String CREATE_DATABASE = "CREATE DATABASE `%s` CHARACTER SET utf8 COLLATE utf8_bin;\n";
    private static final String CREATE_USER = "CREATE USER `%s`@localhost IDENTIFIED BY '%s';\n";
    private static final String GRANT_PERMISSION = "GRANT ALL ON `%s`.* TO `%s`@localhost;\n";

    public JiraDatabaseMysqlImpl(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    protected String dropDatabase()
    {
        return String.format(DROP_DATABASE, getDatabaseName(getDataSource().getUrl()));
    }

    @Override
    protected String dropUser()
    {
        return String.format(DROP_USER, getDataSource().getUsername(), getDataSource().getUsername());
    }

    @Override
    protected String createDatabase()
    {
        return String.format(CREATE_DATABASE, getDatabaseName(getDataSource().getUrl()));
    }

    @Override
    protected String createUser()
    {
        return String.format(CREATE_USER, getDataSource().getUsername(), getDataSource().getPassword());
    }

    @Override
    protected String grantPermissionForUser()
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
    protected String getDatabaseName(final String url)
    {
        String databaseName;
        databaseName = url.substring(url.lastIndexOf("/") + 1);
        if (databaseName.contains("?"))
        {
            databaseName = databaseName.substring(0, databaseName.indexOf("?"));
        }
        return databaseName;
    }

    @Override
    public Xpp3Dom getPluginConfiguration()
    {
        String sql = dropDatabase() + dropUser() + createDatabase() + createUser() + grantPermissionForUser();
        System.out.println(":: sql : " + sql);
        Xpp3Dom pluginConfiguration = baseConfiguration();
        pluginConfiguration.addChild(
                element(name("sqlCommand"), sql).toDom()
        );
        return pluginConfiguration;
    }
}
