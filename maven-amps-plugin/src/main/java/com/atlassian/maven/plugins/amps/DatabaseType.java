package com.atlassian.maven.plugins.amps;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *  Mapping database type by database uri prefix and database driver
 *  Please refer to the JIRA database documentation at the following URL: http://www.atlassian.com/software/jira/docs/latest/databases/index.html
 */
public enum DatabaseType
{
    HSQL("hsql", "jdbc:hsqldb", "org.hsqldb.jdbcDriver"),
    MYSQL("mysql", "jdbc:mysql", "com.mysql.jdbc.Driver"),
    POSTGRESQL("postgres72", "jdbc:postgresql", "org.postgresql.Driver"),
    ORACLE("oracle10", "jdbc:oracle", "oracle.jdbc.OracleDriver"),
    MSSQL("mssql", "jdbc:sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    MSSQL_JTDS("mssql", "jdbc:jtds:sqlserver", "net.sourceforge.jtds.jdbc.Driver");

    private final String dbType;
    private final String uriPrefix;
    private final String driverClassName;

    DatabaseType(String dbType, String uriPrefix, String driverClassName){
        this.dbType = dbType;
        this.uriPrefix = uriPrefix;
        this.driverClassName = driverClassName;
    }

    /**
     * Checks whether the URI starts with the prefix associated with the database
     *
     * @param uri the give URI for connecting to the database
     * @return {@code true} if the URI is valid for this instance of data source factory
     */
    private boolean accept(String uri)
    {
        return checkNotNull(uri).trim().startsWith(uriPrefix);
    }

    public static DatabaseType produceDatabaseType(String uriPrefix, String driverClassName)
    {
        for (DatabaseType databaseType : values())
        {
            if(databaseType.accept(uriPrefix) && databaseType.driverClassName.equals(driverClassName))
            {
                return databaseType;
            }
        }
        return null;
    }

    /**
     * get database type from database url and driver
     * @param url
     * @param driver
     * @return database type
     */
    public static String getDatabaseType(String url, String driver)
    {
        DatabaseType databaseType = produceDatabaseType(url, driver);
        return databaseType == null ? "" : databaseType.dbType;
    }

    public String getDbType()
    {
        return this.dbType;
    }
}
