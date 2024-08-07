package uk.ac.core.dataprovider.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.ac.core.dataprovider.api.service.DedupClient;
import uk.ac.core.supervisor.client.HttpSupervisorClient;
import uk.ac.core.supervisor.client.SupervisorClient;

@Configuration
@EnableElasticsearchRepositories(basePackages = {
        "uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.elastic",
        "uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.elastic"})
@PropertySource("file:/data/core-properties/elasticsearch-${spring.profiles.active}.properties")
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://core.ac.uk")
                        .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
                        .allowedHeaders("Content-Type");
            }
        };
    }

    @Value("${supervisor.url}")
    String supervisorUrl;

    @Bean
    public SupervisorClient supervisorClient() {
        return new HttpSupervisorClient(supervisorUrl);
    }

    @Value("${deduplication.url}")
    String deduplicationUrl;

    @Bean
    public DedupClient dedupClient() {
        return new DedupClient(deduplicationUrl);
    }
}
