package com.atlassian.maven.plugins.amps.product.jira.config;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.junit.Before;
import org.junit.Test;

import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.H2;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.POSTGRES;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DatabaseTypeUpdaterTransformerTest {
    private Document document;

    @Before
    public void setUp() {
        DocumentFactory documentFactory = DocumentFactory.getInstance();
        document = documentFactory.createDocument(documentFactory.createElement("jira-database-config"));
    }

    @Test
    public void shouldNotModifyTypeWhenSameTypeIsPresent() {
        document.getRootElement().addElement("database-type").setText(POSTGRES.getDbType());
        DatabaseTypeUpdaterTransformer databaseTypeUpdaterTransformer = new DatabaseTypeUpdaterTransformer(POSTGRES);

        boolean transformed = databaseTypeUpdaterTransformer.transform(document);

        assertThat(transformed, is(false));
        assertThat(document.selectSingleNode("/jira-database-config/database-type"), is(not(nullValue())));
        assertThat(document.selectSingleNode("/jira-database-config/database-type").getText(),
                is(equalTo(POSTGRES.getDbType())));
    }

    @Test
    public void shouldModifyTypeWhenDifferentTypeIsPresent() {
        document.getRootElement().addElement("database-type").setText(H2.getDbType());
        DatabaseTypeUpdaterTransformer databaseTypeUpdaterTransformer = new DatabaseTypeUpdaterTransformer(POSTGRES);

        boolean transformed = databaseTypeUpdaterTransformer.transform(document);

        assertThat(transformed, is(true));
        assertThat(document.selectSingleNode("/jira-database-config/database-type"), is(not(nullValue())));
        assertThat(document.selectSingleNode("/jira-database-config/database-type").getText(),
                is(equalTo(POSTGRES.getDbType())));
    }

    @Test
    public void shouldNotCreateTypeWhenMissing() {
        DatabaseTypeUpdaterTransformer databaseTypeUpdaterTransformer = new DatabaseTypeUpdaterTransformer(POSTGRES);

        boolean transformed = databaseTypeUpdaterTransformer.transform(document);

        assertThat(transformed, is(false));
        assertThat(document.selectSingleNode("/jira-database-config/database-type"), is(nullValue()));
    }
}
