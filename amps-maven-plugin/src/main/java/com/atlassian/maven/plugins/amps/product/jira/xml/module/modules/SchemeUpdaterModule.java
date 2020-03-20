package com.atlassian.maven.plugins.amps.product.jira.xml.module.modules;

import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import com.atlassian.maven.plugins.amps.product.jira.xml.module.TransformationModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

public class SchemeUpdaterModule implements TransformationModule {
    private final JiraDatabaseType jiraDatabaseType;
    private final String schema;

    public SchemeUpdaterModule(JiraDatabaseType jiraDatabaseType, String schema) {
        this.jiraDatabaseType = jiraDatabaseType;
        this.schema = schema;
    }

    @Override
    public boolean transform(Document document) throws MojoExecutionException {
        final Node schemaNode = document.selectSingleNode("//jira-database-config/schema-name");

        // postgres, mssql, hsql
        if (jiraDatabaseType.hasSchema()) {
            if (StringUtils.isEmpty(schema)) {
                throw new MojoExecutionException("Database configuration missed schema");
            }
            if (null == schemaNode) {
                // add schema-name node
                try {
                    ((Element) document.selectSingleNode("//jira-database-config"))
                            .addElement("schema-name").addText(schema);
                    return true;
                } catch (NullPointerException npe) {
                    throw new MojoExecutionException(npe.getMessage());
                }
            } else {
                if (StringUtils.isNotEmpty(schemaNode.getText()) && !schema.equals(schemaNode.getText())) {
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
