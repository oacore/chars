package uk.ac.core.reindexinvoke;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.ac.core.supervisor.client.HttpSupervisorClient;
import uk.ac.core.supervisor.client.SupervisorClient;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@EnableScheduling
@ComponentScan({"uk.ac.core"})
@PropertySource("file:/data/core-properties/chars-components-${spring.profiles.active}.properties")
public class InvokeReindexConfiguration {

    @Value("${supervisor.url}")
    public String SUPERVISOR_URL;

    @Bean
    SupervisorClient supervisorClient() {
        return new HttpSupervisorClient(SUPERVISOR_URL);
    }
    
    @Bean
    DatabaseQuery createDatabaseQuery(JdbcTemplate jdbcTemplate) {
        return new DatabaseQuery(jdbcTemplate);
    }
}
