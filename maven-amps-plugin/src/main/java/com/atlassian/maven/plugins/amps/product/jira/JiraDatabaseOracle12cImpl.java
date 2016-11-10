package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.util.FileUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;

public class JiraDatabaseOracle12cImpl extends AbstractJiraOracleDatabase {

    public JiraDatabaseOracle12cImpl(final DataSource dataSource) {
        super(dataSource);
    }

    protected String getSqlToDropAndCreateUser() {
        if (oracleInStandaloneMode()) {
            // Use the "classic mode" user management query
            return new JiraDatabaseOracle10gImpl(getDataSource()).getSqlToDropAndCreateUser();
        }
        return getTenantedModeDropAndCreateUserQuery();
    }

    private String getTenantedModeDropAndCreateUserQuery() {
        // Note to people running Oracle on another machine or in a VM:
        // Oracle will try to resolve this dump file path relative to its own filesystem.
        final String dumpFileDirectoryPath = (new File(getDataSource().getDumpFilePath())).getParent();
        return FileUtils.readFileToString("oracle12c-template.sql", getClass(), UTF_8)
                .replace("v_data_pump_dir", dumpFileDirectoryPath)
                .replace("v_jira_user", getDataSource().getUsername())
                .replace("v_jira_pwd", getDataSource().getPassword());
    }

    /**
     * Indicates whether Oracle 12c is running in standalone mode, the non-tenanted mode in which 10g and 11g run.
     *
     * @return <code>false</code> if Oracle is running in single or multi tenant mode
     */
    private boolean oracleInStandaloneMode() {
        final JdbcOperations jdbcOperations = new JdbcTemplate(getDataSource().getJdbcDataSource());
        final String isCdb = jdbcOperations.queryForObject("select cdb from v$database", String.class);
        return isCdb == null || isCdb.toLowerCase().startsWith("n");
    }

    @Nonnull
    @Override
    protected Map<String, String> getDriverProperties() {
        // See http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-faq-090281.html#05_11
        return singletonMap("internal_logon", "SYSDBA");
    }
}
