package com.atlassian.maven.plugins.amps.product.jira;

/**
 * Mapping database type by database uri prefix and database driver Please refer to the JIRA database documentation at
 * the following URL: http://www.atlassian.com/software/jira/docs/latest/databases/index.html
 */
public enum JiraDatabaseType
{
    HSQL("hsql", true, "jdbc:hsqldb", "org.hsqldb.jdbcDriver", "org.hsqldb:hsqldb"),
    H2("h2", true, "jdbc:h2", "org.h2.Driver", "com.h2database:h2"),
    MYSQL("mysql", false, "jdbc:mysql", "com.mysql.jdbc.Driver", "mysql:mysql-connector-java"),
    POSTGRES("postgres72", true, "jdbc:postgresql", "org.postgresql.Driver", "org.postgresql:postgresql"),
//    ORACLE("oracle10g", false, "jdbc:oracle", "oracle.jdbc.OracleDriver", "com.oracle:ojdbc6"),
    ORACLE("oracle10g", false, "jdbc:oracle", "oracle.jdbc.OracleDriver", "cn.guoyukun.jdbc:oracle-ojdbc6"),
    MSSQL("mssql", true, "jdbc:sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "net.sourceforge.jtds:jtds"),
    MSSQL_JTDS("mssql", true, "jdbc:jtds", "net.sourceforge.jtds.jdbc.Driver", "net.sourceforge.jtds:jtds");

    public static JiraDatabaseType getDatabaseType(String uriPrefix, String driverClassName)
    {
        for (JiraDatabaseType databaseType : values())
        {
            if (databaseType.accept(uriPrefix) && databaseType.driverClassName.equals(driverClassName))
            {
                return databaseType;
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

    private JiraDatabaseType(final String dbType, final boolean hasSchema, final String uriPrefix, final String driverClassName, final String libArtifact)
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
