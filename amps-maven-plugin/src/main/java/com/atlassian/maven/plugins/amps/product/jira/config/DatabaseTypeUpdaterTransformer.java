package com.atlassian.maven.plugins.amps.product.jira.config;

import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import com.atlassian.maven.plugins.amps.product.common.XMLDocumentTransformer;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;

/**
 * This transformer will update or create database-type node in dbconfig.xml to represent configured database type.
 */
public class DatabaseTypeUpdaterTransformer implements XMLDocumentTransformer {

    private final JiraDatabaseType dbType;

    public DatabaseTypeUpdaterTransformer(JiraDatabaseType dbType) {
        this.dbType = dbType;
    }

    @Override
    public boolean transform(Document document) {
        final Node dbTypeNode = document.selectSingleNode("/jira-database-config/database-type");

        // update database type
        if (dbTypeNode != null && StringUtils.isNotEmpty(dbTypeNode.getStringValue())) {
            String currentDbType = dbTypeNode.getStringValue();
            if (!currentDbType.equals(dbType.getDbType())) {
                dbTypeNode.setText(dbType.getDbType());
                return true;
            }
        }

        return false;
    }
}
