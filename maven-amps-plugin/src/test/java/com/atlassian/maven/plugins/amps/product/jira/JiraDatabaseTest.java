package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JiraDatabaseTest
{
    @Test
    public void postgresDatabaseName() throws Exception
    {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getDriver()).thenReturn("org.postgresql.Driver");
        final JiraDatabasePostgresImpl postgres = new JiraDatabasePostgresImpl(dataSource);
        assertThat("database name should be : ddd", postgres.getDatabaseName("jdbc:postgresql:database"), equalTo("database"));
        assertThat("database name should be : eeee", postgres.getDatabaseName("jdbc:postgresql://host/eeee"), equalTo("eeee"));
        assertThat("database name should be : fff", postgres.getDatabaseName("jdbc:postgresql://host:6969/fff"), equalTo("fff"));
        assertThat("database name should be : ttttt", postgres.getDatabaseName("jdbc:postgresql://localhost:6969/ttttt?user=aaa&password=bbb&uuuuu=ppp"), equalTo("ttttt"));
    }

    @Test
    public void mysqlDatabaseName() throws Exception
    {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getDriver()).thenReturn("com.mysql.jdbc.Driver");
        final JiraDatabaseMysqlImpl mysql = new JiraDatabaseMysqlImpl(dataSource);
        assertThat("database name should be : ddd", mysql.getDatabaseName("jdbc:mysql://localhost:3306/ddd"), equalTo("ddd"));
        assertThat("database name should be : eeee", mysql.getDatabaseName("jdbc:mysql://127.0.0.1:3306/eeee?profileSQL=true"), equalTo("eeee"));
    }

    @Test
    public void mssqlDatabaseName() throws Exception
    {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getDriver()).thenReturn("net.sourceforge.jtds.jdbc.Driver");
        final JiraDatabaseMssqlImpl mssql = new JiraDatabaseMssqlImpl(dataSource);
        assertThat("database name should be : ddd", mssql.getDatabaseName("jdbc:jtds:sqlserver://localhost:1433/ddd"), equalTo("ddd"));
        assertThat("database name should be : eeee", mssql.getDatabaseName("jdbc:jtds:sybase://127.0.0.1/eeee;autoCommit=false"), equalTo("eeee"));
    }
}