package com.atlassian.maven.plugins.amps;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

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
}
