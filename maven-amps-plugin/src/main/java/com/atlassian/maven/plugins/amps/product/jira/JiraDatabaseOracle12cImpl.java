package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.util.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;

public class JiraDatabaseOracle12cImpl extends AbstractJiraOracleDatabase
{
    public JiraDatabaseOracle12cImpl(final DataSource dataSource)
    {
        super(dataSource);
    }

    protected String getSqlToDropAndCreateUser()
    {
        // This path is only valid if Oracle can read the local filesystem, e.g. is not running in a VM
        final String dumpFileDirectoryPath = (new File(getDataSource().getDumpFilePath())).getParent();
        return FileUtils.readFileToString("oracle12c-template.sql", getClass(), UTF_8)
                .replace("v_data_pump_dir", dumpFileDirectoryPath)
                .replace("v_jira_user", getDataSource().getUsername())
                .replace("v_jira_pwd", getDataSource().getPassword());
    }

    @Nonnull
    @Override
    protected Map<String, String> getDriverProperties() {
        // See http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-faq-090281.html#05_11
        return singletonMap("internal_logon", "SYSDBA");
    }
}
