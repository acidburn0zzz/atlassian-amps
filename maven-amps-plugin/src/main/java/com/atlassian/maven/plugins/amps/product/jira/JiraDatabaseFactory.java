package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import org.apache.maven.plugin.MojoExecutionException;

import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.getDatabaseType;

public final class JiraDatabaseFactory
{
    private static JiraDatabaseFactory instance = null;

    private JiraDatabaseFactory()
    {
    }

    public static synchronized JiraDatabaseFactory getJiraDatabaseFactory()
    {
        if (null == instance)
        {
            instance = new JiraDatabaseFactory();
        }
        return instance;
    }

    public JiraDatabase getJiraDatabase(DataSource dataSource) throws MojoExecutionException
    {
        final JiraDatabaseType databaseType = getDatabaseType(dataSource.getUrl(), dataSource.getDriver());
        final JiraDatabase jiraDatabase;
        if (null == databaseType)
        {
            return null;
        }
        switch (databaseType)
        {
            case POSTGRES:
                jiraDatabase = new JiraDatabasePostgresImpl(dataSource);
                break;
            case MYSQL:
                jiraDatabase = new JiraDatabaseMysqlImpl(dataSource);
                break;
            case ORACLE:
                jiraDatabase = new JiraDatabaseOracleImpl(dataSource);
                break;
            case MSSQL:
            case MSSQL_JTDS:
                jiraDatabase = new JiraDatabaseMssqlImpl(dataSource);
                break;
            default:
                jiraDatabase = null;
        }
        return jiraDatabase;
    }
}
