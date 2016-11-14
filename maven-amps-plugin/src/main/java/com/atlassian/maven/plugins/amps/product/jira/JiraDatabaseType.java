package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.DatabaseMetaData;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

/**
 * Mapping database type by database uri prefix and database driver Please refer to the JIRA database documentation at
 * the following URL: https://confluence.atlassian.com/display/AdminJIRAServer071/Supported+platforms
 */
public enum JiraDatabaseType {

    HSQL("hsql", true, "jdbc:hsqldb", "org.hsqldb.jdbcDriver", ds -> null),

    H2("h2", true, "jdbc:h2", "org.h2.Driver", ds -> null),

    MYSQL("mysql", false, "jdbc:mysql", "com.mysql.jdbc.Driver", JiraDatabaseMysqlImpl::new),

    POSTGRES("postgres72", true, "jdbc:postgresql", "org.postgresql.Driver", JiraDatabasePostgresImpl::new),

    // Has to appear before 10g as it is more stringent
    ORACLE_12C("oracle12c", false, "jdbc:oracle", "oracle.jdbc.OracleDriver", JiraDatabaseOracle12cImpl::new) {

        @Override
        boolean matches(final DataSource dataSource) {
            return super.matches(dataSource) &&
                    JiraDatabaseType.dbVersionMatches(
                            dataSource, version -> version.startsWith(ORACLE_12C_VERSION_PREFIX));
        }
    },

    ORACLE_10G("oracle10g", false, "jdbc:oracle", "oracle.jdbc.OracleDriver", JiraDatabaseOracle10gImpl::new),

    MSSQL("mssql", true, "jdbc:sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", JiraDatabaseMssqlImpl::new),

    MSSQL_JTDS("mssql", true, "jdbc:jtds", "net.sourceforge.jtds.jdbc.Driver", JiraDatabaseMssqlImpl::new);

    /**
     * The string that the database product version needs to start with
     * in order for the {@link #ORACLE_12C} db type to be detected.
     *
     * @see DatabaseMetaData#getDatabaseProductVersion
     */
    public static final String ORACLE_12C_VERSION_PREFIX = "Oracle Database 12c";

    /**
     * Indicates whether the database product version of the given data source matches the given predicate.
     *
     * @param dataSource the data source to query
     * @param predicate the predicate to apply (can assume a non-null version)
     * @return see above
     * @see DatabaseMetaData#getDatabaseProductVersion
     */
    @ParametersAreNonnullByDefault
    private static boolean dbVersionMatches(final DataSource dataSource, final Predicate<String> predicate)
    {
        return dataSource.<String>getJdbcMetaData(DatabaseMetaData::getDatabaseProductVersion)
                .filter(predicate)
                .isPresent();
    }

    /**
     * Returns the {@link JiraDatabaseType} for the given {@link DataSource}.
     *
     * @param dataSource the datasource for which to get the type
     * @return the first matching database type, if one exists
     * @since version 6.2.7
     */
    @Nonnull
    public static Optional<JiraDatabaseType> getDatabaseType(final DataSource dataSource)
    {
        return stream(values())
                .filter(dbType -> dbType.matches(dataSource))
                .findFirst();
    }

    private final String dbType;
    // if database does not have schema, schema-name node will be removed
    private final boolean hasSchema;
    private final String uriPrefix;
    private final String driverClassName;
    private final Function<DataSource, JiraDatabase> jiraDbSupplier;

    JiraDatabaseType(final String dbType, final boolean hasSchema, final String uriPrefix, final String driverClassName,
                     @Nonnull final Function<DataSource, JiraDatabase> jiraDbSupplier)
    {
        this.dbType = dbType;
        this.hasSchema = hasSchema;
        this.uriPrefix = uriPrefix;
        this.driverClassName = driverClassName;
        this.jiraDbSupplier = requireNonNull(jiraDbSupplier);
    }

    /**
     * Indicates whether this db type matches the given data source.
     * This implementation checks the URL prefix and the driver class.
     *
     * @param dataSource the data source to check against
     * @return see above
     */
    boolean matches(final DataSource dataSource) {
        return startsWithExpectedPrefix(dataSource.getUrl()) && driverClassName.equals(dataSource.getDriver());
    }

    /**
     * Checks whether the given URI starts with the expected prefix for this type of database
     *
     * @param uri the give URI for connecting to the database
     * @return {@code true} if the URI is valid for this instance of data source factory
     */
    private boolean startsWithExpectedPrefix(final String uri)
    {
        return uri != null && uri.trim().startsWith(uriPrefix);
    }

    public String getDbType()
    {
        return this.dbType;
    }

    public boolean hasSchema()
    {
        return this.hasSchema;
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
