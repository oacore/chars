package uk.ac.core.metadata_download_failure_diagnosis_tool.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.ac.core.supervisor.client.HttpSupervisorClient;
import uk.ac.core.supervisor.client.SupervisorClient;

@Configuration
@PropertySource("file:/data/core-properties/chars-components-${spring.profiles.active}.properties")
public class FailureDiagnosisToolConfiguration {
    @Value("${supervisor.url}")
    public String SUPERVISOR_URL;

    @Bean
    SupervisorClient supervisorClient() {
        return new HttpSupervisorClient(SUPERVISOR_URL);
    }
}
