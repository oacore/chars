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
import uk.ac.core.oadiscover.services.epmc.EPMCResponse;
import uk.ac.core.oadiscover.services.epmc.FullTextUrl;
import uk.ac.core.oadiscover.services.epmc.Result;

/**
 *
 * @author lucas
 */
public class EuropePMCCall extends ExternalCall {

    private static final Logger LOG = Logger.getLogger(EuropePMCCall.class.getName());

    public EuropePMCCall(HttpClient httpClient, JdbcTemplate jdbcTemplate, ElasticsearchTemplate elasticSearchTemplate, String doi, long artificialDelay) {
        super(DiscoveryProvider.EPMC, httpClient, jdbcTemplate, elasticSearchTemplate, doi, artificialDelay);
    }

    public EuropePMCCall(HttpClient httpClient, JdbcTemplate jdbcTemplate, ElasticsearchTemplate elasticSearchTemplate, String doi) {
        super(DiscoveryProvider.EPMC, httpClient, jdbcTemplate, elasticSearchTemplate, doi);
    }

    @Override
    public DiscoverySource executeExternalCall() throws Exception {
        LOG.info("EuropePMC: looking for oa links in EuropePMC for doi : " + doi);
        long funcStart = System.currentTimeMillis();

        DiscoverySource discoverySource = null;
        HttpGet request = null;
        try {
            request = new HttpGet("https://www.ebi.ac.uk/europepmc/webservices/rest/search?"
                    + "query=doi:" + doi
                    + "%20AND%20OPEN_ACCESS:Y&resultType=core&format=json");
            HttpResponse response = this.httpClient.execute(request);
            BufferedReader rd;
            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            EPMCResponse ePMCResponse = new Gson().fromJson(rd, EPMCResponse.class);

            if (ePMCResponse != null
                    && ePMCResponse.getHitCount() > 0) {

                if (ePMCResponse.getResultList().getResult() != null
                        && !ePMCResponse.getResultList().getResult().isEmpty()) {

                    Result result = ePMCResponse.getResultList().getResult().get(0);
                    if (result.getHasPDF().equals("Y")) {
                        List<FullTextUrl> fullTextUrls = result.getFullTextUrlList().getFullTextUrl();
                        for (FullTextUrl fullTextUrl : fullTextUrls) {
                            if (fullTextUrl.getDocumentStyle().equals("pdf")) {
                                discoverySource = new DiscoverySource();
                                discoverySource.setLink(fullTextUrl.getUrl());
                                discoverySource.setSource(DiscoveryProvider.EPMC.verbose());
                                discoverySource.setValid(Boolean.TRUE);
                            }
                        }
                    }

                }
            }
        } catch (IOException | UnsupportedOperationException ex) {
            Logger.getLogger(EuropePMCCall.class.getName()).log(Level.SEVERE, null, ex);
        } finally { 
            request.releaseConnection();
        }
        long funcEnd = System.currentTimeMillis();
        long duration = funcEnd - funcStart;
        LOG.info("EuropePMC: returning " + discoverySource + " after " + duration + " ms");
        return discoverySource;
    }

}
