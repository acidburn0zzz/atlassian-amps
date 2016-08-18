package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import com.sun.xml.txw2.IllegalSignatureException;

import javax.annotation.Nonnull;
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
     * @throws IllegalStateException if the database can't be obtained
     */
    @Nonnull
    public JiraDatabase getJiraDatabase(final DataSource dataSource)
    {
        return getDatabaseType(dataSource)
                .map(dbType -> dbType.getJiraDatabase(dataSource))
                .orElseThrow(() -> new IllegalSignatureException("No DB type for " + dataSource));
    }
}
