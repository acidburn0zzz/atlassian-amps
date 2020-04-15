package com.atlassian.maven.plugins.amps.product.common;

import org.dom4j.Document;

public interface XMLDocumentValidator {
    /**
     * @param document to be validated
     * @throws ValidationException when validation fails
     */
    void validate(Document document) throws ValidationException;
}
