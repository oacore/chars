package uk.ac.core.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "uk.ac.core.audit")
public class AuditLogConfig {

}
