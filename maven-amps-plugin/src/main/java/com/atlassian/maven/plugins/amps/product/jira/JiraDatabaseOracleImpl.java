package com.atlassian.maven.plugins.amps.product.jira;

import java.io.File;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.product.ImportMethod;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public class JiraDatabaseOracleImpl extends AbstractJiraDatabase
{

    private static final String DATA_PUMP_DIR = "DATA_PUMP_DIR";
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


    public JiraDatabaseOracleImpl(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    protected String dropDatabase()
    {
        return null;
    }

    @Override
    protected String dropUser()
    {
        return null;
    }

    @Override
    protected String createDatabase()
    {
        return null;
    }

    @Override
    protected String createUser()
    {
        return null;
    }

    private String getDropAndCreateUser()
    {
        final String dumpFileDirectoryPath = (new File(getDataSource().getDumpFilePath())).getParent();
        final String username = getDataSource().getUsername();
        final String dropAndCreateUser = String.format(DROP_AND_CREATE_USER,
                // drop user if exists
                username, username,
                // create user with default tablespace
                username, getDataSource().getPassword(), username
                , DATA_PUMP_DIR, dumpFileDirectoryPath,
                DATA_PUMP_DIR, username
        );
        return dropAndCreateUser;
    }

    @Override
    protected String grantPermissionForUser()
    {
        return null;
    }

    @Override
    protected String getDatabaseName(String url)
    {
        return getDataSource().getSchema();
    }

    @Override
    public Xpp3Dom getConfigDatabaseTool() throws MojoExecutionException
    {
        Xpp3Dom configDatabaseTool = null;
        if (ImportMethod.IMPDP.equals(ImportMethod.getValueOf(getDataSource().getImportMethod())))
        {
            final File dumpFile = new File(getDataSource().getDumpFilePath());
            final File dumpFileDirecotry = dumpFile.getParentFile();
            final String dumpFileName = dumpFile.getName();
            // grant read, write and executable on dump file and parent directory for Oracle to execute import - impdp
            dumpFile.setExecutable(true, false);
            dumpFile.setReadable(true, false);
            dumpFile.setWritable(true, false);
            dumpFileDirecotry.setExecutable(true, false);
            dumpFileDirecotry.setReadable(true, false);
            dumpFileDirecotry.setWritable(true, false);
            configDatabaseTool = configuration(
                    element(name("executable"), "impdp"),
                    element(name("arguments"),
                            element(name("argument"), getDataSource().getUsername() + "/" + getDataSource().getPassword()),
                            element(name("argument"), "DUMPFILE=" + dumpFileName),
                            element(name("argument"), "DIRECTORY=" + DATA_PUMP_DIR)
                    )
            );
        }
        return configDatabaseTool;
    }

    @Override
    public Xpp3Dom getPluginConfiguration()
    {
        // In Oracle database , User and Schema are quite the same concept, create/drop user also create/drop schema.
        String sql = getDropAndCreateUser();
        getLog().info("Oracle initialization database sql: " + sql);
        Xpp3Dom pluginConfiguration = systemDatabaseConfiguration();
        pluginConfiguration.addChild(
                element(name("sqlCommand"), sql).toDom()
        );
        pluginConfiguration.addChild(element(name("delimiter"), "/").toDom());
        pluginConfiguration.addChild(element(name("delimiterType"), "row").toDom());
        return pluginConfiguration;
    }
}
