package com.atlassian.maven.plugins.amps;


import com.atlassian.maven.plugins.amps.product.ImportMethod;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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


    private static final String FILENAME_DBCONFIG = "dbconfig.xml";


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
                            updateDbConfigXmlWithDatasourceValues(new File(productExecution.getProduct().getDataHome()), dataSource);
                            goals.runPreIntegrationTest(dataSource);
                            break;
                        case 0:
                            getLog().info("Missing configuration dataSource for pre-integration-test");
                            break;
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
        if (StringUtils.isNotEmpty(importMethod))
        {
            dataSource.setImportMethod(importMethod);
        }
        else
        {
            // default is import standard sql
            dataSource.setImportMethod(ImportMethod.SQL.getMethod());
        }
        getLog().info("Pre-integration-test import method: " + dataSource.getImportMethod());
    }

    public void updateDbConfigXmlWithDatasourceValues(final File homeDir, final DataSource dataSource)
            throws MojoExecutionException
    {
        final File dbConfigXml = new File(homeDir, FILENAME_DBCONFIG);
        if (!dbConfigXml.exists())
        {
            return;
        }

        try
        {
            InputStream templateIn = new FileInputStream(dbConfigXml);
            String template = IOUtils.toString(templateIn, "utf-8");
            template = template.replace("@@DATABASE-NAME@@", dataSource.getDatabaseName());
            template = template.replace("@@DELEGATOR-NAME@@", dataSource.getDelegatorName());
            template = template.replace("@@DATABASE-TYPE@@", dataSource.getType());
            template = template.replace("@@SCHEMA-NAME@@", dataSource.getSchema());
            template = template.replace("@@JDBC-URL@@", dataSource.getUrl());
            template = template.replace("@@JDBC-DRIVER-CLASS@@", dataSource.getDriver());
            template = template.replace("@@JDBC-USERNAME@@", dataSource.getUsername());
            template = template.replace("@@JDBC-PASSWORD@@", dataSource.getPassword());
            template = template.replace("@@JDBC-POOL-SIZE@@", dataSource.getJdbcPoolSize());
            template = template.replace("@@JDBC-VALIDATION@@", dataSource.getJdbcValidation());

            FileUtils.writeStringToFile(dbConfigXml, template, "utf-8");
        }
        catch(IOException ex)
        {
            throw new MojoExecutionException("Unable to update config file: " + FILENAME_DBCONFIG, ex);
        }
    }
}
