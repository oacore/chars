package uk.ac.core.services.web.affiliations.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.grobid.core.engines.Engine;
import org.grobid.core.factory.GrobidFactory;
import org.grobid.core.main.GrobidHomeFinder;
import org.grobid.core.utilities.GrobidProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
@ComponentScan("uk.ac.core")
public class AffiliationsConfiguration {
    private static final String GROBID_HOME = "/data/grobid/grobid-home/";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean("jsonObjectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper(new JsonFactory());
    }

    @Bean
    public HttpEntity entity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        return new HttpEntity(headers);
    }

    @Bean
    public Engine engine() {
        System.out.println("Initialising GROBID engine");
        GrobidHomeFinder finder = new GrobidHomeFinder(Collections.singletonList(GROBID_HOME));
        GrobidProperties.getInstance(finder);
        return GrobidFactory.getInstance().createEngine();
    }

}
