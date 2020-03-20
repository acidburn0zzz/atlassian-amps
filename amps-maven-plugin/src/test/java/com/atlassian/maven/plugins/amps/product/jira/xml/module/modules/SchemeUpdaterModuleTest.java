package com.atlassian.maven.plugins.amps.product.jira.xml.module.modules;

import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SchemeUpdaterModuleTest {
    private final String PUBLIC_SCHEMA = "PUBLIC";

    private Document document;

    @Before
    public void setUp() {
        DocumentFactory documentFactory = DocumentFactory.getInstance();
        document = documentFactory.createDocument(documentFactory.createElement("jira-database-config"));
    }

    @Test
    public void givenExistingSchemaAndOracleWhenModuleExecutedThenSchemaIsRemoved() throws MojoExecutionException {
        document.getRootElement().addElement("schema-name").setText("PUBLIC");
        SchemeUpdaterModule schemeUpdaterModule = new SchemeUpdaterModule(JiraDatabaseType.ORACLE_12C, null);

        assertTrue(schemeUpdaterModule.transform(document));

        assertNull(document.selectSingleNode("//jira-database-config/schema-name"));
    }

    @Test
    public void givenMissingSchemaAndOracleWhenModuleExecutedThenSchemaIsUntouched() throws MojoExecutionException {
        SchemeUpdaterModule schemeUpdaterModule = new SchemeUpdaterModule(JiraDatabaseType.ORACLE_12C, null);

        assertFalse(schemeUpdaterModule.transform(document));

        assertNull(document.selectSingleNode("//jira-database-config/schema-name"));
    }

    @Test
    public void givenMissingSchemaAndMySQLWhenModuleExecutedThenSchemaIsCreated() throws MojoExecutionException {
        SchemeUpdaterModule schemeUpdaterModule = new SchemeUpdaterModule(JiraDatabaseType.MSSQL, PUBLIC_SCHEMA);

        assertTrue(schemeUpdaterModule.transform(document));

        assertNotNull(document.selectSingleNode("//jira-database-config/schema-name"));
        assertEquals(document.selectSingleNode("//jira-database-config/schema-name").getText(), PUBLIC_SCHEMA);
    }

    @Test
    public void givenDifferentSchemaAndMySQLWhenModuleExecutedThenSchemaIsChanged() throws MojoExecutionException {
        document.getRootElement().addElement("schema-name").setText("PRIVATE");
        SchemeUpdaterModule schemeUpdaterModule = new SchemeUpdaterModule(JiraDatabaseType.MSSQL, PUBLIC_SCHEMA);

        assertTrue(schemeUpdaterModule.transform(document));

        assertNotNull(document.selectSingleNode("//jira-database-config/schema-name"));
        assertEquals(document.selectSingleNode("//jira-database-config/schema-name").getText(), PUBLIC_SCHEMA);
    }

    @Test
    public void givenSameSchemaAndMySQLWhenModuleExecutedThenSchemaIsNotChanged() throws MojoExecutionException {
        document.getRootElement().addElement("schema-name").setText(PUBLIC_SCHEMA);
        SchemeUpdaterModule schemeUpdaterModule = new SchemeUpdaterModule(JiraDatabaseType.MSSQL, PUBLIC_SCHEMA);

        assertFalse(schemeUpdaterModule.transform(document));

        assertNotNull(document.selectSingleNode("//jira-database-config/schema-name"));
        assertEquals(document.selectSingleNode("//jira-database-config/schema-name").getText(), PUBLIC_SCHEMA);
    }


}
