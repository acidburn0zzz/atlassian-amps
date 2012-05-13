package com.atlassian.maven.plugins.amps;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

/**
 * Definition of a datasource.
 * For more information about the properties, see http://cargo.codehaus.org/DataSource+and+Resource+Support
 * @since 3.11
 */
public class DataSource
{

    /**
     * Connection url, such as "jdbc:hsqldb:/path/to/database"
     */
    private String url;

    /**
     * Driver, such as "org.hsqldb.jdbcDriver"
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

    public DataSource()
    {
        // Default constructor
    }

    public String getCargoString()
    {
        if (cargoString != null)
            return cargoString;
        
        List<String> cargoProperties = Lists.newArrayList();
        cargoProperties.add("cargo.datasource.url=" + url);
        cargoProperties.add("cargo.datasource.driver=" + driver);
        cargoProperties.add("cargo.datasource.username=" + username);
        cargoProperties.add("cargo.datasource.password=" + password);
        cargoProperties.add("cargo.datasource.jndi=" + jndi);
        if (!StringUtils.isBlank(type))
            cargoProperties.add("cargo.datasource.type=" + type);
        if (!StringUtils.isBlank(transactionSupport))
            cargoProperties.add("cargo.datasource.transactionsupport=" + transactionSupport);
        if (!StringUtils.isBlank(properties))
            cargoProperties.add("cargo.datasource.properties=" + properties);
        
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

    @Override
    public String toString()
    {
        return "DataSource [url=" + url + ", driver=" + driver + ", username=" + username + ", password=" + password + ", jndi=" + jndi + ", type=" + type
                + ", transactionSupport=" + transactionSupport + ", properties=" + properties + ", cargoString=" + cargoString + ", libArtifacts="
                + libArtifacts + "]";
    }

    
}
