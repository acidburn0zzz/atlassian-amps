package com.atlassian.maven.plugins.amps.product.jira.xml.module;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

public interface TransformationModule {

    /**
     *
     * @param document - document to be transformed
     * @return if document has been transformed
     */
    boolean transform(Document document) throws MojoExecutionException;
}
