package uk.ac.core.indexbigrepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.ac.core.database.configuration.DatabaseConfig;
import uk.ac.core.supervisor.client.HttpSupervisorClient;
import uk.ac.core.supervisor.client.SupervisorClient;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@EnableScheduling
@ComponentScan({"uk.ac.core"})@Import({DatabaseConfig.class})
@PropertySource("file:/data/core-properties/chars-components-${spring.profiles.active}.properties")
public class IndexBigRepositoryConfiguration {

    @Value("${supervisor.url}")
    public String SUPERVISOR_URL;

    @Bean
    SupervisorClient supervisorClient() {
        return new HttpSupervisorClient(SUPERVISOR_URL);
    }
}
