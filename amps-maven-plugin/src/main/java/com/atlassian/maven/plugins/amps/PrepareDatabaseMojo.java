package com.atlassian.maven.plugins.amps;


import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.List;

import static com.atlassian.maven.plugins.amps.product.ImportMethod.SQL;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.getDatabaseType;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Run the pre integration tests prepare data.
 */
@Mojo (name = "prepare-database", requiresDependencyResolution = ResolutionScope.TEST)
public class PrepareDatabaseMojo extends AbstractTestGroupsHandlerMojo
{
    @Parameter (property = "maven.test.skip", defaultValue = "false")
    private boolean testsSkip;

    @Parameter (property = "skipTests", defaultValue = "false")
    private boolean skipTests;

    @Parameter (property = "db.dump.file.path")
    private String dumpFilePath;

    @Parameter (property = "import.method")
    private String importMethod;

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
        if (productExecutions != null)
        {
            for (final ProductExecution productExecution : productExecutions)
            {
                // clear dirty data for JIRA to run integration test
                if (ProductHandlerFactory.JIRA.equals(productExecution.getProduct().getId()))
                {
                    final List<DataSource> dataSources = productExecution.getProduct().getDataSources();
                    switch (dataSources.size())
                    {
                        case 1:
                            final DataSource dataSource = dataSources.get(0);
                            final JiraDatabaseType databaseType = getDatabaseType(dataSource)
                                    .orElseThrow(() -> new MojoExecutionException(
                                            "Could not detect database type for dataSource: " + dataSource));
                            // need to be clever here: add database artifact for maven-sql-plugin to execute sql
                            // if we have configured database library in product artifacts then we have 2 solutions:
                            // 1. check groupId, artifactId of all product's libraries contain
                            // our database type name: postgres, mysql, mssql, oracle then put to dataSource libArtifact
                            // 2. add all of product artifacts to dependencies (safety but redundant the others product libraries)
                            // fail-back we add default database library by detected database type above when: not config product artifacts
                            // or product's artifacts groupId, artifactId does not contain database name
                            final List<ProductArtifact> libArtifacts = productExecution.getProduct().getLibArtifacts();
                            if (libArtifacts == null || libArtifacts.isEmpty())
                            {
                                throw new MojoExecutionException("Product library artifact is empty, please provide library for database: " + databaseType);
                            }
                            for (ProductArtifact productArtifact : libArtifacts)
                            {
                                dataSource.getLibArtifacts().add(new LibArtifact(
                                        productArtifact.getGroupId(), productArtifact.getArtifactId(), productArtifact.getVersion()));
                            }
                            populateDatasourceParameter(dataSource);
                            goals.runPreIntegrationTest(dataSource);
                            break;
                        case 0:
                            getLog().info("No dataSource configured for pre-integration-test");
                            break;
                        default:
                            getLog().info("Multiple dataSources not supported. Configuration has these " + dataSources.size() + " dataSources:");
                            for (DataSource dbSource : dataSources)
                            {
                                getLog().info("Database URL: " + dbSource.getUrl());
                            }
                    }
                }
            }
        }
    }

    private void populateDatasourceParameter(final DataSource dataSource)
    {
        if (isNotEmpty(defaultDatabase))
        {
            dataSource.setDefaultDatabase(defaultDatabase);
        }
        if (isNotEmpty(systemUsername))
        {
            dataSource.setSystemUsername(systemUsername);
        }
        if (isNotEmpty(systemPassword))
        {
            dataSource.setSystemPassword(systemPassword);
        }
        if (isNotEmpty(dumpFilePath))
        {
            dataSource.setDumpFilePath(dumpFilePath);
        }
        if (isNotEmpty(importMethod))
        {
            dataSource.setImportMethod(importMethod);
        }
        else
        {
            // default is import standard sql
            dataSource.setImportMethod(SQL.getMethod());
        }
        getLog().info("Pre-integration-test import method: " + dataSource.getImportMethod());
    }
}
