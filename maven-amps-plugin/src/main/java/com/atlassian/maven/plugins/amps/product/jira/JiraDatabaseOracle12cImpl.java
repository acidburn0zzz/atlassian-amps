package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.toFile;

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
        final URL fileUrl = getClass().getResource(name);
        final File file = toFile(fileUrl);
        try {
            return FileUtils.readFileToString(file, UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
