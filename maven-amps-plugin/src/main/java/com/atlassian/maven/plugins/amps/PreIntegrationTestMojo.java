package com.atlassian.maven.plugins.amps;


import java.util.List;

import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType;

import com.google.common.collect.ImmutableList;

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
            for (ProductExecution pe : productExecutions)
            {
                if (ProductHandlerFactory.JIRA.equals(pe.getProduct().getId()))
                {
                    List<DataSource> dataSources = pe.getProduct().getDataSources();
                    if (dataSources.size() == 1)
                    {
                        DataSource dataSource = dataSources.get(0);
                        if (null == pe.getProduct().getLibArtifacts() || pe.getProduct().getLibArtifacts().size() == 0)
                        {
                            throw new MojoExecutionException("Missing configuration dataSource artifact library");
                        }
                        // determine lib artifact for jdbc
                        JiraDatabaseType databaseType = JiraDatabaseType.getDatabaseType(dataSource.getUrl(), dataSource.getDriver());
                        boolean hasDatabaseLibrary = false;
                        for (ProductArtifact lib : pe.getProduct().getLibArtifacts())
                        {
                            if (databaseType.getLibArtifact().equals(lib.getGroupId() + ":" + lib.getArtifactId()))
                            {
                                hasDatabaseLibrary = true;
                                LibArtifact jdbc = new LibArtifact();
                                jdbc.setGroupId(lib.getGroupId());
                                jdbc.setArtifactId(lib.getArtifactId());
                                jdbc.setVersion(lib.getVersion());
                                dataSource.setLibArtifacts(ImmutableList.of(jdbc));
                                break;
                            }
                        }
                        if (hasDatabaseLibrary)
                        {
                            goals.runPreIntegrationTest(dataSource);
                        }
                    }
                    else if (dataSources.size() > 1)
                    {
                        throw new MojoExecutionException("Could not support multiple dataSource");
                    }
                }
            }
        }
    }
}
