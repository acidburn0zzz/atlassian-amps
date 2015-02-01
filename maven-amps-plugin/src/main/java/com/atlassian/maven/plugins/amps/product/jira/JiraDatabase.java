package com.atlassian.maven.plugins.amps.product.jira;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public interface JiraDatabase
{
    /**
     * create sql-maven-plugin configuration, include all sql for drop/create database
     * Please refer to documentation of maven-sql-plugin at url
     * http://mojo.codehaus.org/sql-maven-plugin/index.html
     * @return configuration
     */
    Xpp3Dom getPluginConfiguration() throws MojoExecutionException;

    /**
     * create database library dependency for sql-maven-plugin connect to database
     * @return dependency
     */
    List<Dependency> getDependencies();
}
