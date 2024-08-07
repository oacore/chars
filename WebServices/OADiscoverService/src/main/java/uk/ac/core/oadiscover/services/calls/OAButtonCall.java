package uk.ac.core.oadiscover.services.calls;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.core.oadiscover.model.DiscoveryProvider;
import uk.ac.core.oadiscover.model.DiscoverySource;
import uk.ac.core.oadiscover.services.oabutton.Availability;
import uk.ac.core.oadiscover.services.oabutton.OAButtonResponse;

/**
 *
 * @author lucas
 */
public class OAButtonCall extends ExternalCall{

    private static final Logger LOG = Logger.getLogger(OAButtonCall.class.getName());

    public OAButtonCall(HttpClient httpClient, JdbcTemplate jdbcTemplate, ElasticsearchTemplate elasticSearchTemplate, String doi, long artificialDelay) {
        super(DiscoveryProvider.OABUTTON, httpClient, jdbcTemplate, elasticSearchTemplate, doi, artificialDelay);
    }

    public OAButtonCall(HttpClient httpClient, JdbcTemplate jdbcTemplate, ElasticsearchTemplate elasticSearchTemplate, String doi) {
        super(DiscoveryProvider.OABUTTON, httpClient, jdbcTemplate, elasticSearchTemplate, doi);
    }    
    
    @Override
    public DiscoverySource executeExternalCall() throws Exception {
        LOG.info("OAButton: looking for oa links in OAButton for doi : " + doi);
        long funcStart = System.currentTimeMillis();

        DiscoverySource discoverySource = null;
        HttpGet request = null;
        try {
            request = new HttpGet("https://api.openaccessbutton.org/availability?doi=" + doi);
            HttpResponse response = this.httpClient.execute(request);
            BufferedReader rd;
            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            OAButtonResponse oAButtonResponse = new Gson().fromJson(rd, OAButtonResponse.class);
            
            if (oAButtonResponse != null
                    && oAButtonResponse.getData() != null
                    && oAButtonResponse.getData().getAvailability() != null
                    && !oAButtonResponse.getData().getAvailability().isEmpty()) {
                List<Availability> availableVersions = oAButtonResponse.getData().getAvailability();

                for (Availability availableVersion : availableVersions) {
                    if (availableVersion.getUrl() != null
                            && !availableVersion.getUrl().isEmpty()) {
                        discoverySource = new DiscoverySource();
                        discoverySource.setLink(availableVersion.getUrl());
                        discoverySource.setSource(DiscoveryProvider.OABUTTON.verbose());
                        discoverySource.setValid(Boolean.TRUE);
                    }
                }
            }
        } catch (IOException | UnsupportedOperationException ex) {
            Logger.getLogger(OAButtonCall.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            request.releaseConnection();
        }
        long funcEnd = System.currentTimeMillis();
        long duration = funcEnd - funcStart;
        LOG.info("OAButton: returning " + discoverySource + " after " + duration + " ms");
        return discoverySource;
    }

}
