package uk.ac.core.oadiscover.controller;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.request.EventHit;
import com.brsanthu.googleanalytics.request.GoogleAnalyticsResponse;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.database.service.document.DocumentDAO;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.oadiscover.configuration.OADiscoverConstants;
import uk.ac.core.oadiscover.model.DiscoverIRPayload;
import uk.ac.core.oadiscover.model.DiscoveryProvider;
import uk.ac.core.oadiscover.model.DiscoverySource;
import uk.ac.core.oadiscover.services.OADiscoveryBlacklistService;
import uk.ac.core.oadiscover.services.OADiscoveryHashingService;
import uk.ac.core.oadiscover.services.ParallelDiscoveryService;
import uk.ac.core.oadiscover.services.calls.CoreCall;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mc26486
 */
@RestController
public class OADiscoverController {

    private static final Logger LOG = Logger.getLogger("OADiscoverController");

    @Autowired
    ParallelDiscoveryService parallelDiscoveryService;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    HttpClient httpClient;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataProviderService dataProviderService;

    @Autowired
    DocumentDAO documentDAO;

    @Autowired
    ExecutorService executorService;

    @Autowired
    OADiscoveryBlacklistService discoveryBlacklistService;

    @Autowired
    ArticleMetadataRepository articleMetadataRepository;

    @Autowired
    OADiscoveryHashingService oaDiscoveryHashingService;

    @RequestMapping(value = "/discover-ir", method = {RequestMethod.GET, RequestMethod.POST})
    public OADiscoverServiceResponse discoverP(
            @RequestParam(value = "doi", defaultValue = "") String doi,
            @RequestParam(value = "title", defaultValue = "") String title,
            @RequestParam(value = "year", defaultValue = "") String year,
            @RequestParam(value = "authors", defaultValue = "") String authors,
            @RequestHeader(value = "X-Client-Id", defaultValue = "") String clientId,
            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
            @RequestHeader(value = "eprints_id", defaultValue = "") String eprintsId,
            @RequestParam(value = "plugin_id", defaultValue = "") String pluginId,
            @RequestParam(value = "referrer_url", defaultValue = "") String referrerUrl,
            @RequestBody(required = false) String payload,
            HttpServletRequest request
    ) {
        if (payload != null && !payload.isEmpty()
                && doi.isEmpty() && eprintsId.isEmpty() && pluginId.isEmpty() && referrerUrl.isEmpty()) {
            // parse the payload, extract the request parameters and OVERWRITE query params with
            Gson gson = new Gson();
            DiscoverIRPayload payloadObj = gson.fromJson(payload, DiscoverIRPayload.class);
            doi = payloadObj.getDoi();
            eprintsId = payloadObj.getEprints_id();
            pluginId = payloadObj.getPlugin_id();
            referrerUrl = payloadObj.getReferrer_url();
        }
        return discover(doi, title, year, authors, clientId, userAgent, eprintsId, pluginId, referrerUrl, request);
    }

    @RequestMapping(value = "/discover", method = {RequestMethod.GET, RequestMethod.POST})
    public OADiscoverServiceResponse discover(
            @RequestParam(value = "doi", defaultValue = "") String doi,
            @RequestParam(value = "title", defaultValue = "") String title,
            @RequestParam(value = "year", defaultValue = "") String year,
            @RequestParam(value = "authors", defaultValue = "") String authors,
            @RequestHeader(value = "X-Client-Id", defaultValue = "") String clientId,
            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
            @RequestHeader(value = "eprints_id", defaultValue = "") String eprintsId,
            @RequestParam(value = "plugin_id", defaultValue = "") String pluginId,
            @RequestParam(value = "referrer_url", defaultValue = "") String referrerUrl,
            HttpServletRequest request
    ) {

        String remoteAddressIp = request.getRemoteAddr();

        LOG.info("Input doi: " + doi
                + " title: " + title
                + " year: " + year
                + " authors: " + authors
                + " X-clientID: " + clientId
                + " IP address: " + remoteAddressIp
                + " user agent: " + userAgent
                + " eprintsId: " + eprintsId
                + " pluginId: " + pluginId
                + " referrerUrl: " + referrerUrl
        );

        OADiscoverServiceResponse oADiscoverServiceResponse = null;
        if (!doi.isEmpty() && !doi.equals("undefined") && doi.length() > 5) {

            // do not track PRTG requests
            if (!userAgent.contains("PRTG Network Monitor")) {
                trackBrowserEventGA(clientId, remoteAddressIp, userAgent, doi);
            }

            oADiscoverServiceResponse = discoverOALinkByDOI(doi);

            if (checkIfBlackListed(referrerUrl, doi)) {
                oADiscoverServiceResponse = buildBlacklistedResponse(oADiscoverServiceResponse.getFullTextLink());
            }

        } else if (eprintsId != null && !eprintsId.isEmpty()) {
            // discover IR
            oADiscoverServiceResponse = discoverInstitutionalRepository(eprintsId, pluginId, referrerUrl, doi, userAgent, request);

        } else if (title != null && !title.isEmpty()) {
            // we do not support querying by title for now           
        }

        return oADiscoverServiceResponse;
    }

    private void trackBrowserEventGA(String clientId, String remoteAddressIp, String userAgent, String doi) {
        // GAnalytics tracking
        GoogleAnalytics ga = GoogleAnalytics.builder()
                .withTrackingId(OADiscoverConstants.GA_TRACKING_ID)
                .build();

        // if we receive empty client ID then track anonymously according to IP address
        String trackedClientId = clientId.isEmpty()
                ? OADiscoverConstants.GA_TRACKING_ANONYMOUS_PREFIX + DigestUtils.sha1Hex(remoteAddressIp)
                : clientId;

        EventHit eventHit = ga
                .event()
                // .userID        <--  need to set this according to DB lookup on user registration table
                .clientId(trackedClientId)
                .eventCategory(OADiscoverConstants.GA_EVENT_CATEGORY)
                .eventAction(OADiscoverConstants.GA_DISCOVERY_BROWSER_IMPRESSION_EVENT_ACTION)
                .userAgent(userAgent)
                .userIp(remoteAddressIp)
                .eventLabel(doi)
                .eventValue(0);

        Future<GoogleAnalyticsResponse> future = eventHit.sendAsync();
    }

    private OADiscoverServiceResponse discoverOALinkByDOI(String doi) {
        OADiscoverServiceResponse oADiscoverServiceResponse = null;
        DiscoverySource verifiedLink = null;
        LOG.info("Search local...");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CoreCall coreCall = new CoreCall(elasticsearchTemplate, articleMetadataRepository, httpClient, jdbcTemplate, doi);
        try {
            verifiedLink = executorService.submit(coreCall).get();
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        if (verifiedLink != null) {
            oADiscoverServiceResponse = buildResponse(verifiedLink.getSource(), verifiedLink.getLink());
        } else {
            LOG.info("Parallel execution to external services...");
            verifiedLink = this.parallelDiscoveryService.getAdditionalDiscoverySource(doi);
            if (verifiedLink != null) {
                oADiscoverServiceResponse = buildResponse(verifiedLink.getSource(), verifiedLink.getLink());
            } else {
                oADiscoverServiceResponse = buildEmptyResponse();
            }
        }
        return oADiscoverServiceResponse;
    }

    private OADiscoverServiceResponse discoverInstitutionalRepository(
            String eprintsId,
            String pluginId,
            String referrerUrl,
            String doi,
            String userAgent,
            HttpServletRequest request
    ) {

        String remoteAddressIp = request.getRemoteAddr();

        LOG.info("Input eprints_id: " + eprintsId
                + " plugin_id: " + pluginId
                + " referrer_url: " + referrerUrl
                + " doi: " + doi
                + " IP address: " + remoteAddressIp
                + " user agent: " + userAgent);

        // at least one of eprints_id or doi needed
        if ((eprintsId == null || eprintsId.isEmpty()) && (doi == null || doi.isEmpty())) {
            return null;
        }

        if (!userAgent.contains("PRTG Network Monitor")) {
            trackRepositoryEventGA(pluginId, remoteAddressIp, userAgent, doi);
        }

        OADiscoverServiceResponse response = null;

        // if doi is not present - try to find it by the eprints id and referer url
        if (doi == null || doi.isEmpty()) {

            //Get the repository from the referrer_url
            URI uri;
            String repoDomain = null;
            try {
                uri = new URI(referrerUrl);
                repoDomain = uri.getHost();
            } catch (URISyntaxException ex) {
                Logger.getLogger(OADiscoverController.class.getName()).log(Level.SEVERE, null, ex);
            }

            DataProviderBO dataProvider = dataProviderService.findAllReposWithSimilarOaiPmhEndpoint(repoDomain).get(0);

            // get coreId by oai and repository domain
            List<RepositoryDocument> coreIds = documentDAO.getArticlesByOaiAndRepo(eprintsId, dataProvider.getId());

            // get DOI
            if (!coreIds.isEmpty()) {
                for (RepositoryDocument rd : coreIds) {
                    if (rd.getPdfStatus() == 1) {
                        if (checkIfBlackListed(referrerUrl, doi)) {
                            response = buildBlacklistedResponse(this.constructCOREDownloadUrl(rd.getIdDocument()));
                            return response;
                        }
                        response = buildResponse(DiscoveryProvider.CORE.verbose(), this.constructCOREDownloadUrl(rd.getIdDocument()));
                        return response;
                    }
                    String dbDOI = documentDAO.getArticleDoiById(rd.getIdDocument());
                    if (dbDOI != null) {
                        doi = dbDOI;
                        break;
                    }
                }
            }
        }

        // use the generic discovery function by DOI
        response = this.discoverOALinkByDOI(doi);

        LOG.info(String.format("Checking if %s, %s, %s is blacklisted", doi, referrerUrl, eprintsId));
        if (checkIfBlackListed(referrerUrl, doi)) {
            response = buildBlacklistedResponse(response.getFullTextLink());
        }

        return response;
    }

    private void trackRepositoryEventGA(String pluginId, String remoteAddressIp, String userAgent, String doi) {
        // GAnalytics tracking
        GoogleAnalytics ga = GoogleAnalytics.builder()
                .withTrackingId(OADiscoverConstants.GA_TRACKING_ID)
                .build();

        // if we receive empty plugin ID then track anonymously according to IP address
        String trackedClientId = pluginId.isEmpty()
                ? OADiscoverConstants.GA_TRACKING_ANONYMOUS_IR_PREFIX + DigestUtils.sha1Hex(remoteAddressIp)
                : pluginId;

        EventHit eventHit = ga
                .event()
                .clientId(trackedClientId)
                .eventCategory(OADiscoverConstants.GA_EVENT_CATEGORY)
                .eventAction(OADiscoverConstants.GA_DISCOVERY_IR_IMPRESSION_EVENT_ACTION)
                .userAgent(userAgent)
                .userIp(remoteAddressIp)
                .eventLabel(doi)
                .eventValue(0);

        Future<GoogleAnalyticsResponse> future = eventHit.sendAsync();
    }

    private OADiscoverServiceResponse buildResponse(String source, String downloadUrl) {
        OADiscoverServiceResponse oADiscoverServiceResponse = new OADiscoverServiceResponse();
        oADiscoverServiceResponse.setFullTextLink(downloadUrl, oaDiscoveryHashingService);
        oADiscoverServiceResponse.setSource(source);
        return oADiscoverServiceResponse;
    }

    private OADiscoverServiceResponse buildBlacklistedResponse(String downloadUrl) {
        return buildEmptyResponse();
    }

    private OADiscoverServiceResponse buildEmptyResponse() {
        OADiscoverServiceResponse oADiscoverServiceResponse = new OADiscoverServiceResponse();
        oADiscoverServiceResponse.setFullTextLink(null, oaDiscoveryHashingService);
        oADiscoverServiceResponse.setSource("Not found");
        return oADiscoverServiceResponse;
    }

    // constructs the reader URL 
    private String constructCOREDownloadUrl(Integer id) {
        return "https://core.ac.uk/reader/" + id;
    }

    private boolean checkIfBlackListed(
            String referrerUrl, String doi) {

        boolean blacklisted = discoveryBlacklistService.isBlackListed(referrerUrl, doi);
        return blacklisted;
    }
}
