package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JiraDatabaseOracle12cImplTest {

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private JiraDatabaseOracle12cImpl jiraDatabaseOracle12c;

    @Test
    public void shouldGenerateCorrectDropAndCreateUserSql() {
        // Set up
        final File dumpFile = FileUtils.getFile("path", "to", "dump_file.dmp");
        when(dataSource.getDumpFilePath()).thenReturn(dumpFile.getPath());
        when(dataSource.getPassword()).thenReturn("the_password");
        when(dataSource.getUsername()).thenReturn("the_username");

        // Invoke
        final String sqlToDropAndCreateUser = jiraDatabaseOracle12c.getSqlToDropAndCreateUser();

        // Check
        assertThat(sqlToDropAndCreateUser, is(
                "-- To run this script, you need to connect using the username \"SYS AS SYSDBA\"\n" +
                "DECLARE\n" +
                "  v_count INTEGER := 0;\n" +
                "  v_sid VARCHAR2(20);\n" +
                "BEGIN\n" +
                "  SELECT SYS_CONTEXT('userenv','instance_name') INTO v_sid FROM DUAL;\n" +
                "\n" +
                "  -- Ensure we're in the root container\n" +
                "  EXECUTE IMMEDIATE 'ALTER SESSION SET CONTAINER=CDB$ROOT';\n" +
                "\n" +
                "  -- Configure the Data Pump directory\n" +
                "  EXECUTE IMMEDIATE q'{CREATE OR REPLACE DIRECTORY DATA_PUMP_DIR AS 'path/to'}';\n" +
                "\n" +
                "  -- Does the JIRA pluggable DB exist?\n" +
                "  SELECT COUNT (1) INTO v_count FROM cdb_pdbs WHERE pdb_name = 'JIRA_PDB';\n" +
                "  IF v_count > 0\n" +
                "  THEN\n" +
                "    -- Yes, close and drop it\n" +
                "    EXECUTE IMMEDIATE 'ALTER PLUGGABLE DATABASE JIRA_PDB CLOSE';\n" +
                "    EXECUTE IMMEDIATE 'DROP PLUGGABLE DATABASE JIRA_PDB INCLUDING DATAFILES';\n" +
                "  END IF;\n" +
                "\n" +
                "  -- [Re]create the JIRA pluggable DB, switch to it, and open it\n" +
                "  EXECUTE IMMEDIATE 'CREATE PLUGGABLE DATABASE JIRA_PDB ' ||\n" +
                "                    'ADMIN USER jira_dba IDENTIFIED BY jira_dba ' ||\n" +
                "                    'FILE_NAME_CONVERT = (''/u01/app/oracle/oradata/' || v_sid || '/pdbseed/'',''/u01/app/oracle/oradata/' || v_sid || '/JIRA_PDB/'')';\n" +
                "  EXECUTE IMMEDIATE 'ALTER SESSION SET CONTAINER=JIRA_PDB';\n" +
                "  EXECUTE IMMEDIATE 'ALTER PLUGGABLE DATABASE OPEN';\n" +
                "\n" +
                "  -- Create the JIRA user/schema in the JIRA DB\n" +
                "  EXECUTE IMMEDIATE 'CREATE USER the_username IDENTIFIED BY the_password';\n" +
                "  EXECUTE IMMEDIATE 'GRANT CONNECT, RESOURCE, IMP_FULL_DATABASE TO the_username';\n" +
                "  EXECUTE IMMEDIATE 'GRANT READ, WRITE ON DIRECTORY DATA_PUMP_DIR TO the_username';\n" +
                "END;"));
    }
}