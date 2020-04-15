package com.atlassian.maven.plugins.amps.product.jira.config;

import com.atlassian.maven.plugins.amps.product.common.ValidationException;
import com.atlassian.maven.plugins.amps.product.common.XMLDocumentValidator;
import org.dom4j.Document;

public class DbConfigValidator implements XMLDocumentValidator {
    public void validate(Document document) throws ValidationException {
        if (document.selectSingleNode("/jira-database-config") == null) {
            throw new ValidationException("Database configuration file is invalid - missing root entity 'jira-database-config'");
        }
    }
}
