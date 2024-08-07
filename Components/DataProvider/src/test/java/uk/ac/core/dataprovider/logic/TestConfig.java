package uk.ac.core.dataprovider.logic;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EntityScan("uk.ac.core.dataprovider.logic.entity")
@EnableJpaRepositories("uk.ac.core.dataprovider.logic.repository.dataprovider")
public class TestConfig {

}