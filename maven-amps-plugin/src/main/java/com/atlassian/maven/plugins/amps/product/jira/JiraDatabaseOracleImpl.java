package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.product.ImportMethod;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

public class JiraDatabaseOracleImpl extends AbstractJiraDatabase
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
            + "    EXECUTE IMMEDIATE('GRANT CONNECT, RESOURCE TO %s IDENTIFIED BY %s');\n"
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
        return String.format(DROP_AND_CREATE_USER, getDataSource().getUsername(), getDataSource().getUsername(),
                getDataSource().getUsername(), getDataSource().getPassword());
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
        if (ImportMethod.IMPDB.toString().equals(getDataSource().getImportMethod()))
        {
            configDatabaseTool = configuration(
                    element(name("executable"), "impdp"),
                    element(name("arguments"),
                            element(name("argument"), "DUMPFILE=" + getDataSource().getDumpFilePath()),
                            element(name("argument"), "DIRECTORY=" + getDataSource().getUsername())
                    )
            );
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
