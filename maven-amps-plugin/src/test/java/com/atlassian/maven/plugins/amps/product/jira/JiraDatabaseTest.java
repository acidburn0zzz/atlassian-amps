package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.LibArtifact;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class JiraDatabaseTest
{
    @Test
    public void postgresDatabaseName() throws Exception
    {
        final DataSource dataSource = postgresDataSourceTest();
        final JiraDatabasePostgresqlImpl postgres = new JiraDatabasePostgresqlImpl(dataSource);
        assertThat("database name should be : ddd", postgres.getDatabaseName("abc:ddd"), equalTo("ddd"));
    }

    private static final DataSource postgresDataSourceTest()
    {
        final DataSource dataSource = new DataSource();
        dataSource.setDriver("");
        dataSource.setUrl("");
        dataSource.setUsername("");
        dataSource.setPassword("");

        final LibArtifact lib = new LibArtifact();
        lib.setGroupId("");
        lib.setArtifactId("");
        lib.setVersion("");
        dataSource.setLibArtifacts(ImmutableList.of(lib));
        return dataSource;
    }

}