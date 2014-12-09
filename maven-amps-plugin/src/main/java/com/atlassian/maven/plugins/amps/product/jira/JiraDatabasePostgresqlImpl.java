package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.LibArtifact;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public class JiraDatabasePostgresqlImpl extends AbstractJiraDatabase
{
    private static final String DROP_DATABASE           = "DROP DATABASE IF EXISTS \"%s\";";
    private static final String DROP_USER               = "DROP USER IF EXISTS \"%s\";";
    private static final String CREATE_DATABASE         = "CREATE DATABASE \"%s\";";
    private static final String CREATE_USER             = "CREATE ROLE \"%s\" WITH PASSWORD '%s';";
    private static final String GRANT_PERMISSION        = "ALTER DATABASE \"%s\" OWNER TO \"%s\";";

    public JiraDatabasePostgresqlImpl(DataSource dataSource)
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
        return String.format(DROP_USER, getDataSource().getUsername());
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
     * reference postgres 9.3 documentation: Connecting to the Database
     * http://jdbc.postgresql.org/documentation/93/connect.html
     * With JDBC, a database is represented by a URL (Uniform Resource Locator) takes one of the following forms:
     * jdbc:postgresql:database
     * jdbc:postgresql://host/database
     * jdbc:postgresql://host:port/database
     * @return
     */
    @Override
    protected String getDatabaseName(String url)
    {
        String databaseName = "";
        if (!url.contains("/"))
        {
            databaseName = url.substring(url.lastIndexOf(":") + 1);
        }
        else
        {
            databaseName = url.substring(url.lastIndexOf("/") + 1);
        }
        return databaseName;
    }

    @Override
    public Xpp3Dom getPluginConfiguration()
    {
        String sql = dropDatabase() + dropUser() + createDatabase() + createUser() + grantPermissionForUser();
        System.out.println("::::: sql  : " + sql );
        Xpp3Dom pluginConfiguration = baseConfiguration();
        pluginConfiguration.addChild(
                element(name("sqlCommand"), sql).toDom()
        );
        return pluginConfiguration;
    }



}
