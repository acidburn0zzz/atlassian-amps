package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import org.junit.Before;
import org.junit.Test;

import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseFactory.getJiraDatabaseFactory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JiraDatabaseTest
{
    private static JiraDatabaseFactory factory;

    @Before
    public void setup()
    {
        factory = getJiraDatabaseFactory();
    }

    @Test
    public void postgresDatabaseName() throws Exception
    {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getDriver()).thenReturn("org.postgresql.Driver");
        final JiraDatabasePostgresImpl postgres = new JiraDatabasePostgresImpl(dataSource);
        assertThat("database name should be : ddd", postgres.getDatabaseName("jdbc:postgresql:ddd"), equalTo("ddd"));
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

    @Test
    public void postgresDatabaseFactory() throws Exception
    {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:postgresql://host/eeee");
        when(dataSource.getDriver()).thenReturn("org.postgresql.Driver");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);
        assertThat("Database implementation must be Postgres", jiraDatabase, instanceOf(JiraDatabasePostgresImpl.class));
    }

    @Test
    public void mysqlDatabaseFactory() throws Exception
    {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:mysql://localhost:3306/ffffff");
        when(dataSource.getDriver()).thenReturn("com.mysql.jdbc.Driver");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);
        assertThat("Database implementation must be Mysql", jiraDatabase, instanceOf(JiraDatabaseMysqlImpl.class));
    }

    @Test
    public void oracleDatabaseFactory() throws Exception
    {
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:oracle:thin:@localhost:1521:XE");
        when(dataSource.getDriver()).thenReturn("oracle.jdbc.OracleDriver");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);
        assertThat("Database implementation must be Oracle", jiraDatabase, instanceOf(JiraDatabaseOracleImpl.class));
    }

    @Test
    public void mssqlDatabaseFactory() throws Exception
    {
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:sqlserver://host/eeee");
        when(dataSource.getDriver()).thenReturn("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);
        assertThat("Database implementation must be Mssql", jiraDatabase, instanceOf(JiraDatabaseMssqlImpl.class));
    }

    @Test
    public void mssqlJtdsDatabaseFactory() throws Exception
    {
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:jtds:sqlserver://localhost:1433/ppppp");
        when(dataSource.getDriver()).thenReturn("net.sourceforge.jtds.jdbc.Driver");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);
        assertThat("Database implementation must be Mssql", jiraDatabase, instanceOf(JiraDatabaseMssqlImpl.class));
    }
}