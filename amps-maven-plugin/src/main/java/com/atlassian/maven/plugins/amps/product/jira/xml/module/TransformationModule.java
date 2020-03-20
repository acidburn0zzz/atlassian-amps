package com.atlassian.maven.plugins.amps.product.jira.xml.module;

import com.atlassian.maven.plugins.amps.product.JiraProductHandler;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

import java.io.File;

/**
 * TransformationModule is calculation unit I use to refactor:
 * {@link JiraProductHandler#updateDbConfigXml(File, JiraDatabaseType, String)}
 *
 *
 */
public interface TransformationModule<T> {

    /**
     *
     * @param entity - entity to be transformed
     * @return if entity has been transformed
     */
    boolean transform(T entity) throws MojoExecutionException;
}
