package com.atlassian.maven.plugins.amps.product.jira.module.modules.xml;

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

public class SchemeUpdaterTransformerTest {
    private final String PUBLIC_SCHEMA = "PUBLIC";

    private Document document;

    @Before
    public void setUp() {
        DocumentFactory documentFactory = DocumentFactory.getInstance();
        document = documentFactory.createDocument(documentFactory.createElement("jira-database-config"));
    }

    @Test
    public void shouldRemoveSchemaWhenPresentSchemaAndDatabaseDoesNotSupportSchema() throws MojoExecutionException {
        document.getRootElement().addElement("schema-name").setText("PUBLIC");
        SchemeUpdaterTransformer schemeUpdaterTransformer = new SchemeUpdaterTransformer(JiraDatabaseType.ORACLE_12C, null);

        assertTrue(schemeUpdaterTransformer.transform(document));

        assertNull(document.selectSingleNode("//jira-database-config/schema-name"));
    }

    @Test
    public void shouldNotCreateSchemaWhenMissingSchemaAndDatabaseDoesNotSupportSchema() throws MojoExecutionException {
        SchemeUpdaterTransformer schemeUpdaterTransformer = new SchemeUpdaterTransformer(JiraDatabaseType.ORACLE_12C, null);

        assertFalse(schemeUpdaterTransformer.transform(document));

        assertNull(document.selectSingleNode("//jira-database-config/schema-name"));
    }

    @Test
    public void shouldCreateSchemaWhenMissingAndDatabaseSupportsSchema() throws MojoExecutionException {
        SchemeUpdaterTransformer schemeUpdaterTransformer = new SchemeUpdaterTransformer(JiraDatabaseType.MSSQL, PUBLIC_SCHEMA);

        assertTrue(schemeUpdaterTransformer.transform(document));

        assertNotNull(document.selectSingleNode("//jira-database-config/schema-name"));
        assertEquals(document.selectSingleNode("//jira-database-config/schema-name").getText(), PUBLIC_SCHEMA);
    }

    @Test
    public void shouldChangeSchemaWhenDifferentSchemaAndDatabaseSupportsSchema() throws MojoExecutionException {
        document.getRootElement().addElement("schema-name").setText("PRIVATE");
        SchemeUpdaterTransformer schemeUpdaterTransformer = new SchemeUpdaterTransformer(JiraDatabaseType.MSSQL, PUBLIC_SCHEMA);

        assertTrue(schemeUpdaterTransformer.transform(document));

        assertNotNull(document.selectSingleNode("//jira-database-config/schema-name"));
        assertEquals(document.selectSingleNode("//jira-database-config/schema-name").getText(), PUBLIC_SCHEMA);
    }

    @Test
    public void shouldNotChangeSchemaWhenSameSchemaAndDatabaseSupportsSchema() throws MojoExecutionException {
        document.getRootElement().addElement("schema-name").setText(PUBLIC_SCHEMA);
        SchemeUpdaterTransformer schemeUpdaterTransformer = new SchemeUpdaterTransformer(JiraDatabaseType.MSSQL, PUBLIC_SCHEMA);

        assertFalse(schemeUpdaterTransformer.transform(document));

        assertNotNull(document.selectSingleNode("//jira-database-config/schema-name"));
        assertEquals(document.selectSingleNode("//jira-database-config/schema-name").getText(), PUBLIC_SCHEMA);
    }


}
