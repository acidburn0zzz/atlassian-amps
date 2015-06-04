package ${package};

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * General spring configuration.
 */
@Configuration
public class AppConfig
{
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
}
