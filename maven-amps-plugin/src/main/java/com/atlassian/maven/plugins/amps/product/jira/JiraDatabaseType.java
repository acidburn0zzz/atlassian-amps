package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

/**
 * Mapping database type by database uri prefix and database driver Please refer to the JIRA database documentation at
 * the following URL: https://confluence.atlassian.com/display/AdminJIRAServer071/Supported+platforms
 */
public enum JiraDatabaseType {

    HSQL("hsql", true, "jdbc:hsqldb", "org.hsqldb.jdbcDriver", "org.hsqldb:hsqldb", ds -> null),

    H2("h2", true, "jdbc:h2", "org.h2.Driver", "com.h2database:h2", ds -> null),

    MYSQL("mysql", false, "jdbc:mysql", "com.mysql.jdbc.Driver", "mysql:mysql-connector-java", JiraDatabaseMysqlImpl::new),

    POSTGRES("postgres72", true, "jdbc:postgresql", "org.postgresql.Driver", "org.postgresql:postgresql", JiraDatabasePostgresImpl::new),

    ORACLE_10G("oracle10g", false, "jdbc:oracle", "oracle.jdbc.OracleDriver", "com.oracle:ojdbc6", JiraDatabaseOracle10gImpl::new),

    ORACLE_12C("oracle12c", false, "jdbc:oracle", "oracle.jdbc.OracleDriver", "com.oracle:ojdbc7", JiraDatabaseOracle12cImpl::new),

    MSSQL("mssql", true, "jdbc:sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "net.sourceforge.jtds:jtds", JiraDatabaseMssqlImpl::new),

    MSSQL_JTDS("mssql", true, "jdbc:jtds", "net.sourceforge.jtds.jdbc.Driver", "net.sourceforge.jtds:jtds", JiraDatabaseMssqlImpl::new);

    /**
     * Returns the first {@link JiraDatabaseType} with the given JDBC URL prefix and driver class.
     *
     * @param uriPrefix the URL prefix to match upon
     * @param driverClassName the driver class to match upon
     * @return the matching database type, if one exists
     * @since version 6.2.7
     */
    @Nonnull
    public static Optional<JiraDatabaseType> getDatabaseType(final String uriPrefix, final String driverClassName)
    {
        return stream(values())
                .filter(dbType -> dbType.accept(uriPrefix))
                .filter(dbType -> dbType.driverClassName.equals(driverClassName))
                .findFirst();
    }

    private final String dbType;
    // if database does not have schema, schema-name node will be removed
    private final boolean hasSchema;
    private final String uriPrefix;
    private final String driverClassName;
    private final String libArtifact;
    private final Function<DataSource, JiraDatabase> jiraDbSupplier;

    JiraDatabaseType(final String dbType, final boolean hasSchema, final String uriPrefix, final String driverClassName,
                     final String libArtifact, @Nonnull final Function<DataSource, JiraDatabase> jiraDbSupplier)
    {
        this.dbType = dbType;
        this.hasSchema = hasSchema;
        this.uriPrefix = uriPrefix;
        this.driverClassName = driverClassName;
        this.libArtifact = libArtifact;
        this.jiraDbSupplier = requireNonNull(jiraDbSupplier);
    }

    /**
     * Checks whether the URI starts with the prefix associated with the database
     *
     * @param uri the give URI for connecting to the database
     * @return {@code true} if the URI is valid for this instance of data source factory
     */
    private boolean accept(String uri)
    {
        return null != uri && uri.trim().startsWith(uriPrefix);
    }

    public String getDbType()
    {
        return this.dbType;
    }

    public boolean hasSchema()
    {
        return this.hasSchema;
    }

    public String getLibArtifact()
    {
        return libArtifact;
    }

    /**
     * Returns the appropriate type of {@link JiraDatabase} for this {@link JiraDatabaseType}.
     *
     * @param dataSource the JIRA data source
     * @return see above
     */
    @Nullable
    public JiraDatabase getJiraDatabase(final DataSource dataSource) {
        return jiraDbSupplier.apply(dataSource);
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
