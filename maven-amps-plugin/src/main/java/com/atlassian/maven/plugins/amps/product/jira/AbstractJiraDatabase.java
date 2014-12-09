package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.LibArtifact;
import com.atlassian.maven.plugins.amps.DataSource;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public abstract class AbstractJiraDatabase implements JiraDatabase
{
    private DataSource dataSource;
    private LibArtifact lib;

    public AbstractJiraDatabase(DataSource dataSource)
    {
        this.dataSource = dataSource;
        this.lib = dataSource.getLibArtifacts().get(0);
    }

    public DataSource getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public LibArtifact getLib()
    {
        return lib;
    }

    public void setLib(LibArtifact lib)
    {
        this.lib = lib;
    }

    protected abstract String dropDatabase();
    protected abstract String createDatabase();
    protected abstract String dropUser();
    protected abstract String createUser();
    protected abstract String grantPermissionForUser();

    protected Xpp3Dom baseConfiguration()
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

    protected abstract String getDatabaseName(String url);

    @Override
    public Dependency getDependency()
    {
        Dependency databaseDependency = new Dependency();
        databaseDependency.setGroupId(lib.getGroupId());
        databaseDependency.setArtifactId(lib.getArtifactId());
        databaseDependency.setVersion(lib.getVersion());
        return databaseDependency;
    }
}
