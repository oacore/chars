package uk.ac.core.oadiscover.services.calls;

import java.util.concurrent.Callable;
import java.util.logging.Logger;
import org.apache.http.client.HttpClient;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.core.oadiscover.model.DiscoveryProvider;
import uk.ac.core.oadiscover.model.DiscoverySource;

/**
 *
 * @author lucas
 */
public class KopernioCall extends ExternalCall {

    private static final Logger LOG = Logger.getLogger(KopernioCall.class.getName());

    public KopernioCall(HttpClient httpClient, JdbcTemplate jdbcTemplate, ElasticsearchTemplate elasticSearchTemplate, String doi, long artificialDelay) {
        super(DiscoveryProvider.KOPERNIO,httpClient, jdbcTemplate, elasticSearchTemplate,  doi, artificialDelay);
    }

    public KopernioCall(HttpClient httpClient, JdbcTemplate jdbcTemplate, ElasticsearchTemplate elasticSearchTemplate, String doi) {
        super(DiscoveryProvider.KOPERNIO,httpClient, jdbcTemplate, elasticSearchTemplate, doi);
    }



    @Override
    public DiscoverySource executeExternalCall() throws Exception {
        // kopernio has authenticated API
        // http://canaryhaz.com/api/v1/resolve/ [DOI] 
        return null;
    }

}
