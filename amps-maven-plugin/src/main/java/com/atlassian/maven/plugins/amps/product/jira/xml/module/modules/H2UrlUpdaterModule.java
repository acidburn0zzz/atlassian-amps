package com.atlassian.maven.plugins.amps.product.jira.xml.module.modules;

import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import com.atlassian.maven.plugins.amps.product.jira.xml.module.TransformationModule;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.Document;
import org.dom4j.Node;

import java.io.File;

import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.H2;
import static org.apache.commons.io.FileUtils.getFile;

public class H2UrlUpdaterModule implements TransformationModule<Document> {
    public static final String H2_JDBC_URL_TEMPLATE = "jdbc:h2:file:%s;MV_STORE=FALSE;MVCC=TRUE";
    public static final String H2_SUFFIX = "database/h2db";

    private final File homeDir;
    private final JiraDatabaseType dbType;
    private final Log logger;

    public H2UrlUpdaterModule(final File homeDir, final JiraDatabaseType dbType, Log logger) {
        this.homeDir = homeDir;
        this.dbType = dbType;
        this.logger = logger;
    }

    @Override
    public boolean transform(Document entity) {
        final Node jdbcUrl = entity.selectSingleNode("//jira-database-config/jdbc-datasource/url");

        if (!dbType.equals(H2)) {
            return false;
        }

        if (jdbcUrl == null) {
            logger.warn("dbconfig.xml doesn't contain jdbc-url");
            return false;
        }

        jdbcUrl.setText(String.format(H2_JDBC_URL_TEMPLATE, getFile(homeDir, H2_SUFFIX)));
        return true;
    }
}
