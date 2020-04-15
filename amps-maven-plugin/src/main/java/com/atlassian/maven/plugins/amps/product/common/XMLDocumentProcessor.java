package com.atlassian.maven.plugins.amps.product.common;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

/**
 * XMLDocumentProcessor should be used in correct order:
 * <ol>
 *  <li>load</li>
 *  <li>validate*</li>
 *  <li>transform*</li>
 *  <li>saveIfModified</li>
 * </ol>
 * (*) marked methods may be used zero or more times.
 * <p>
 * Different order may result in undefined behaviour.
 */
public class XMLDocumentProcessor {
    private boolean modified;
    private final XMLDocumentHandler xmlDocumentHandler;

    private Document document;

    public XMLDocumentProcessor(XMLDocumentHandler xmlDocumentHandler) {
        this.xmlDocumentHandler = xmlDocumentHandler;
        this.modified = false;
    }

    /**
     * loads document into memory for further transformations.
     */
    public XMLDocumentProcessor load() throws MojoExecutionException {
        document = xmlDocumentHandler.read();
        return this;
    }


    public XMLDocumentProcessor validate(XMLDocumentValidator validator) throws ValidationException {
        validator.validate(document);
        return this;
    }


    public XMLDocumentProcessor transform(XMLDocumentTransformer handler) throws MojoExecutionException {
        modified |= handler.transform(document);
        return this;
    }

    public void saveIfModified() throws MojoExecutionException {
        if (this.modified) {
            this.xmlDocumentHandler.write(this.document);
        }
    }

}
