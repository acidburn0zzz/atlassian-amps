package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import javax.annotation.Nullable;

import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.getDatabaseType;

public final class JiraDatabaseFactory
{
    private static JiraDatabaseFactory instance;

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

    /**
     * Returns the {@link JiraDatabase} for the given {@link DataSource}.
     *
     * @param dataSource the data source
     * @return null if the database cannot be obtained
     */
    @Nullable
    public JiraDatabase getJiraDatabase(final DataSource dataSource)
    {
        return getDatabaseType(dataSource.getUrl(), dataSource.getDriver())
                .map(dbType -> dbType.getJiraDatabase(dataSource))
                .orElse(null);
    }
}
