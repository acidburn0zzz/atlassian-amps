package com.atlassian.maven.plugins.amps;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.maven.plugins.amps.product.JiraProductHandler;

@RunWith(MockitoJUnitRunner.class)
public class TestDataSource
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
        ds.setDefaultValues("a", "b", "c", "d", "e", "f", "g", "h");
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
        ds.setDefaultValues("a", "b", "c", "d", "e", "f", "g", "h");
        
        assertEquals("cargo.datasource.url=b" +
        		"|cargo.datasource.driver=c" +
        		"|cargo.datasource.username=d" +
        		"|cargo.datasource.password=e" +
        		"|cargo.datasource.jndi=a" +
        		"|cargo.datasource.type=f" +
        		"|cargo.datasource.transactionsupport=g" +
        		"|cargo.datasource.properties=h", ds.getCargoString());
    }

    @Mock MavenContext context;
    @Mock MavenProject project;
    @Mock Build build;
    
    @Mock JiraProductHandler jph;
    
    @Test
    public void jiraDefaultString()
    {
        Mockito.when(context.getProject()).thenReturn(project);
        Mockito.when(project.getBuild()).thenReturn(build);
        Mockito.when(build.getDirectory()).thenReturn("");
        
        Product jiraProduct = new Product();
        jiraProduct.setDataSource(new DataSource());
        jiraProduct.setInstanceId("");
        Map<String, String> systemProperties = new JiraProductHandler(context, null).getSystemProperties(jiraProduct);
        //Map<String, String> systemProperties = jph.getSystemProperties(jiraProduct);
        String cargoString = systemProperties.get("cargo.datasource.datasource");
        assertEquals("cargo.datasource.url=jdbc:hsqldb:/home/database" +
        		"|cargo.datasource.driver=org.hsqldb.jdbcDriver" +
        		"|cargo.datasource.username=sa" +
        		"|cargo.datasource.password=" +
        		"|cargo.datasource.jndi=jdbc/JiraDS" +
        		"|cargo.datasource.type=javax.sql.DataSource",
                cargoString);

    }
}
