package uk.ac.core.oadiscover.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.oadiscover.model.DiscoveryProvider;
import uk.ac.core.oadiscover.model.DiscoverySource;
import uk.ac.core.oadiscover.services.calls.CoreCall;
import uk.ac.core.oadiscover.services.calls.EuropePMCCall;
import uk.ac.core.oadiscover.services.calls.KopernioCall;
import uk.ac.core.oadiscover.services.calls.OAButtonCall;
import uk.ac.core.oadiscover.services.calls.UnpaywallCall;

/**
 *
 * @author mc26486
 */
@Service
public class ParallelDiscoveryService {

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ExecutorService executorService;

    @Autowired
    ArticleMetadataRepository articleMetadataRepository;

    private static final Logger LOG = Logger.getLogger(ParallelDiscoveryService.class.getName());

    static final List<DiscoveryProvider> availableSources = new ArrayList<DiscoveryProvider>() {
        {
            add(DiscoveryProvider.UNPAYWALL);
            add(DiscoveryProvider.EPMC);
        }
    };

    public DiscoverySource getAdditionalDiscoverySource(String doi) {

        List<Callable<DiscoverySource>> calls
                = availableSources.stream()
                        .map(id -> buildCall(id, doi))
                        .collect(Collectors.toList());

        //start!
        long t0 = System.currentTimeMillis();
        DiscoverySource discoverySourceResult = null;

        // submit all calls - with a timeout
        List<Future<DiscoverySource>> futures = null;
        try {
            futures = executorService.invokeAll(calls, 6, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(ParallelDiscoveryService.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            long t1 = System.currentTimeMillis();
            long duration = t1 - t0;
            if (duration > 5000) {
                LOG.log(Level.FINE, "Watch thread:  Doi : " + doi + " exceeeded the 5 seconds limit!! ");
                break;
            }
            int futures_done = 0;
            for (Future<DiscoverySource> future : futures) {

                if (future.isDone()) {
                    futures_done++;
                    try {
                        discoverySourceResult = future.get();
                        if (discoverySourceResult != null && !discoverySourceResult.getSource().equals("Kopernio")) {
                            return discoverySourceResult;
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ParallelDiscoveryService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        ex.printStackTrace();
                        Logger.getLogger(ParallelDiscoveryService.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
            // if all calls are done but all returning null then just break...
            if (futures_done == availableSources.size()) {
                break;
            }
        }

        return discoverySourceResult;
    }

    private Callable<DiscoverySource> buildCall(DiscoveryProvider discoveryProvider, String doi) {
        Callable<DiscoverySource> callable = null;
        switch (discoveryProvider) {
            case UNPAYWALL:
                callable = new UnpaywallCall(httpClient, jdbcTemplate, elasticsearchTemplate, doi);
                break;
            case OABUTTON:
                callable = new OAButtonCall(httpClient, jdbcTemplate, elasticsearchTemplate, doi);
                break;
            case CORE:
                callable = new CoreCall(elasticsearchTemplate,articleMetadataRepository, httpClient, jdbcTemplate, doi);
                break;
            case KOPERNIO:
                callable = new KopernioCall(httpClient, jdbcTemplate, elasticsearchTemplate, doi);
                break;
            case EPMC:
                callable = new EuropePMCCall(httpClient, jdbcTemplate, elasticsearchTemplate, doi);
                break;
        }
        return callable;
    }
}
