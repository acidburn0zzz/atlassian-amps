package com.atlassian.maven.plugins.amps.product.jira.xml.module.modules;

import org.apache.maven.plugin.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.H2;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.MYSQL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class H2UrlUpdaterModuleTest {
    private Document document;

    static private String DUMMY_URL = "dummy";
    static private File SAMPLE_FILE = new File("/sample/file/path");
    static private String DATABASE_URI = "jdbc:h2:file:/sample/file/path/database/h2db;MV_STORE=FALSE;MVCC=TRUE";

    @Before
    public void setUp() {
        DocumentFactory documentFactory = DocumentFactory.getInstance();
        document = documentFactory.createDocument(documentFactory.createElement("jira-database-config"));
    }

    @Test
    public void givenExistingJDBCUrlWhenTransformedWithH2ThenConfigurationIsUpdated() throws Exception {
        document.getRootElement().addElement("jdbc-datasource").addElement("url").setText(DUMMY_URL);

        H2UrlUpdaterModule h2UrlUpdaterModule = new H2UrlUpdaterModule(SAMPLE_FILE, H2, null);
        assertTrue(h2UrlUpdaterModule.transform(document));

        assertNotNull(document.selectSingleNode("//jira-database-config/jdbc-datasource/url"));
        assertEquals(document.selectSingleNode("//jira-database-config/jdbc-datasource/url").getText(), DATABASE_URI);
    }

    @Test
    public void givenExistingJDBCUrlWhenTransformedWithMySQLThenConfigurationIsNotUpdated() throws Exception {
        document.getRootElement().addElement("jdbc-datasource").addElement("url").setText(DUMMY_URL);

        H2UrlUpdaterModule h2UrlUpdaterModule = new H2UrlUpdaterModule(SAMPLE_FILE, MYSQL, null);
        assertFalse(h2UrlUpdaterModule.transform(document));

        assertNotNull(document.selectSingleNode("//jira-database-config/jdbc-datasource/url"));
        assertEquals(document.selectSingleNode("//jira-database-config/jdbc-datasource/url").getText(), DUMMY_URL);
    }

    @Test
    public void givenMissingJDBCUrlWhenTransformedWithH2ThenConfigurationIsNotUpdatedAndWarnIsPrinted() throws Exception {
        Log logger = mock(Log.class);

        H2UrlUpdaterModule h2UrlUpdaterModule = new H2UrlUpdaterModule(SAMPLE_FILE, H2, logger);
        assertFalse(h2UrlUpdaterModule.transform(document));

        assertNull(document.selectSingleNode("//jira-database-config/jdbc-datasource/url"));
        verify(logger).warn(anyString());
    }

}
