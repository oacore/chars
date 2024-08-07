package uk.ac.core.elasticsearch.configuration;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import java.net.InetSocketAddress;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "uk.ac.core.elasticsearch.repositories")
@PropertySource("file:/data/core-properties/elasticsearch-${spring.profiles.active}.properties")
public class ElasticsearchConfiguration {

    @Bean
    ElasticsearchTemplate elasticsearchTemplate(Client client) {
        return new ElasticsearchTemplate(client);
    }

    @Value("${elasticsearch.clustername:'core_cluster'}")
    private String clustername;

    @Value("${elasticsearch.endpoints:'localhost:9300'}")
    private String[] endpoints;

    @Bean
    Client client() {
        Settings settings = Settings.builder()
                .put("cluster.name", clustername)
                //                .put("client.transport.sniff", true)
                .build();

        TransportClient client = new PreBuiltTransportClient(settings);
        for (String endpoint : endpoints) {
            String esTransportAddressHost = endpoint.split(":")[0];
            int esTransportAddressPort = Integer.parseInt(endpoint.split(":")[1]);
            InetSocketAddress inetSocketTransportAddress = new InetSocketAddress(esTransportAddressHost, esTransportAddressPort);
            TransportAddress address = new TransportAddress(inetSocketTransportAddress);
            client.addTransportAddress(address);
        }

        return client;
    }

    // To override, pass the commandline argument --elasticsearch.indexName.articles=articles_2016_11_30
    @Value("${elasticsearch.indexName.articles:'articles_production_current'}")
    private String indexName;

    @Value("${elasticsearch.indexName.works:'works_production_current'}")
    private String worksIndexName;

    @Bean
    public String indexName() {
        return indexName;
    }

    @Bean
    public String worksIndexName() {
        return worksIndexName;
    }
}
