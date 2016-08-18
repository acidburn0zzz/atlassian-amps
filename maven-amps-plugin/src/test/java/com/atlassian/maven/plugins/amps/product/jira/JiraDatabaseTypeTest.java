package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.LibArtifact;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.MSSQL;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.ORACLE_10G;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.ORACLE_12C;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.POSTGRES;
import static com.atlassian.maven.plugins.amps.product.jira.JiraDatabaseType.getDatabaseType;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JiraDatabaseTypeTest {

    private void assertDbType(@Nullable final String url, @Nullable final String driverClass,
                              @Nullable final String driverArtifactId, @Nullable final JiraDatabaseType expectedDbType) {
        // Set up
        final DataSource dataSource = mockDataSource(url, driverClass, driverArtifactId);

        // Invoke
        final Optional<JiraDatabaseType> dbType = getDatabaseType(dataSource);

        // Check
        assertThat(dbType, is(Optional.ofNullable(expectedDbType)));
    }

    private DataSource mockDataSource(@Nullable final String url, @Nullable final String driverClass, @Nullable final String driverArtifactId) {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getDriver()).thenReturn(driverClass);
        when(dataSource.getUrl()).thenReturn(url);
        if (driverArtifactId != null) {
            final LibArtifact driver = mock(LibArtifact.class);
            when(driver.getArtifactId()).thenReturn(driverArtifactId);
            when(dataSource.getLibArtifacts()).thenReturn(singletonList(driver));
        }
        return dataSource;
    }

    @Test
    public void shouldReturnEmptyForNullUrlAndDriver()
    {
        assertDbType(null, null, null, null);
    }

    @Test
    public void shouldRecognisePostgresUriAndDriver()
    {
        assertDbType("jdbc:postgresql://localhost:5432/amps-test", "org.postgresql.Driver", null, POSTGRES);
    }

    @Test
    public void shouldRecogniseSqlServerUriAndDriver()
    {
        assertDbType("jdbc:sqlserver://amps-test", "com.microsoft.sqlserver.jdbc.SQLServerDriver", null, MSSQL);
    }

    @Test
    public void shouldRecogniseOracle10gUriAndDriver()
    {
        assertDbType("jdbc:oracle:thin:@localhost:1521:XE", "oracle.jdbc.OracleDriver", null, ORACLE_10G);
    }

    @Test
    public void shouldRecogniseOracle12gArtifactId()
    {
        assertDbType("jdbc:oracle:thin:@localhost:1521:XE", "oracle.jdbc.OracleDriver", "ojdbc7", ORACLE_12C);
    }

    @Test
    public void shouldReturnEmptyForMicrosoftStyleUriWithJtdsDriver()
    {
        assertDbType("jdbc:sqlserver://amps-test;user=MyUserName;password=*****;", "net.sourceforge.jtds.jdbc.Driver", null, null);
    }
}