package com.atlassian.maven.plugins.amps.product.jira;

import javax.annotation.Nullable;

/**
 * Mapping database type by database uri prefix and database driver Please refer to the JIRA database documentation at
 * the following URL: https://confluence.atlassian.com/display/AdminJIRAServer071/Supported+platforms
 */
public enum JiraDatabaseType {

    HSQL("hsql", true, "jdbc:hsqldb", "org.hsqldb.jdbcDriver", "org.hsqldb:hsqldb"),
    H2("h2", true, "jdbc:h2", "org.h2.Driver", "com.h2database:h2"),
    MYSQL("mysql", false, "jdbc:mysql", "com.mysql.jdbc.Driver", "mysql:mysql-connector-java"),
    POSTGRES("postgres72", true, "jdbc:postgresql", "org.postgresql.Driver", "org.postgresql:postgresql"),
    ORACLE_10G("oracle10g", false, "jdbc:oracle", "oracle.jdbc.OracleDriver", "com.oracle:ojdbc6"),
    MSSQL("mssql", true, "jdbc:sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "net.sourceforge.jtds:jtds"),
    MSSQL_JTDS("mssql", true, "jdbc:jtds", "net.sourceforge.jtds.jdbc.Driver", "net.sourceforge.jtds:jtds");

    /**
     * Returns the {@link JiraDatabaseType} with the given JDBC URL prefix and driver class.
     *
     * @param uriPrefix the URL prefix to match upon
     * @param driverClassName the driver class to match upon
     * @return null if there is no such enum value
     */
    @Nullable
    public static JiraDatabaseType getDatabaseType(final String uriPrefix, final String driverClassName)
    {
        for (final JiraDatabaseType dbType : values())
        {
            if (dbType.accept(uriPrefix) && dbType.driverClassName.equals(driverClassName))
            {
                return dbType;
            }
        }
        return null;
    }

    private final String dbType;
    // if database does not have schema, schema-name node will be removed
    private final boolean hasSchema;
    private final String uriPrefix;
    private final String driverClassName;
    private final String libArtifact;

    JiraDatabaseType(final String dbType, final boolean hasSchema, final String uriPrefix, final String driverClassName, final String libArtifact)
    {
        this.dbType = dbType;
        this.hasSchema = hasSchema;
        this.uriPrefix = uriPrefix;
        this.driverClassName = driverClassName;
        this.libArtifact = libArtifact;
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

    @Override
    public String toString()
    {
        return super.toString();
    }
}
