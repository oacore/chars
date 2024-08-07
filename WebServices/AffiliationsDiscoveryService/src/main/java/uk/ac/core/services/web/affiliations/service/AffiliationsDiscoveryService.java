package uk.ac.core.services.web.affiliations.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryRequest;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Service
public class AffiliationsDiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(AffiliationsDiscoveryService.class);
    private static final Long TIMEOUT = 5L;

    private final RegexExtractionService regexService;
    private final OrcidApiExtractionService orcidService;
    private final CrossrefApiExtractionService crossrefService;
    private final GrobidExtractionService grobidService;
    private final OpenAlexApiExtractionService openAlexService;
    private final ExecutorService executor;

    @Autowired
    public AffiliationsDiscoveryService(
            RegexExtractionService regexService,
            OrcidApiExtractionService orcidService,
            CrossrefApiExtractionService crossrefService,
            GrobidExtractionService grobidService, OpenAlexApiExtractionService openAlexService) {
        this.regexService = regexService;
        this.orcidService = orcidService;
        this.crossrefService = crossrefService;
        this.grobidService = grobidService;
        this.openAlexService = openAlexService;
        this.executor = new ThreadPoolExecutor(
                3,
                5,
                2, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public AffiliationsDiscoveryResponse extract(AffiliationsDiscoveryRequest request) {
        log.info("Received the request");
        log.info("Starting affiliation extraction ...");
        long start = System.currentTimeMillis(), end;
        List<AffiliationsDiscoveryResponse> responses = new ArrayList<>();
        try {
            List<Callable<AffiliationsDiscoveryResponse>> extractionTasks =
                    Arrays.asList(
                            () -> this.regexService.extract(request),
                            () -> this.orcidService.extract(request),
                            () -> this.crossrefService.extract(request),
                            () -> this.grobidService.extract(request),
                            () -> this.openAlexService.extract(request)
                    );
            List<Future<AffiliationsDiscoveryResponse>> futures =
                    this.executor.invokeAll(extractionTasks);
            for (Future<AffiliationsDiscoveryResponse> future: futures) {
                responses.add(future.get(TIMEOUT, TimeUnit.SECONDS));
            }
        } catch (Exception e) {
            log.error("Exception occurred", e);
        }
        AffiliationsDiscoveryResponse response = this.findBestResponse(responses);
        end = System.currentTimeMillis();
        log.info("Affiliation mining finished in {} ms", end - start);
        log.info("Best result with {} affiliations", response.getCount());
        return response;
    }

    public AffiliationsDiscoveryResponse extractWithOpenAlex(AffiliationsDiscoveryRequest request) {
        log.info("Received the request");
        log.info("Starting affiliation extraction using OpenAlex API only ...");
        long start = System.currentTimeMillis(), end;
        AffiliationsDiscoveryResponse response = this.openAlexService.extract(request);
        end = System.currentTimeMillis();
        log.info("Affiliation mining finished in {} ms", end - start);
        log.info("Result with {} affiliations", response.getCount());
        return response;
    }

    public AffiliationsDiscoveryResponse extractWithGrobid(AffiliationsDiscoveryRequest request) {
        log.info("Received the request");
        log.info("Starting affiliation extraction using GROBID engine only ...");
        long start = System.currentTimeMillis(), end;
        AffiliationsDiscoveryResponse response = this.grobidService.extract(request);
        end = System.currentTimeMillis();
        log.info("Affiliation mining finished in {} ms", end - start);
        log.info("Result with {} affiliations", response.getCount());
        return response;
    }

    public AffiliationsDiscoveryResponse extractWithRegex(AffiliationsDiscoveryRequest request) {
        log.info("Received the request");
        log.info("Starting affiliation extraction using regex only ...");
        long start = System.currentTimeMillis(), end;
        AffiliationsDiscoveryResponse response = this.regexService.extract(request);
        end = System.currentTimeMillis();
        log.info("Affiliation mining finished in {} ms", end - start);
        log.info("Result with {} affiliations", response.getCount());
        return response;
    }

    public AffiliationsDiscoveryResponse extractWithCrossref(AffiliationsDiscoveryRequest request) {
        log.info("Received the request");
        log.info("Starting affiliation extraction using crossref only ...");
        long start = System.currentTimeMillis(), end;
        AffiliationsDiscoveryResponse response = this.crossrefService.extract(request);
        end = System.currentTimeMillis();
        log.info("Affiliation mining finished in {} ms", end - start);
        log.info("Result with {} affiliations", response.getCount());
        return response;
    }

    public AffiliationsDiscoveryResponse extractWithOrcid(AffiliationsDiscoveryRequest request) {
        log.info("Received the request");
        log.info("Starting affiliation extraction using orcid only ...");
        long start = System.currentTimeMillis(), end;
        AffiliationsDiscoveryResponse response = this.orcidService.extract(request);
        end = System.currentTimeMillis();
        log.info("Affiliation mining finished in {} ms", end - start);
        log.info("Result with {} affiliations", response.getCount());
        return response;
    }

    private AffiliationsDiscoveryResponse findBestResponse(List<AffiliationsDiscoveryResponse> responses) {
        AffiliationsDiscoveryResponse maxCountResponse = responses.get(0);
        for (AffiliationsDiscoveryResponse response: responses) {
            if (response.getCount() > maxCountResponse.getCount()) {
                maxCountResponse = response;
            }
        }
        if (maxCountResponse.getCount() == 0) {
            if (maxCountResponse.getMessage() == null) {
                maxCountResponse.setMessage("Could not find any affiliations");
            }
        }
        return maxCountResponse;
    }

}
