package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import org.codehaus.plexus.util.xml.Xpp3Dom;

public class JiraDatabaseMysqlImpl extends AbstractJiraDatabase
{

    public JiraDatabaseMysqlImpl(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    protected String dropDatabase()
    {
        return null;
    }

    @Override
    protected String createDatabase()
    {
        return null;
    }

    @Override
    protected String dropUser()
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
        return null;
    }

    @Override
    public Xpp3Dom getPluginConfiguration()
    {
        return null;
    }
}
