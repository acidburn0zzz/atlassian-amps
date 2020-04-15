package com.atlassian.maven.plugins.amps.product.common;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class is responsible for reading and saving XML document on change.
 * It delegates transformation to external objects.
 */
public class XMLDocumentHandler {
    protected final File file;

    /**
     * @param file - xml file to be transformed
     */
    public XMLDocumentHandler(File file) {
        this.file = file;
    }

    protected Document read() throws MojoExecutionException {
        final SAXReader reader = new SAXReader();
        try {
            return reader.read(file);
        } catch (DocumentException ex) {
            throw new MojoExecutionException("Cannot parse XML file: " + file.getName(), ex);
        }
    }

    protected void write(Document document) throws MojoExecutionException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            XMLWriter writer = new XMLWriter(fos, OutputFormat.createPrettyPrint());
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write updated XML file: " + file.getName(), e);
        }
    }
}
