package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import java.io.File;

public class JiraDatabaseOracle10gImpl extends AbstractJiraOracleDatabase
{
    private static final String DROP_AND_CREATE_USER =
            "DECLARE\n"
                    + "    v_count INTEGER := 0;\n"
                    + "BEGIN\n"
                    + "    SELECT COUNT (1) INTO v_count FROM dba_users WHERE username = UPPER ('%s'); \n"
                    + "    IF v_count != 0\n"
                    + "    THEN\n"
                    + "        EXECUTE IMMEDIATE('DROP USER %s CASCADE');\n"
                    + "    END IF;\n"
                    + "    v_count := 0; \n"
                    + "    SELECT COUNT (1) INTO v_count FROM dba_tablespaces WHERE tablespace_name = UPPER('jiradb2'); \n"
                    + "    IF v_count != 0\n"
                    + "    THEN\n"
                    + "        EXECUTE IMMEDIATE('DROP TABLESPACE jiradb2 INCLUDING CONTENTS AND DATAFILES');\n"
                    + "    END IF;\n"

                    + "    EXECUTE IMMEDIATE(q'{CREATE TABLESPACE jiradb2 DATAFILE '/tmp/jiradb2.dbf' SIZE 32m AUTOEXTEND ON NEXT 32m MAXSIZE 4096m EXTENT MANAGEMENT LOCAL}');\n"
                    + "    EXECUTE IMMEDIATE('CREATE USER %s IDENTIFIED BY %s DEFAULT TABLESPACE jiradb2 QUOTA UNLIMITED ON jiradb2');\n"
                    + "    EXECUTE IMMEDIATE('GRANT CONNECT, RESOURCE, IMP_FULL_DATABASE TO %s');\n"
                    + "    EXECUTE IMMEDIATE(q'{CREATE OR REPLACE DIRECTORY %s AS '%s'}');\n"
                    + "    EXECUTE IMMEDIATE('GRANT READ, WRITE ON DIRECTORY %s TO %s');\n"
                    + "END;\n"
                    + "/";

    public JiraDatabaseOracle10gImpl(final DataSource dataSource)
    {
        super(dataSource);
    }

    protected String getSqlToDropAndCreateUser()
    {
        final String dumpFileDirectoryPath = (new File(getDataSource().getDumpFilePath())).getParent();
        final String username = getDataSource().getUsername();
        return String.format(DROP_AND_CREATE_USER,
                // drop user if exists
                username, username,
                // create user with default tablespace
                username,
                getDataSource().getPassword(),
                username,
                DATA_PUMP_DIR,
                dumpFileDirectoryPath,
                DATA_PUMP_DIR,
                username
        );
    }
}
