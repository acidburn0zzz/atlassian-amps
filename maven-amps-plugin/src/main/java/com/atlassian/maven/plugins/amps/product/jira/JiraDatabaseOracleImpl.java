package com.atlassian.maven.plugins.amps.product.jira;

import java.io.File;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.product.ImportMethod;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public class JiraDatabaseOracleImpl extends AbstractJiraDatabase
{

    private static final Logger LOG = LoggerFactory.getLogger(JiraDatabaseOracleImpl.class);
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
                    + "    EXECUTE IMMEDIATE('GRANT CONNECT, RESOURCE, IMP_FULL_DATABASE TO %s IDENTIFIED BY %s');\n"
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
                username, username,
                username, getDataSource().getPassword()
                , DATA_PUMP_DIR, dumpFileDirectoryPath,
                DATA_PUMP_DIR, username
        );
        LOG.info("Oracle drop and create user sql: " + dropAndCreateUser);
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
        LOG.info("Oracle import method: " + getDataSource().getImportMethod());
        if (ImportMethod.IMPDP.toString().equals(getDataSource().getImportMethod()))
        {
            final File dumpFile = new File(getDataSource().getDumpFilePath());
            final File dumpFileDirecotry = dumpFile.getParentFile();
            final String dumpFileName = dumpFile.getName();
            // grant executable on dump file for Oracle user to execute import
            dumpFile.setExecutable(true, false);
            dumpFileDirecotry.setExecutable(true, false);
            configDatabaseTool = configuration(
                    element(name("executable"), "impdp"),
                    element(name("arguments"),
                            element(name("argument"), getDataSource().getUsername() + "/" + getDataSource().getPassword()),
//                            element(name("argument"), getDataSource().getSystemUsername() + "/" + getDataSource().getSystemPassword()),
                            element(name("argument"), "DUMPFILE=" + dumpFileName),
                            element(name("argument"), "DIRECTORY=" + DATA_PUMP_DIR)
                    )
            );
            LOG.info("Configuration Oracle DB tool: " + configDatabaseTool);
        }
        return configDatabaseTool;
    }

    @Override
    public Xpp3Dom getPluginConfiguration()
    {
        // In oralce, user and schema is quite the same concept, create/drop user also create/drop schema.
        String sql = getDropAndCreateUser();
        Xpp3Dom pluginConfiguration = systemDatabaseConfiguration();
        pluginConfiguration.addChild(
                element(name("sqlCommand"), sql).toDom()
        );
        pluginConfiguration.addChild(element(name("delimiter"), "/").toDom());
        pluginConfiguration.addChild(element(name("delimiterType"), "row").toDom());
        return pluginConfiguration;
    }
}
