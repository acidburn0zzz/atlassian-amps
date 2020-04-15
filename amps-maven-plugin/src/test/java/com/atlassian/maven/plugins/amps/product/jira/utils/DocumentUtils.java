package com.atlassian.maven.plugins.amps.product.jira.utils;

import com.atlassian.maven.plugins.amps.product.jira.TestUpdateDbConfigXml;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

final public class DocumentUtils {
    private static final String SAMPLE_DB_CONFIG = "/com/atlassian/maven/plugins/amps/product/jira/sample.config.db.xml";

    private DocumentUtils() {
    }

    public static Document getDocumentFrom(File configFile) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(configFile);
    }

    public static void copySampleDbConfigTo(File to) throws IOException {
        InputStream is = TestUpdateDbConfigXml.class.getResourceAsStream(SAMPLE_DB_CONFIG);
        copyInputStreamToFile(is, to);
    }
}
