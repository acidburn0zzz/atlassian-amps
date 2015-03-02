package com.atlassian.maven.plugins.amps.product.jira;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.maven.plugins.amps.LibArtifact;
import com.atlassian.maven.plugins.amps.DataSource;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public abstract class AbstractJiraDatabase implements JiraDatabase
{
    private DataSource dataSource;
    protected LibArtifact lib;

    public AbstractJiraDatabase(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    protected abstract String dropDatabase() throws MojoExecutionException;
    protected abstract String createDatabase() throws MojoExecutionException;
    protected abstract String dropUser();
    protected abstract String createUser();
    protected abstract String grantPermissionForUser() throws MojoExecutionException;

    protected Xpp3Dom systemDatabaseConfiguration()
    {
        return configuration(
                element(name("driver") , dataSource.getDriver()),
                element(name("url"), dataSource.getDefaultDatabase()),
                element(name("username"), dataSource.getSystemUsername()),
                element(name("password"), dataSource.getSystemPassword()),
                // we need commit transaction for drop database and then create them again
                element(name("autocommit"), "true")
        );
    }

    protected Xpp3Dom productDatabaseConfiguration()
    {
        return configuration(
                element(name("driver") , dataSource.getDriver()),
                element(name("url"), dataSource.getUrl()),
                element(name("username"), dataSource.getUsername()),
                element(name("password"), dataSource.getPassword()),
                // we need commit transaction for drop database and then create them again
                element(name("autocommit"), "true")
        );
    }

    protected abstract String getDatabaseName(String url) throws MojoExecutionException;

    @Override
    public List<Dependency> getDependencies()
    {
        if (null == dataSource.getLibArtifacts() || dataSource.getLibArtifacts().size() == 0)
        {
            return null;
        }
        List<Dependency> dependencies = new ArrayList<Dependency>();
        for(LibArtifact libArtifact: dataSource.getLibArtifacts())
        {
            Dependency dependency = new Dependency();
            dependency.setGroupId(libArtifact.getGroupId());
            dependency.setArtifactId(libArtifact.getArtifactId());
            dependency.setVersion(libArtifact.getVersion());
            dependencies.add(dependency);
        }
        return dependencies;
    }

    @Override
    public Xpp3Dom getConfigImportFile()
    {
        Xpp3Dom pluginConfiguration = productDatabaseConfiguration();
        pluginConfiguration.addChild(
                element(name("srcFiles"),
                        element(name("srcFile"), getDataSource().getDumpFilePath())).toDom()
        );
        return pluginConfiguration;
    }

    @Override
    public Xpp3Dom getConfigDatabaseTool() throws MojoExecutionException
    {
        return null;
    }
}
