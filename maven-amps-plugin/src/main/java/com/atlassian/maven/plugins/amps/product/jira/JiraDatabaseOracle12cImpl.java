package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

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
        return readFileToString("oracle12c-template.sql")
                .replace("v_data_pump_dir", dumpFileDirectoryPath)
                .replace("v_jira_user", getDataSource().getUsername())
                .replace("v_jira_pwd", getDataSource().getPassword());
    }

    private String readFileToString(final String name) {
        final InputStream fileStream = getClass().getResourceAsStream(name);
        requireNonNull(fileStream, format("Could not find '%s' on classpath of %s", name, getClass().getName()));
        try {
            return IOUtils.toString(fileStream, UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
