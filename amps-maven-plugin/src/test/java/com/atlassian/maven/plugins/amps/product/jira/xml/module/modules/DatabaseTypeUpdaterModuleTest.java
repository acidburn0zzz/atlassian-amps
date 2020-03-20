package com.atlassian.maven.plugins.amps.product.jira.xml.module.modules;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.junit.Before;
import org.junit.Test;

import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.H2;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.POSTGRES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DatabaseTypeUpdaterModuleTest {
    private Document document;

    @Before
    public void setUp() {
        DocumentFactory documentFactory = DocumentFactory.getInstance();
        document = documentFactory.createDocument(documentFactory.createElement("jira-database-config"));
    }

    @Test
    public void givenConfigurationWithPostgresTypeWhenProcessedWithPostgresTypeThenConfigIsUnchanged() {
        document.getRootElement().addElement("database-type").setText(POSTGRES.getDbType());

        DatabaseTypeUpdaterModule databaseTypeUpdaterModule = new DatabaseTypeUpdaterModule(POSTGRES);
        assertFalse(databaseTypeUpdaterModule.transform(document));

        assertNotNull(document.selectSingleNode("//jira-database-config/database-type"));
        assertEquals(document.selectSingleNode("//jira-database-config/database-type").getText(), POSTGRES.getDbType());
    }

    @Test
    public void givenConfigurationWithH2TypeWhenProcessedWithPostgresTypeThenConfigIsUnchanged() {
        document.getRootElement().addElement("database-type").setText(H2.getDbType());

        DatabaseTypeUpdaterModule databaseTypeUpdaterModule = new DatabaseTypeUpdaterModule(POSTGRES);
        assertTrue(databaseTypeUpdaterModule.transform(document));

        assertNotNull(document.selectSingleNode("//jira-database-config/database-type"));
        assertEquals(document.selectSingleNode("//jira-database-config/database-type").getText(), POSTGRES.getDbType());
    }

    @Test
    public void givenConfigurationWithoutTypeWhenProcessedWithPostgresTypeThenConfigIsUnchanged() {
        
        DatabaseTypeUpdaterModule databaseTypeUpdaterModule = new DatabaseTypeUpdaterModule(POSTGRES);
        assertFalse(databaseTypeUpdaterModule.transform(document));

        assertNull(document.selectSingleNode("//jira-database-config/database-type"));
    }
}
