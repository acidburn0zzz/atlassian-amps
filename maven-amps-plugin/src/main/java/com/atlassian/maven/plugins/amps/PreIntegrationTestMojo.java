package com.atlassian.maven.plugins.amps;


import java.util.List;

import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Run the pre integration tests prepare data.
 */
@Mojo (name = "pre-integration-test", requiresDependencyResolution = ResolutionScope.TEST)
public class PreIntegrationTestMojo extends RunMojo
{
    @Parameter (property = "maven.test.skip", defaultValue = "false")
    private boolean testsSkip;

    @Parameter (property = "skipTests", defaultValue = "false")
    private boolean skipTests;

    @Parameter (property = "db.dump.file.path")
    private String dumpFilePath;

    @Parameter (property = "db.default.database")
    private String defaultDatabase;

    @Parameter (property = "db.system.username")
    private String systemUsername;

    @Parameter (property = "db.system.password")
    private String systemPassword;

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        if (testsSkip || skipTests)
        {
            getLog().info("Pre integration tests skipped");
            return;
        }
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

                            populateDatasourceParameter(dataSource);
                            goals.runPreIntegrationTest(dataSource, dataSource.getDumpFilePath());
                            break;
                        case 0:
                            getLog().info("Missing configuration dataSource for pre-integration-test");
                        default:
                            getLog().info("Multiple dataSources does not support. Configuration has: " + dataSources.size() + " dataSources below");
                            for (DataSource dbSource : dataSources)
                            {
                                getLog().info("Database URL: " + dbSource.getUrl());
                            }
                            getLog().info("Could not support multiple dataSource");
                    }
                }
            }
        }
    }

    private void populateDatasourceParameter(DataSource dataSource)
    {
        if (StringUtils.isNotEmpty(defaultDatabase))
        {
            dataSource.setDefaultDatabase(defaultDatabase);
        }
        if (StringUtils.isNotEmpty(systemUsername))
        {
            dataSource.setSystemUsername(systemUsername);
        }
        if (StringUtils.isNotEmpty(systemPassword))
        {
            dataSource.setSystemPassword(systemPassword);
        }
        if (StringUtils.isNotEmpty(dumpFilePath))
        {
            dataSource.setDumpFilePath(dumpFilePath);
        }
    }
}
