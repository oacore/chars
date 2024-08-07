package uk.ac.core.reporting;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "uk.ac.core.reporting.metrics.data.repo")
public class ReportingConfig {
}
