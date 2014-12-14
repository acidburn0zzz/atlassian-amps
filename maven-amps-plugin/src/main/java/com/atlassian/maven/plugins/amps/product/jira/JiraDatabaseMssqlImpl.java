package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

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
     * reference jtds documentation url http://jtds.sourceforge.net/faq.html The URL format for jTDS is:
     * jdbc:jtds:<server_type>://<server>[:<port>][/<database>][;<property>=<value>[;...]]
     * @param url
     * @return database name
     */
    @Override
    protected String getDatabaseName(String url)
    {
        String databaseName = "";
        databaseName = url.substring(url.lastIndexOf("/") + 1);
        if (databaseName.contains(";"))
        {
            databaseName = databaseName.substring(0, databaseName.indexOf(";"));
        }
        return databaseName;
    }

    @Override
    public Xpp3Dom getPluginConfiguration()
    {
        String sql = dropDatabase() + dropUser() + createDatabase() + createUser() + grantPermissionForUser();
        Xpp3Dom pluginConfiguration = baseConfiguration();
        pluginConfiguration.addChild(
                element(name("sqlCommand"), sql).toDom()
        );
        return pluginConfiguration;
    }
}
