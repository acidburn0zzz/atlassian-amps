package io.atlassian.scheduler.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * General spring configuration.
 */
@Configuration
@ComponentScan (basePackages = "io.atlassian.scheduler.db")
public class AppConfig
{
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    private HikariDataSource dataSource;
    private String dbURL;

    @Bean (destroyMethod = "close")
    public DataSource dataSource(
            @Value ("#{systemEnvironment['PG_SCHEDULER_SPIKE_ROLE']}") final String dbUser,
            @Value ("#{systemEnvironment['PG_SCHEDULER_SPIKE_PASSWORD']}") final String dbPassword,
            @Value ("#{systemEnvironment['PG_SCHEDULER_SPIKE_HOST']}") final String dbHost,
            @Value ("#{systemEnvironment['PG_SCHEDULER_SPIKE_PORT']}") final int dbPort,
            @Value ("#{systemEnvironment['PG_SCHEDULER_SPIKE_SCHEMA']}") final String dbSchema)
    {
        dbURL = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbSchema;
        log.debug("DB URL: " + dbURL);
        dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(dbURL);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName("org.postgresql.Driver");

        return dataSource;
    }

    @Bean
    public JdbcPooledConnectionSource connectionSource(
            @Value ("#{systemEnvironment['PG_SCHEDULER_SPIKE_ROLE']}") final String dbUser,
            @Value ("#{systemEnvironment['PG_SCHEDULER_SPIKE_PASSWORD']}") final String dbPassword,
            @Value ("#{systemEnvironment['PG_SCHEDULER_SPIKE_HOST']}") final String dbHost,
            @Value ("#{systemEnvironment['PG_SCHEDULER_SPIKE_PORT']}") final int dbPort,
            @Value ("#{systemEnvironment['PG_SCHEDULER_SPIKE_SCHEMA']}") final String dbSchema)
            throws SQLException
    {
        JdbcPooledConnectionSource jdbcPooledConnectionSource =
                new JdbcPooledConnectionSource("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbSchema, dbUser, dbPassword);
        jdbcPooledConnectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
        return jdbcPooledConnectionSource;
    }
}
