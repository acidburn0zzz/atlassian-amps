package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.util.FileUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JiraDatabaseOracle12cImpl extends AbstractJiraOracleDatabase {

    public JiraDatabaseOracle12cImpl(final DataSource dataSource) {
        super(dataSource);
    }

    protected String getSqlToDropAndCreateUser() {
        if (oracleInStandaloneMode()) {
            // Use the "classic mode" user management query; does not require SYSDBA privilege
            return new JiraDatabaseOracle10gImpl(getDataSource()).getSqlToDropAndCreateUser();
        }
        return getTenantedModeDropAndCreateUserQuery();
    }

    /**
     * Returns the "drop and create user" query for use in CDB (tenanted) mode. Because this query
     * requires the SYSDBA privilege, the AMPS datasource must be configured with a <code>systemUsername</code>
     * that has that privilege, e.g. <code>SYS AS SYSDBA</code>.
     *
     * @return see above
     */
    private String getTenantedModeDropAndCreateUserQuery() {
        // Note that because Oracle tries to resolve this dump file path relative to its own
        // file system, the data import will only work if AMPS and Oracle are running on the
        // same machine (or the path is valid on both machines by virtue of file sharing).
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
}
