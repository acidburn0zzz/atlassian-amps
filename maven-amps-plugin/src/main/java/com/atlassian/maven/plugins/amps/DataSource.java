package com.atlassian.maven.plugins.amps;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

import static com.atlassian.maven.plugins.amps.util.PropertyUtils.parse;
import static com.google.common.base.Objects.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Definition of a datasource.
 * For more information about the properties, see http://cargo.codehaus.org/DataSource+and+Resource+Support
 * @since 3.11
 */
public class DataSource
{
    private static final String[] FIELDS_TO_EXCLUDE_FROM_TO_STRING = { "password", "systemPassword" };

    private static final char PROPERTY_DELIMITER = ';';

    private static final char PROPERTY_KEY_VALUE_DELIMITER = '=';

    /**
     * Connection url, such as "jdbc:h2:file:/path/to/database/file"
     */
    private String url;

    /**
     * database schema, such as "public", "dbo"
     */
    private String schema;

    /**
     * Driver, such as "org.h2.Driver"
     */
    private String driver;

    /**
     * Username, e.g. "sa"
     */
    private String username;

    /**
     * Password. May be empty.
     */
    private String password;

    /**
     * JNDI name, such as jdbc/JiraDS
     */
    private String jndi;

    /**
     * Type of driver: "java.sql.Driver" or "javax.sql.XSDataSource".
     * Will not be forwarded to Cargo if empty.
     */
    private String type;

    /**
     * Which transaction support. LOCAL_TRANSACTION or XQ_TRANSACTION.
     * Will not be forwarded to Cargo if empty.
     */
    private String transactionSupport;

    /**
     * Properties to pass to the driver. Semi-colon delimited string.
     * Will not be forwarded to Cargo if empty.
     */
    private String properties;

    /**
     * Cargo-style string to pass to Cargo in the "cargo.datasource.datasource" property.
     * If set, the other properties will not be read.
     * 
     * <p>Example:
     * cargo.datasource.username=sa|cargo.datasource.password...|...
     * </p>
     */
    private String cargoString;

    /**
     * Additional libraries required in the container (e.g. Tomcat) to support the driver.
     * Example with a random library:
     *
     * <pre>
     * {@code
     * <libArtifacts>
     *   <libArtifact>
     *      <groupId>postgres</groupId>
     *      <artifactId>postgres</artifactId>
     *      <version>9.1-901-1.jdbc4</artifactId>
     *   </libArtifact>
     * </libArtifacts>
     * }
     * </pre>
     */
    private List<LibArtifact> libArtifacts = Lists.newArrayList();

    /**
     * using for drop/create schema/database
     * you can not drop your current connected database
     */
    private String defaultDatabase;

    /**
     * Database system user can drop/create schema/database/user
     */
    private String systemUsername;

    /**
     * Password of database system user. May be empty.
     */
    private String systemPassword;

    /**
     * Dump file path use for import data.
     * AMPS is using JDBC for against crossed-database so this import file have to be standard SQL
     */
    private String dumpFilePath;

    /**
     * Import method define appropriate way to make import
     * sql : AMPS will use JDBC to import SQL dump file, file must contain standard SQL
     * psql: AMPS will use Postgres psql to import SQL dump file. Eg: psql -f jira_63_postgres91_dump.sql -U jira_user jiradb
     * impdp: AMPS will use Oracle impdp to import dump file. Eg: impdp jira_user/jira_pwd directory=data_pump_dir DUMPFILE=<dumpFilePath>
     * sqlcmd: AMPS will user SQL Server sqlcmd to restore backup file. Eg: sqlcmd -s localhost -Q "RESTORE DATABASE JIRA FROM DISK='d:\jira_63_sqlserver_2008.bak'"
     */
    private String importMethod = "sql";

    public DataSource()
    {
        // Default constructor
    }

    public String getCargoString()
    {
        if (cargoString != null) {
            return cargoString;
        }

        final List<String> cargoProperties = Lists.newArrayList();
        cargoProperties.add("cargo.datasource.url=" + firstNonNull(url, ""));
        cargoProperties.add("cargo.datasource.driver=" + firstNonNull(driver, ""));
        cargoProperties.add("cargo.datasource.username=" + firstNonNull(username, ""));
        cargoProperties.add("cargo.datasource.password=" + firstNonNull(password, ""));
        cargoProperties.add("cargo.datasource.jndi=" + firstNonNull(jndi, ""));
        if (!isBlank(type)) {
            cargoProperties.add("cargo.datasource.type=" + type);
        }
        if (!isBlank(transactionSupport)) {
            cargoProperties.add("cargo.datasource.transactionsupport=" + transactionSupport);
        }
        if (!isBlank(properties)) {
            cargoProperties.add("cargo.datasource.properties=" + properties);
        }

        cargoString = StringUtils.join(cargoProperties, "|");
        return cargoString;
    }

    /**
     * @param cargoString
     *            the cargo-style string to pass to Cargo in the "cargo.datasource.datasource" property.
     */
    public DataSource(String cargoString)
    {
        this.cargoString = cargoString;
    }
    
    /**
     * Apply default values to the current bean
     * @param defaultValues a bean that default values will be read from.
     */
    public void useForUnsetValues(DataSource defaultValues)
    {
        if (this.jndi == null)                  this.jndi = defaultValues.jndi;
        if (this.url == null)                   this.url = defaultValues.url;
        if (this.schema == null)                this.schema = defaultValues.schema;
        if (this.driver == null)                this.driver = defaultValues.driver;
        if (this.username == null)              this.username = defaultValues.username;
        if (this.password == null)              this.password = defaultValues.password;
        if (this.type == null)                  this.type = defaultValues.type;
        if (this.transactionSupport == null)    this.transactionSupport = defaultValues.transactionSupport;
        if (this.properties == null)            this.properties = defaultValues.properties;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getSchema()
    {
        return schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    public String getDriver()
    {
        return driver;
    }

    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getJndi()
    {
        return jndi;
    }

    public void setJndi(String jndi)
    {
        this.jndi = jndi;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getTransactionSupport()
    {
        return transactionSupport;
    }

    public void setTransactionSupport(String transactionSupport)
    {
        this.transactionSupport = transactionSupport;
    }

    public String getProperties()
    {
        return properties;
    }

    public void setProperties(String properties)
    {
        this.properties = properties;
    }

    public List<LibArtifact> getLibArtifacts()
    {
        return libArtifacts;
    }

    public void setLibArtifacts(List<LibArtifact> libArtifacts)
    {
        this.libArtifacts = libArtifacts;
    }

    public void setCargoString(String cargoString)
    {
        this.cargoString = cargoString;
    }


    public String getDefaultDatabase()
    {
        return defaultDatabase;
    }

    public void setDefaultDatabase(String defaultDatabase)
    {
        this.defaultDatabase = defaultDatabase;
    }

    public String getSystemUsername()
    {
        return systemUsername;
    }

    public void setSystemUsername(String systemUsername)
    {
        this.systemUsername = systemUsername;
    }

    public String getSystemPassword()
    {
        return systemPassword;
    }

    public void setSystemPassword(String systemPassword)
    {
        this.systemPassword = systemPassword;
    }

    public String getDumpFilePath()
    {
        return dumpFilePath;
    }

    public void setDumpFilePath(String dumpFilePath)
    {
        this.dumpFilePath = dumpFilePath;
    }

    public String getImportMethod()
    {
        return importMethod;
    }

    public void setImportMethod(String importMethod)
    {
        this.importMethod = importMethod;
    }

    @Override
    public String toString()
    {
        // Used in error messages, etc.
        return new ReflectionToStringBuilder(this, SHORT_PREFIX_STYLE)
                .setExcludeFieldNames(FIELDS_TO_EXCLUDE_FROM_TO_STRING)
                .toString();
    }

    /**
     * Returns the JDBC data source for this instance, connecting as the configured
     * system user and applying any configured driver properties.
     *
     * @return see above
     */
    public javax.sql.DataSource getJdbcDataSource() {
        final DriverManagerDataSource dataSource =
                new DriverManagerDataSource(defaultDatabase, systemUsername, systemPassword);
        dataSource.setConnectionProperties(parse(properties, PROPERTY_KEY_VALUE_DELIMITER, PROPERTY_DELIMITER));
        return dataSource;
    }

    /**
     * Queries this data source for its JDBC metadata and applies the given callback.
     *
     * @param callback the callback to apply
     * @return the non-null return value of the callback, or none if null or there was an error
     */
    public <T> Optional<T> getJdbcMetaData(@Nonnull final DatabaseMetaDataCallback callback) {
        try {
            @SuppressWarnings("unchecked")
            final T metaData = (T) JdbcUtils.extractDatabaseMetaData(getJdbcDataSource(), callback);
            return Optional.ofNullable(metaData);
        } catch (final ClassCastException | MetaDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Adds the given JDBC driver property.
     *
     * @param name the property name
     * @param value the property value
     */
    public void addProperty(final String name, final String value) {
        final String newProperty = name + PROPERTY_KEY_VALUE_DELIMITER + value;
        if (isBlank(properties)) {
            properties = newProperty;
        } else {
            properties += PROPERTY_DELIMITER + newProperty;
        }
    }

    /**
     * Returns the JDBC driver properties in the <a href="http://www.mojohaus.org/sql-maven-plugin/execute-mojo.html#driverProperties">
     * format required by the <code>sql-maven-plugin</code></a>.
     *
     * @return see above
     */
    public String getSqlPluginJdbcDriverProperties() {
        return trimToEmpty(properties).replace(PROPERTY_DELIMITER, ',');
    }
}
