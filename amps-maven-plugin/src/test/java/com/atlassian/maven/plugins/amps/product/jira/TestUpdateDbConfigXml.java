package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.product.JiraProductHandler;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;
import static org.mockito.Mockito.mock;


public class TestUpdateDbConfigXml {
    public static final String SAMPLE_DB_CONFIG = "/com/atlassian/maven/plugins/amps/product/jira/sample.config.db.xml";
    protected JiraProductHandler jiraProductHandler;

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected static File getTempDir(final String subPath) throws IOException {
        return new File(temporaryFolder.getRoot(), subPath);
    }

    protected static File getConfigFile() throws IOException {
        return getTempDir("test/dbconfig.xml");
    }

    protected static File getH2DbFile() throws IOException {
        return getTempDir("test/database/h2db");
    }

    protected static Document getDocumentFrom(File f) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(getConfigFile());
    }


    protected void copySampleDbConfig() throws IOException {
        InputStream is = TestUpdateDbConfigXml.class.getResourceAsStream(SAMPLE_DB_CONFIG);
        copyInputStreamToFile(is, getConfigFile());
    }

    @Before
    public void setUp() throws IOException {
        jiraProductHandler = new JiraProductHandler(
                mock(MavenContext.class),
                mock(MavenGoals.class),
                mock(ArtifactFactory.class)
        );

        deleteDirectory(getTempDir("test"));
        getTempDir("test").mkdir();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        deleteDirectory(getTempDir("test"));
    }

    @Test
    public void shouldModifyDatabaseTypeWhenTypeIsDifferent() throws Exception {
        copySampleDbConfig();

        jiraProductHandler.updateDbConfigXml(getTempDir("test"), JiraDatabaseType.POSTGRES, "PUBLIC");

        Document doc = getDocumentFrom(getConfigFile());
        assertThat(doc, hasXPath(
                "//jira-database-config/database-type",
                containsString(JiraDatabaseType.POSTGRES.getDbType())
        ));
    }

    @Test
    public void shouldCreateSchemaWhenSchemaIsMissingAndDatabaseSupportsSchema() throws Exception {
        copySampleDbConfig();

        jiraProductHandler.updateDbConfigXml(getTempDir("test"), JiraDatabaseType.MSSQL, "PUBLIC");

        Document doc = getDocumentFrom(getConfigFile());
        assertThat(doc, hasXPath(
                "//jira-database-config/schema-name",
                containsString("PUBLIC")
        ));
    }
}
