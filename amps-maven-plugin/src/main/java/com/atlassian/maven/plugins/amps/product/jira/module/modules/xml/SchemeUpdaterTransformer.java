package com.atlassian.maven.plugins.amps.product.jira.module.modules.xml;

import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import com.atlassian.maven.plugins.amps.product.jira.module.DocumentTransformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

// depend on database type which Jira supported schema or schema-less
// please refer this Jira documentation
// http://www.atlassian.com/software/jira/docs/latest/databases/index.html
public class SchemeUpdaterTransformer implements DocumentTransformer {
    private final JiraDatabaseType jiraDatabaseType;
    private final String schema;

    public SchemeUpdaterTransformer(JiraDatabaseType jiraDatabaseType, String schema) {
        this.jiraDatabaseType = jiraDatabaseType;
        this.schema = schema;
    }

    @Override
    public boolean transform(Document entity) throws MojoExecutionException {
        final Node schemaNode = entity.selectSingleNode("//jira-database-config/schema-name");

        // postgres, mssql, hsql
        if (jiraDatabaseType.hasSchema()) {
            if (StringUtils.isEmpty(schema)) {
                throw new MojoExecutionException("Database configuration missed schema");
            }
            if (null == schemaNode) {
                // add schema-name node
                    ((Element) entity.selectSingleNode("//jira-database-config"))
                            .addElement("schema-name").addText(schema);
                    return true;
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
