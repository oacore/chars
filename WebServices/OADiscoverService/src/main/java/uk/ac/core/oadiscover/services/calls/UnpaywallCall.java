package uk.ac.core.oadiscover.services.calls;

import com.example.UnpaywallResponse;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.core.oadiscover.model.DiscoveryProvider;
import uk.ac.core.oadiscover.model.DiscoverySource;

/**
 *
 * @author lucas
 */
public class UnpaywallCall extends ExternalCall {

    private static final Logger LOG = Logger.getLogger(UnpaywallCall.class.getName());

    private long artificialDelay = 0;

    public UnpaywallCall(HttpClient httpClient, JdbcTemplate jdbcTemplate, ElasticsearchTemplate elasticSearchTemplate, String doi, long artificialDelay) {
        super(DiscoveryProvider.UNPAYWALL, httpClient, jdbcTemplate, elasticSearchTemplate, doi, artificialDelay);
    }

    public UnpaywallCall(HttpClient httpClient, JdbcTemplate jdbcTemplate, ElasticsearchTemplate elasticSearchTemplate, String doi) {
        super(DiscoveryProvider.UNPAYWALL, httpClient, jdbcTemplate, elasticSearchTemplate, doi);
    }

    @Override
    public DiscoverySource executeExternalCall() throws Exception {
        LOG.info("Unpaywall: looking for oa links in unpaywall for doi : " + doi);
        long funcStart = System.currentTimeMillis();

        if (artificialDelay > 0) {
            Thread.currentThread().sleep(artificialDelay);
        }

        DiscoverySource discoverySource = null;
        HttpGet request = null;
        try {
            String requestUrl = "http://api.unpaywall.org/v2/" + doi + "?email=theteam@core.ac.uk";
            request = new HttpGet(requestUrl);

            HttpResponse response = null;
            try {
                response = this.httpClient.execute(request);
            } catch (IOException ex) {
                Logger.getLogger(UnpaywallCall.class.getName()).log(Level.SEVERE, null, ex);
            }

            BufferedReader rd;
            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            UnpaywallResponse unpaywallResponse = new Gson().fromJson(rd, UnpaywallResponse.class);

            if (unpaywallResponse != null
                    && unpaywallResponse.getBestOaLocation() != null) {
                discoverySource = new DiscoverySource();
                // prefer 'url for pdf' over 'url for landing page'
                if (unpaywallResponse.getBestOaLocation().getUrlForPdf() != null
                        && !unpaywallResponse.getBestOaLocation().getUrlForPdf().isEmpty()) {
                    discoverySource.setLink(unpaywallResponse.getBestOaLocation().getUrlForPdf());
                } else if (unpaywallResponse.getBestOaLocation().getUrlForLandingPage() != null
                        && !unpaywallResponse.getBestOaLocation().getUrlForLandingPage().isEmpty()) {
                    discoverySource.setLink(unpaywallResponse.getBestOaLocation().getUrlForLandingPage());
                }
                discoverySource.setSource(DiscoveryProvider.UNPAYWALL.verbose());
                discoverySource.setValid(Boolean.TRUE);
            }
        } catch (IOException | UnsupportedOperationException ex) {
            Logger.getLogger(UnpaywallCall.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            request.releaseConnection();
        }

        long funcEnd = System.currentTimeMillis();
        long duration = funcEnd - funcStart;
        LOG.info("Unpaywall: returning " + discoverySource + " after " + duration + " ms");

        return discoverySource;
    }

}
