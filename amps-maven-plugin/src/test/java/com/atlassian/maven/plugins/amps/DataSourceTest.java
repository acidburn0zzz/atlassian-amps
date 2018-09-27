package com.atlassian.maven.plugins.amps;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DataSourceTest
{
    DataSource ds;
    
    @Before
    public void setup()
    {
        ds = new DataSource();
        
        ds.setDriver("aDriver");
        ds.setJndi("aJndi");
        ds.setPassword("aPassword");
        ds.setProperties("aProperties");
        ds.setTransactionSupport("aTransactionSupport");
        ds.setType("aType");
        ds.setUrl("aUrl");
        ds.setUsername("aUsername");
    }
    
    @Test
    public void testCreateCargoString()
    {
        assertEquals("cargo.datasource.url=aUrl" +
                "|cargo.datasource.driver=aDriver" +
                "|cargo.datasource.username=aUsername" +
                "|cargo.datasource.password=aPassword" +
                "|cargo.datasource.jndi=aJndi" +
                "|cargo.datasource.type=aType" +
                "|cargo.datasource.transactionsupport=aTransactionSupport" +
                "|cargo.datasource.properties=aProperties", ds.getCargoString());
    }
    
    @Test
    public void cargoStringOverridesProperties()
    {   
        ds.setCargoString("Obama");
        
        assertEquals("Obama", ds.getCargoString());
    }
    
    @Test
    public void defaultDoesntOverrideProperties()
    {
        DataSource defaultValues = new DataSource();
        defaultValues.setUrl("x");
        defaultValues.setDriver("x");
        defaultValues.setUsername("x");
        defaultValues.setPassword("x");
        defaultValues.setJndi("x");
        defaultValues.setType("x");
        defaultValues.setTransactionSupport("x");
        defaultValues.setProperties("x");
        
        ds.useForUnsetValues(defaultValues);
        
        assertEquals("cargo.datasource.url=aUrl" +
                "|cargo.datasource.driver=aDriver" +
                "|cargo.datasource.username=aUsername" +
                "|cargo.datasource.password=aPassword" +
                "|cargo.datasource.jndi=aJndi" +
                "|cargo.datasource.type=aType" +
                "|cargo.datasource.transactionsupport=aTransactionSupport" +
                "|cargo.datasource.properties=aProperties", ds.getCargoString());
    }
    
    @Test
    public void defaultSetsProperties()
    {
        ds = new DataSource();

        DataSource defaultValues = new DataSource();
        defaultValues.setJndi("a");
        defaultValues.setUrl("b");
        defaultValues.setDriver("c");
        defaultValues.setUsername("d");
        defaultValues.setPassword("e");
        defaultValues.setType("f");
        defaultValues.setTransactionSupport("g");
        defaultValues.setProperties("h");
        
        ds.useForUnsetValues(defaultValues);
        
        assertEquals("cargo.datasource.url=b" +
                "|cargo.datasource.driver=c" +
                "|cargo.datasource.username=d" +
                "|cargo.datasource.password=e" +
                "|cargo.datasource.jndi=a" +
                "|cargo.datasource.type=f" +
                "|cargo.datasource.transactionsupport=g" +
                "|cargo.datasource.properties=h", ds.getCargoString());
    }

    @Test
    public void toStringShouldNotIncludeAnyPasswords() {
        // Set up
        final String password = "verboten!";
        ds.setPassword(password);
        ds.setSystemPassword(password);

        // Invoke
        final String toString = ds.toString();

        // Check
        assertThat(toString, not(containsString(password)));
    }

    @Test
    public void shouldAddFirstProperty() {
        // Set up
        ds = new DataSource();

        // Invoke
        ds.addProperty("foo", "bar");

        // Check
        assertThat(ds.getProperties(), is("foo=bar"));
    }

    @Test
    public void shouldAddSecondProperty() {
        // Set up
        ds = new DataSource();
        ds.setProperties("p1=v1");

        // Invoke
        ds.addProperty("p2", "v2");

        // Check
        assertThat(ds.getProperties(), is("p1=v1;p2=v2"));
    }

    @Test
    public void sqlPluginJdbcDriverPropertiesShouldDefaultToEmptyString() {
        // Set up
        ds = new DataSource();

        // Invoke
        final String driverProperties = ds.getSqlPluginJdbcDriverProperties();

        // Check
        assertThat(driverProperties, isEmptyString());
    }

    @Test
    public void sqlPluginJdbcDriverPropertiesShouldUseCorrectDelimiter() {
        // Set up
        ds = new DataSource();
        ds.addProperty("p1", "v1");
        ds.addProperty("p2", "v2");

        // Invoke
        final String driverProperties = ds.getSqlPluginJdbcDriverProperties();

        // Check
        assertThat(driverProperties, is("p1=v1,p2=v2"));
    }
}
