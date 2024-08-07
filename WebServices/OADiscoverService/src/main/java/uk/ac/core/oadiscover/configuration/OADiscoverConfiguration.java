
package uk.ac.core.oadiscover.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.ac.core.dataprovider.logic.repository.elasticsearch.IndexDataProviderRepository;

/**
 * @author mc26486
 */
@Configuration
public class OADiscoverConfiguration {

    private int TIMEOUT = 10;

    @Bean
    public HttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(TIMEOUT * 1000)
                .setConnectTimeout(TIMEOUT * 1000)
                .build();
        HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        return client;
    }

    @Bean
    ExecutorService executorService() {
        return Executors.newFixedThreadPool(20);
    }
}
