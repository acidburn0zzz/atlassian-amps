package com.atlassian.maven.plugins.amps.product.jira;

import java.io.File;
import java.util.Optional;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.product.ImportMethod;

import org.junit.Before;
import org.junit.Test;

import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseFactory.getJiraDatabaseFactory;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    public void mssqlJtdsDatabaseName() throws Exception
    {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getDriver()).thenReturn("net.sourceforge.jtds.jdbc.Driver");
        final JiraDatabaseMssqlImpl mssql = new JiraDatabaseMssqlImpl(dataSource);
        assertThat("database name should be : ddd", mssql.getDatabaseName("jdbc:jtds:sqlserver://localhost:1433/ddd"), equalTo("ddd"));
        assertThat("database name should be : eeee", mssql.getDatabaseName("jdbc:jtds:sybase://127.0.0.1/eeee;autoCommit=false"), equalTo("eeee"));
    }

    @Test
    public void mssqlDatabaseName() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getDriver()).thenReturn("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        final JiraDatabaseMssqlImpl mssql = new JiraDatabaseMssqlImpl(dataSource);
        assertThat("database name should be : ddd", mssql.getDatabaseName("jdbc:sqlserver://localhost:1433;databaseName=ddd"), equalTo("ddd"));
        assertThat("database name should be : eeee", mssql.getDatabaseName("jdbc:sqlserver://127.0.0.1;databaseName=eeee;autoCommit=false"), equalTo("eeee"));
    }

    @Test
    public void postgresDatabaseFactory() {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:postgresql://host/eeee");
        when(dataSource.getDriver()).thenReturn("org.postgresql.Driver");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);
        assertThat("Database implementation must be Postgres", jiraDatabase, instanceOf(JiraDatabasePostgresImpl.class));
    }

    @Test
    public void mysqlDatabaseFactory()
    {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:mysql://localhost:3306/ffffff");
        when(dataSource.getDriver()).thenReturn("com.mysql.jdbc.Driver");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);
        assertThat("Database implementation must be Mysql", jiraDatabase, instanceOf(JiraDatabaseMysqlImpl.class));
    }

    @Test
    public void oracle10gDatabaseFactory()
    {
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:oracle:thin:@localhost:1521:XE");
        when(dataSource.getDriver()).thenReturn("oracle.jdbc.OracleDriver");
        when(dataSource.getJdbcMetaData(any())).thenReturn(Optional.empty());
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);
        assertThat("Database implementation must be Oracle 10g", jiraDatabase, instanceOf(JiraDatabaseOracle10gImpl.class));
    }

    @Test
    public void mssqlDatabaseFactory()
    {
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:sqlserver://host/eeee");
        when(dataSource.getDriver()).thenReturn("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);
        assertThat("Database implementation must be Mssql", jiraDatabase, instanceOf(JiraDatabaseMssqlImpl.class));
    }

    @Test
    public void mssqlJtdsDatabaseFactory()
    {
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:jtds:sqlserver://localhost:1433/ppppp");
        when(dataSource.getDriver()).thenReturn("net.sourceforge.jtds.jdbc.Driver");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);
        assertThat("Database implementation must be Mssql", jiraDatabase, instanceOf(JiraDatabaseMssqlImpl.class));
    }

    @Test
    public void mssqlGenerateImportDatabaseSQL() throws Exception
    {
        // setup
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        final String expectedSQLGenerated = "RESTORE DATABASE [jiradb] FROM DISK='jira_63_mssql_dump.bak' WITH REPLACE;";
        when(dataSource.getUrl()).thenReturn("jdbc:jtds:sqlserver://localhost:1433/jiradb");
        when(dataSource.getUsername()).thenReturn("jira_user");
        when(dataSource.getPassword()).thenReturn("jira_pwd");
        when(dataSource.getDriver()).thenReturn("net.sourceforge.jtds.jdbc.Driver");
        when(dataSource.getImportMethod()).thenReturn("SQLCMD");
        when(dataSource.getDumpFilePath()).thenReturn("jira_63_mssql_dump.bak");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);

        // execute
        final String configDatabaseSQL = jiraDatabase.getConfigDatabaseTool().getChild("arguments").getChild(3).getValue();

        // assert
        assertThat("Generated SQL should be: " + expectedSQLGenerated, configDatabaseSQL, containsString(expectedSQLGenerated));
    }

    @Test
    public void postgresGenerateInitDatabaseSQL() throws Exception
    {
        // expected result
        final String expectedSQLGenerated = "DROP DATABASE IF EXISTS \"jiradb\";"
                + "DROP USER IF EXISTS \"jira_user\";"
                + "CREATE DATABASE \"jiradb\";"
                + "CREATE USER \"jira_user\" WITH PASSWORD 'jira_pwd' ;"
                + "ALTER ROLE \"jira_user\" superuser; "
                + "ALTER DATABASE \"jiradb\" OWNER TO \"jira_user\";";

        // setup
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:postgresql://localhost:5432/jiradb");
        when(dataSource.getUsername()).thenReturn("jira_user");
        when(dataSource.getPassword()).thenReturn("jira_pwd");
        when(dataSource.getDriver()).thenReturn("org.postgresql.Driver");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);

        // execute
        final String initDatabaseSQL = jiraDatabase.getPluginConfiguration().getChild("sqlCommand").getValue();

        // assert
        assertThat("Generated SQL should be: " + expectedSQLGenerated, initDatabaseSQL, containsString(expectedSQLGenerated));
    }

    @Test
    public void mssqlGenerateInitDatabaseSQL() throws Exception
    {
        // expected result
        final String expectedSQLGenerated = "USE [master]; \n"
                + "IF EXISTS(SELECT * FROM SYS.DATABASES WHERE name='jiradb') \n"
                + "DROP DATABASE [jiradb];\n"
                + "USE [master]; \n"
                + "IF EXISTS(SELECT * FROM SYS.SERVER_PRINCIPALS WHERE name = 'jira_user') \n"
                + "DROP LOGIN jira_user; \n"
                + "USE [master]; \n"
                + " CREATE DATABASE [jiradb]; \n"
                + "USE [master]; \n"
                + " CREATE LOGIN jira_user WITH PASSWORD = 'jira_pwd'; \n"
                + "USE [jiradb];\n"
                + "CREATE USER jira_user FROM LOGIN jira_user; \n"
                + "EXEC SP_ADDROLEMEMBER 'DB_OWNER', 'jira_user'; \n"
                + "ALTER LOGIN jira_user WITH DEFAULT_DATABASE = [jiradb]; ";
        // setup
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUsername()).thenReturn("jira_user");
        when(dataSource.getPassword()).thenReturn("jira_pwd");
        when(dataSource.getDriver()).thenReturn("net.sourceforge.jtds.jdbc.Driver");
        when(dataSource.getUrl()).thenReturn("jdbc:jtds:sqlserver://localhost:1433/jiradb");
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);

        // execute
        final String initDatabaseSQL = jiraDatabase.getPluginConfiguration().getChild("sqlCommand").getValue();

        // assert
        assertThat("Generated SQL should be: " + expectedSQLGenerated, initDatabaseSQL, containsString(expectedSQLGenerated));
    }

    @Test
    public void mysqlGenerateInitDatabaseSQL() throws Exception
    {
        // expected result
        final String expectedSQLGenerated = "DROP DATABASE IF EXISTS `jiradb`;\n"
                + "GRANT USAGE ON *.* TO `jira_user`@localhost;\n"
                + "DROP USER `jira_user`@localhost;\n"
                + "CREATE DATABASE `jiradb` CHARACTER SET utf8 COLLATE utf8_bin;\n"
                + "CREATE USER `jira_user`@localhost IDENTIFIED BY 'jira_pwd';\n"
                + "GRANT ALL ON `jiradb`.* TO `jira_user`@localhost;";
        // setup
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:mysql://localhost:3307/jiradb");
        when(dataSource.getUsername()).thenReturn("jira_user");
        when(dataSource.getPassword()).thenReturn("jira_pwd");
        when(dataSource.getDriver()).thenReturn("com.mysql.jdbc.Driver");

        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);

        // execute
        final String initDatabaseSQL = jiraDatabase.getPluginConfiguration().getChild("sqlCommand").getValue();

        // assert
        assertThat("Generated SQL should be: " + expectedSQLGenerated, initDatabaseSQL, containsString(expectedSQLGenerated));
    }

    @Test
    public void oracleGenerateInitDatabaseSQL() throws Exception
    {
        // expected result
        final String dataPumpDir = File.separatorChar + "usr" + File.separatorChar + "home";
        String expectedSQLGenerated = "DECLARE\n"
                + "    v_count INTEGER := 0;\n"
                + "BEGIN\n"
                + "    SELECT COUNT (1) INTO v_count FROM dba_users WHERE username = UPPER ('jira_user'); \n"
                + "    IF v_count != 0\n"
                + "    THEN\n"
                + "        EXECUTE IMMEDIATE('DROP USER jira_user CASCADE');\n"
                + "    END IF;\n"
                + "    v_count := 0; \n"
                + "    SELECT COUNT (1) INTO v_count FROM dba_tablespaces WHERE tablespace_name = UPPER('jiradb2'); \n"
                + "    IF v_count != 0\n"
                + "    THEN\n"
                + "        EXECUTE IMMEDIATE('DROP TABLESPACE jiradb2 INCLUDING CONTENTS AND DATAFILES');\n"
                + "    END IF;\n"
                + "    EXECUTE IMMEDIATE(q'{CREATE TABLESPACE jiradb2 DATAFILE '/tmp/jiradb2.dbf' SIZE 32m AUTOEXTEND ON NEXT 32m MAXSIZE 4096m EXTENT MANAGEMENT LOCAL}');\n"
                + "    EXECUTE IMMEDIATE('CREATE USER jira_user IDENTIFIED BY jira_pwd DEFAULT TABLESPACE jiradb2 QUOTA UNLIMITED ON jiradb2');\n"
                + "    EXECUTE IMMEDIATE('GRANT CONNECT, RESOURCE, IMP_FULL_DATABASE TO jira_user');\n"
                + "    EXECUTE IMMEDIATE(q'{CREATE OR REPLACE DIRECTORY DATA_PUMP_DIR AS '%s'}');\n"
                + "    EXECUTE IMMEDIATE('GRANT READ, WRITE ON DIRECTORY DATA_PUMP_DIR TO jira_user');\n"
                + "END;\n"
                + "/";
        expectedSQLGenerated = String.format(expectedSQLGenerated, dataPumpDir);
        // setup
        final JiraDatabaseFactory factory = getJiraDatabaseFactory();
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getUrl()).thenReturn("jdbc:oracle:thin:@localhost:1521:XE");
        when(dataSource.getUsername()).thenReturn("jira_user");
        when(dataSource.getPassword()).thenReturn("jira_pwd");
        when(dataSource.getDriver()).thenReturn("oracle.jdbc.OracleDriver");
        when(dataSource.getDumpFilePath()).thenReturn("/usr/home/oracle.bak");
        when(dataSource.getJdbcMetaData(any())).thenReturn(Optional.empty());
        final JiraDatabase jiraDatabase = factory.getJiraDatabase(dataSource);

        // execute
        final String initDatabaseSQL = jiraDatabase.getPluginConfiguration().getChild("sqlCommand").getValue();

        // assert
        assertThat("Generated SQL should be: " + expectedSQLGenerated, initDatabaseSQL, containsString(expectedSQLGenerated));
    }


    @Test
    public void testImportMethodCaseInsensitively()
    {
        final ImportMethod IMPDP = ImportMethod.getValueOf("IMPDP");
        final ImportMethod impdp = ImportMethod.getValueOf("impdp");
        final ImportMethod iMpdp = ImportMethod.getValueOf("iMpdp");
        assertThat("Import method should be IMPDP", IMPDP, equalTo(ImportMethod.IMPDP));
        assertThat("Import method should be IMPDP", impdp, equalTo(ImportMethod.IMPDP));
        assertThat("Import method should be IMPDP", iMpdp, equalTo(ImportMethod.IMPDP));
    }
}