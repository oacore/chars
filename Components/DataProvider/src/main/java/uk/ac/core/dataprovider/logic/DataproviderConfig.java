package uk.ac.core.dataprovider.logic;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.ac.core.dataprovider.logic.repository.elasticsearch.IndexDataProviderRepository;

@Configuration
@EnableJpaRepositories(
        excludeFilters =
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = IndexDataProviderRepository.class),
        basePackages = "uk.ac.core.dataprovider.logic.repository")
@EnableElasticsearchRepositories("uk.ac.core.dataprovider.logic.repository.elasticsearch")
public class DataproviderConfig {

}
