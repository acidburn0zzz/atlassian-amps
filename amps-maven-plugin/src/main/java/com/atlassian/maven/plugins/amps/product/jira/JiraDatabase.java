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

    /**
     * create sql-maven-plugin configuration, include sql file dump path for import data
     * Please refer to documentation of maven-sql-plugin at url
     * http://mojo.codehaus.org/sql-maven-plugin/index.html
     * @return configuration
     */
    Xpp3Dom getConfigImportFile();

    /**
     * create exec-maven-plugin configuration to execute specific database tool
     */
    Xpp3Dom getConfigDatabaseTool() throws MojoExecutionException;
}
