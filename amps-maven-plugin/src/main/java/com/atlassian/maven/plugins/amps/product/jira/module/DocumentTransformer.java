package com.atlassian.maven.plugins.amps.product.jira.module;

import com.atlassian.maven.plugins.amps.product.JiraProductHandler;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

import java.io.File;

/**
 * TransformationModule is a basic calculation unit that is used to wrap structural code into something meaningful.
 * It is an effort to modularize code into closed modules to declutter classes which are overwhelmed with functions.
 */
public interface DocumentTransformer {

    /**
     * @param entity - entity to be transformed
     * @return if entity has been transformed
     */
    boolean transform(Document entity) throws MojoExecutionException;
}
