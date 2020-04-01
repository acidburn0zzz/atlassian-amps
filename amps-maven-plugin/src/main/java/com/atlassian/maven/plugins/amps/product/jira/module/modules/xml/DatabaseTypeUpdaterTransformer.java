package com.atlassian.maven.plugins.amps.product.jira.module.modules.xml;

import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import com.atlassian.maven.plugins.amps.product.jira.module.DocumentTransformer;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;

public class DatabaseTypeUpdaterTransformer implements DocumentTransformer {

    private final JiraDatabaseType dbType;

    public DatabaseTypeUpdaterTransformer(JiraDatabaseType dbType) {
        this.dbType = dbType;
    }

    @Override
    public boolean transform(Document entity) {
        final Node dbTypeNode = entity.selectSingleNode("//jira-database-config/database-type");

        // update database type
        if (null != dbTypeNode && StringUtils.isNotEmpty(dbTypeNode.getStringValue())) {
            String currentDbType = dbTypeNode.getStringValue();
            if (!currentDbType.equals(dbType.getDbType())) {
                dbTypeNode.setText(dbType.getDbType());
                return true;
            }
        }

        return false;
    }
}
