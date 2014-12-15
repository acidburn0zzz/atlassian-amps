package com.atlassian.maven.plugins.amps;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
    @Parameter (property = "clear.database", defaultValue = "false")
    private boolean isClearDatabase;

    private final Map<String, LibArtifact> defaultDatabaseLibrary = ImmutableMap.of(
            "POSTGRES", new LibArtifact("org.postgresql", "postgresql", "9.3-1102-jdbc41"),
            "MYSQL", new LibArtifact("mysql", "mysql-connector-java", "5.1.32"),
            "MSSQL", new LibArtifact("net.sourceforge.jtds", "jtds", "1.3.1"),
            "MSSQL_JTDS", new LibArtifact("net.sourceforge.jtds", "jtds", "1.3.1"),
            "ORACLE", new LibArtifact("cn.guoyukun.jdbc", "oracle-ojdbc6", "11.2.0.3.0")
    );

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        final MavenGoals goals = getMavenGoals();
        final List<ProductExecution> productExecutions = getProductExecutions();
        if (null != productExecutions)
        {
            for (ProductExecution pe : productExecutions)
            {
                // clear dirty data for JIRA to run integration test
                if (ProductHandlerFactory.JIRA.equals(pe.getProduct().getId()) && isClearDatabase)
                {
                    List<DataSource> dataSources = pe.getProduct().getDataSources();
                    switch (dataSources.size())
                    {
                        case 1:
                            DataSource dataSource = dataSources.get(0);
                            JiraDatabaseType databaseType = JiraDatabaseType.getDatabaseType(dataSource.getUrl(), dataSource.getDriver());
                            if (null == databaseType)
                            {
                                throw new MojoExecutionException("Could not detect database type, please check your database driver and database url which amps supported");
                            }
                            // need to be clever here: add database artifact for maven-sql-plugin to execute sql
                            // if we have configured database library in product artifacts then we have 2 solutions:
                            // 1. check groupId, artifactId of all product's libraries contain
                            // our database type name: postgres, mysql, mssql, oracle then put to dataSource libArtifact
                            // 2. add all of product artifacts to dependencies (safety but redundant the others product libraries)
                            // fail-back we add default database library by detected database type above when: not config product artifacts
                            // or product's artifacts groupId, artifactId does not contain database name
                            boolean hasDatabaseLibrary = false;
                            if (null != pe.getProduct().getLibArtifacts() && pe.getProduct().getLibArtifacts().size() > 0)
                            {
                                for (ProductArtifact pa : pe.getProduct().getLibArtifacts())
                                {
                                    if (pa.getGroupId().contains(databaseType.toString().toLowerCase())
                                            || pa.getArtifactId().contains(databaseType.toString().toLowerCase()))
                                    {
                                        hasDatabaseLibrary = true;
                                        dataSource.setLibArtifacts(ImmutableList.of(new LibArtifact(pa.getGroupId(), pa.getArtifactId(), pa.getVersion())));
                                        break;
                                    }
                                }
                            }
                            if (!hasDatabaseLibrary)
                            {
                                // add default database artifact
                                dataSource.setLibArtifacts(ImmutableList.of(defaultDatabaseLibrary.get(databaseType.toString())));
                            }
                            goals.runPreIntegrationTest(dataSource);
                            break;
                        case 0:
                            throw new MojoExecutionException("Missing configuration dataSource");
                        default:
                            throw new MojoExecutionException("Could not support multiple dataSource");
                    }
                }
            }
        }
    }
}
