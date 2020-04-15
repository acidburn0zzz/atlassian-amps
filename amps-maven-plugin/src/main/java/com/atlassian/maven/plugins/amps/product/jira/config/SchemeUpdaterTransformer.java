package com.atlassian.maven.plugins.amps.product.jira.config;

import com.atlassian.maven.plugins.amps.product.common.XMLDocumentTransformer;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * This transformer will update or create schema-name node in dbconfig.xml
 * to represent configured schema on databases supporting it.
 * Otherwise, it will remove schema node from configuration,
 * if database is not supporting it.
 */
public class SchemeUpdaterTransformer implements XMLDocumentTransformer {
    private final JiraDatabaseType jiraDatabaseType;
    private final String schema;

    public SchemeUpdaterTransformer(JiraDatabaseType jiraDatabaseType, String schema) {
        this.jiraDatabaseType = jiraDatabaseType;
        this.schema = schema;
    }

    @Override
    public boolean transform(final Document document) throws MojoExecutionException {
        final Node schemaNode = document.selectSingleNode("/jira-database-config/schema-name");

        // postgres, mssql, hsql
        if (jiraDatabaseType.hasSchema()) {
            if (isEmpty(schema)) {
                throw new MojoExecutionException("Database configuration is missing the schema");
            }
            if (null == schemaNode) {
                // add schema-name node
                ((Element) document.selectSingleNode("/jira-database-config"))
                        .addElement("schema-name").addText(schema);
                return true;
            } else {
                if (isNotEmpty(schemaNode.getText()) && !schema.equals(schemaNode.getText())) {
                    schemaNode.setText(schema);
                    return true;
                }
            }
        }
        // mysql, oracle
        else {
            if (schemaNode != null) {
                // remove schema node
                schemaNode.detach();
                return true;
            }
        }
        return false;
    }
}
