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

import java.io.File;
import java.io.IOException;

import static com.atlassian.maven.plugins.amps.product.jira.utils.DocumentUtils.copySampleDbConfigTo;
import static com.atlassian.maven.plugins.amps.product.jira.utils.DocumentUtils.getDocumentFrom;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;
import static org.mockito.Mockito.mock;


public class TestUpdateDbConfigXml {

    protected JiraProductHandler jiraProductHandler;

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected static File getSubdirectoryFromTempDir(final String subPath) throws IOException {
        return new File(temporaryFolder.getRoot(), subPath);
    }

    protected static File getConfigFile() throws IOException {
        return getSubdirectoryFromTempDir("test/dbconfig.xml");
    }

    @Before
    public void setUp() throws IOException {
        jiraProductHandler = new JiraProductHandler(
                mock(MavenContext.class),
                mock(MavenGoals.class),
                mock(ArtifactFactory.class)
        );

        deleteDirectory(getSubdirectoryFromTempDir("test"));
        getSubdirectoryFromTempDir("test").mkdir();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        deleteDirectory(getSubdirectoryFromTempDir("test"));
    }

    @Test
    public void shouldModifyDatabaseTypeWhenTypeIsDifferent() throws Exception {
        copySampleDbConfigTo(getConfigFile());

        jiraProductHandler.updateDbConfigXml(getSubdirectoryFromTempDir("test"), JiraDatabaseType.POSTGRES, "PUBLIC");

        Document doc = getDocumentFrom(getConfigFile());
        assertThat(doc, hasXPath(
                "//jira-database-config/database-type",
                containsString(JiraDatabaseType.POSTGRES.getDbType())
        ));
    }

    @Test
    public void shouldCreateSchemaWhenSchemaIsMissingAndDatabaseSupportsSchema() throws Exception {
        copySampleDbConfigTo(getConfigFile());

        jiraProductHandler.updateDbConfigXml(getSubdirectoryFromTempDir("test"), JiraDatabaseType.MSSQL, "PUBLIC");

        Document doc = getDocumentFrom(getConfigFile());
        assertThat(doc, hasXPath(
                "//jira-database-config/schema-name",
                containsString("PUBLIC")
        ));
    }
}
