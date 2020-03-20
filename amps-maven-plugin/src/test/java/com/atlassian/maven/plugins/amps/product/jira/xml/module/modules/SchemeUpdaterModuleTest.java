package com.atlassian.maven.plugins.amps.product.jira.xml.module.modules;

import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SchemeUpdaterModuleTest {
    Document document;

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


}
