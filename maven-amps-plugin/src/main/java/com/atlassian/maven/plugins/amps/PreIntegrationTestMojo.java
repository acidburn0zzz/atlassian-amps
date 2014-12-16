package com.atlassian.maven.plugins.amps;


import java.util.List;

import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Run the pre integration tests prepare data.
 */
@Mojo (name = "pre-integration-test", requiresDependencyResolution = ResolutionScope.TEST)
public class PreIntegrationTestMojo extends RunMojo
{
    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        final MavenGoals goals = getMavenGoals();
        final List<ProductExecution> productExecutions = getProductExecutions();
        if (null != productExecutions)
        {
            for (ProductExecution productExecution : productExecutions)
            {
                // clear dirty data for JIRA to run integration test
                if (ProductHandlerFactory.JIRA.equals(productExecution.getProduct().getId()))
                {
                    List<DataSource> dataSources = productExecution.getProduct().getDataSources();
                    switch (dataSources.size())
                    {
                        case 1:
                            DataSource dataSource = dataSources.get(0);
                            JiraDatabaseType databaseType = JiraDatabaseType.getDatabaseType(dataSource.getUrl(), dataSource.getDriver());
                            if (null == databaseType)
                            {
                                throw new MojoExecutionException("Could not detect database type, please check your database driver: " + dataSource.getDriver() + " and database url: " + dataSource.getUrl());
                            }
                            // need to be clever here: add database artifact for maven-sql-plugin to execute sql
                            // if we have configured database library in product artifacts then we have 2 solutions:
                            // 1. check groupId, artifactId of all product's libraries contain
                            // our database type name: postgres, mysql, mssql, oracle then put to dataSource libArtifact
                            // 2. add all of product artifacts to dependencies (safety but redundant the others product libraries)
                            // fail-back we add default database library by detected database type above when: not config product artifacts
                            // or product's artifacts groupId, artifactId does not contain database name
                            if (null == productExecution.getProduct().getLibArtifacts() || productExecution.getProduct().getLibArtifacts().size() == 0)
                            {
                                throw new MojoExecutionException("Product library artifact is empty, please provide library for database: " + databaseType.toString());
                            }
                            for (ProductArtifact productArtifact : productExecution.getProduct().getLibArtifacts())
                            {
                                dataSource.getLibArtifacts().add(new LibArtifact(productArtifact.getGroupId(), productArtifact.getArtifactId(), productArtifact.getVersion()));
                            }
                            goals.runPreIntegrationTest(dataSource);
                            break;
                        case 0:
                            throw new MojoExecutionException("Missing configuration dataSource");
                        default:
                            getLog().info("Multiple dataSources does not support. Configuration has: " + dataSources.size() + " dataSources below");
                            for (DataSource dbSource : dataSources)
                            {
                                getLog().info("Database URL: " + dbSource.getUrl());
                            }
                            throw new MojoExecutionException("Could not support multiple dataSource");
                    }
                }
            }
        }
    }
}
