package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public class JiraDatabaseOracleImpl extends AbstractJiraDatabase
{

    private static final String DROP_DATABASE = "";
    private static final String DROP_USER = "DROP USER %s CASCADE;\n";
    private static final String CREATE_DATABASE = "";
    private static final String CREATE_USER = "GRANT CONNECT, RESOURCE TO %s IDENTIFIED BY %s;\n";
    private static final String GRANT_PERMISSION = "";


    public JiraDatabaseOracleImpl(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    protected String dropDatabase()
    {
        return DROP_DATABASE;
    }

    @Override
    protected String dropUser()
    {
        return String.format(DROP_USER, getDataSource().getUsername());
    }

    @Override
    protected String createDatabase()
    {
        return CREATE_DATABASE;
    }

    @Override
    protected String createUser()
    {
        return String.format(CREATE_USER, getDataSource().getUsername(), getDataSource().getPassword());
    }

    @Override
    protected String grantPermissionForUser()
    {
        return GRANT_PERMISSION;
    }

    @Override
    protected String getDatabaseName(String url)
    {
        return getDataSource().getSchema();
    }


    @Override
    public Xpp3Dom getPluginConfiguration()
    {
        String sql = dropDatabase() + dropUser() + createDatabase() + createUser() + grantPermissionForUser();
        Xpp3Dom pluginConfiguration = systemDatabaseConfiguration();
        pluginConfiguration.addChild(
                element(name("sqlCommand"), sql).toDom()
        );
        return pluginConfiguration;
    }
}
