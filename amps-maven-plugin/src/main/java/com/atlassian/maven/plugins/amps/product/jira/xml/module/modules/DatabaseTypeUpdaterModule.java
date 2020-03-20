package com.atlassian.maven.plugins.amps.product.jira.xml.module.modules;

import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import com.atlassian.maven.plugins.amps.product.jira.xml.module.TransformationModule;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;

public class DatabaseTypeUpdaterModule implements TransformationModule<Document> {

    private final JiraDatabaseType dbType;

    public DatabaseTypeUpdaterModule(JiraDatabaseType dbType) {
        this.dbType = dbType;
    }

    @Override
    public boolean transform(Document entity) {
        final Node dbTypeNode = entity.selectSingleNode("//jira-database-config/database-type");

        // update database type
        if (null != dbTypeNode && StringUtils.isNotEmpty(dbTypeNode.getStringValue())) {
            String currentDbType = dbTypeNode.getStringValue();
            // check null and difference value from dbType
            if (!currentDbType.equals(dbType.getDbType())) {
                dbTypeNode.setText(dbType.getDbType());
                return true;
            }
        }

        return false;
    }
}
