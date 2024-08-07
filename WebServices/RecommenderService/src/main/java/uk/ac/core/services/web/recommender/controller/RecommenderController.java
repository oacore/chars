package uk.ac.core.services.web.recommender.controller;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.request.EventHit;
import com.brsanthu.googleanalytics.request.GoogleAnalyticsResponse;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import uk.ac.core.elasticsearch.caching.RecommendationCachedObject;
import uk.ac.core.elasticsearch.caching.RecommendationCachingService;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.entities.ElasticSearchWorkMetadata;
import uk.ac.core.elasticsearch.recommendation.ElasticSearchRecommendationService;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.elasticsearch.repositories.WorksMetadataRepository;
import uk.ac.core.services.web.recommender.configuration.RecommendConstants;
import uk.ac.core.services.web.recommender.model.RecommenderServiceRequest;
import uk.ac.core.services.web.recommender.model.RecommenderServiceResponse;
import uk.ac.core.services.web.recommender.services.RecommenderService;

/**
 * @author mc26486
 */
@RestController
public class RecommenderController {

    @Autowired
    ArticleMetadataRepository elasticsearchArticleMetadataRepository;

    @Autowired
    RecommenderService recommendService;

    @Autowired
    WorksMetadataRepository elasticsearchWorkMetadataRepository;

    @Autowired
    RecommendationCachingService recommendationCachingService;

    private static final Logger LOG = LoggerFactory.getLogger(RecommenderController.class);

    @RequestMapping(value = "/cache-invalidate", method = {RequestMethod.GET})
    public void invalidate(@RequestParam(value = "source_url") String source_url) {
        LOG.info("Invalidation cache of :" + source_url);
        try {
            String source_url_decoded = URLDecoder.decode(source_url, "UTF-8");
            recommendationCachingService.invalidate(source_url_decoded);
        } catch (UnsupportedEncodingException ex) {
            LOG.info(ex.getMessage());
        }

    }

    @RequestMapping(value = "/recommend", method = {RequestMethod.GET, RequestMethod.POST})
    public RecommenderServiceResponse recommend(
            @RequestParam(value = "referer", defaultValue = "") String referer,
            @RequestParam(value = "recType", defaultValue = "") String recType,
            @RequestParam(value = "idRepository", defaultValue = "") String idRepository,
            @RequestParam(value = "oai", defaultValue = "") String oai,
            @RequestParam(value = "url", defaultValue = "") String url,
            @RequestParam(value = "aabstract", defaultValue = "") String aabstract,
            @RequestParam(value = "title", defaultValue = "") String title,
            @RequestParam(value = "algorithm", defaultValue = "moreLikeThis") String algorithm,
            @RequestParam(value = "countLimit", defaultValue = "10") Integer size,
            @RequestParam(value = "dateLimit", defaultValue = "3") Integer dateDepth,
            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
            @RequestParam(value = "idRecommender", defaultValue = "") String idRecommender,
            @RequestParam(value = "identifier", defaultValue = "") String identifier,
            @RequestParam(value = "resultType", defaultValue = ElasticSearchRecommendationService.RESULT_TYPE_OUTPUT) String resultType,
            HttpServletRequest request
    ) throws Exception {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        Future future = executor.submit(new Callable<RecommenderServiceResponse>() {

            @Override
            public RecommenderServiceResponse call() throws Exception {

                String userAgentInfo = request.getHeader("User-Agent");
                String remoteAddressIp = request.getRemoteAddr();
                int inputCountLimit = size;
                int dateLimit = dateDepth;
                Integer targetArticleId = getArticleIdByIdentifier(identifier, resultType);
                if (targetArticleId == null) {
                    targetArticleId = getArticleIdByReferer(referer, resultType);
                    if (targetArticleId == null) {
                        targetArticleId = getArticleIdByOai(oai, resultType);
                        if (targetArticleId == null) {
                            targetArticleId = getArticleIdByUrl(url, resultType);
                        }
                    }
                }

                RecommenderServiceRequest recommenderServiceRequest = new RecommenderServiceRequest();
                recommenderServiceRequest.setAabstract(aabstract);
                recommenderServiceRequest.setAlgorithm(algorithm);
                recommenderServiceRequest.setTitle(title);
                recommenderServiceRequest.setUrl(url);
                recommenderServiceRequest.setOai(oai);
                recommenderServiceRequest.setTargetArticleId(targetArticleId);
                recommenderServiceRequest.setReferer(referer);
                recommenderServiceRequest.setSize(size);
                recommenderServiceRequest.setRecType(recType);
                recommenderServiceRequest.setRepositoryId(idRepository);
                recommenderServiceRequest.setIdRecommender(idRecommender);
                recommenderServiceRequest.setResultType(resultType);

                LOG.info("Recommender request: " + recommenderServiceRequest.toString());

                trackBrowserEventGaAlternative(recommenderServiceRequest, userAgent, remoteAddressIp);

                // fetch from cache if exists
                String cachedKey = recommenderServiceRequest.getSHA1code();
                LOG.info("Looking in cache for key : {}", cachedKey);
                long t0 = System.currentTimeMillis();
                RecommendationCachedObject cached = recommendationCachingService.fetch(cachedKey);
                long t1 = System.currentTimeMillis();
                LOG.info("Cache lookup took : {} ms", (t1 - t0));
                if (cached != null) {
                    LOG.info("Found object in cache (key: {})", cachedKey);
                    Gson gson = new Gson();
                    RecommenderServiceResponse deserialisedCachedResponse = gson.fromJson(cached.getData(), RecommenderServiceResponse.class);
                    return deserialisedCachedResponse;
                }

                long t2 = System.currentTimeMillis();
                RecommenderServiceResponse recommenderServiceResponse = recommendService.recommend(recommenderServiceRequest);
                long t3 = System.currentTimeMillis();
                LOG.info("Fetching a fresh resultset took : " + (t3 - t2) + "ms");
                // save to cache before returning
                Gson gson = new Gson();
                String serialisedData = gson.toJson(recommenderServiceResponse);
                long cacheTimeStart = System.currentTimeMillis();
                LOG.info("saving to cache" + cachedKey + " : " + referer);
                recommendationCachingService.save(cachedKey, referer, serialisedData);
                long cacheTimeEnd = System.currentTimeMillis();
                LOG.info("Caching took " + (cacheTimeEnd - cacheTimeStart) + " ms");

                return recommenderServiceResponse;
            }

        });


        RecommenderServiceResponse recommenderServiceResponse = null;
        try {
            recommenderServiceResponse = (RecommenderServiceResponse) future.get();
        } catch (InterruptedException e) {
            LOG.error("Interrupted exception" + e.getMessage(), e);
            throw new Exception("The request took too long.");
        } catch (ExecutionException e) {
            LOG.error("Execution interrupted " + e.getMessage(), e);
            throw new Exception("The request took too long.");
        }
        executor.shutdown();
        return recommenderServiceResponse;
    }

    private Set<String> urlVariations(String url) {
        Set<String> possibleUrls = new HashSet<>();
        possibleUrls.add(escapeUris(url));
        possibleUrls.add(escapeUris(url.replace("https", "http")));
        if (url.contains("arxiv.org")) {
            possibleUrls.add(escapeUris(url.substring(0, url.lastIndexOf("v"))));
            possibleUrls.add(escapeUris(url.substring(0, url.lastIndexOf("v")).replace("https", "http")));
        }
        return possibleUrls;
    }

    private Integer getArticleIdByUrl(String url, String resultType) {
        ElasticSearchArticleMetadata targetArticle = null;
        Integer targetArticleId = null;
        if (url != null && !url.isEmpty()) {

            Set<String> possibleUrls = this.urlVariations(url);
            if (resultType.equals(ElasticSearchRecommendationService.RESULT_TYPE_OUTPUT)) {
                List<ElasticSearchArticleMetadata> articles = elasticsearchArticleMetadataRepository.findByUrlsIn(possibleUrls);
                if (!articles.isEmpty()) {
                    targetArticleId = Integer.valueOf(articles.get(0).getId());
                    LOG.info("Found article by Url? " + ((targetArticleId == null) ? "NO" : "Yes, id:" + targetArticleId));
                }
            }else {
                List<ElasticSearchWorkMetadata> articles = elasticsearchWorkMetadataRepository.findListByIdentifiers(possibleUrls);
                if (!articles.isEmpty()) {
                    targetArticleId = Integer.valueOf(articles.get(0).getId());
                    LOG.info("Found article by Url? " + ((targetArticleId == null) ? "NO" : "Yes, id:" + targetArticleId));
                }
            }

        }
        return targetArticleId;
    }

    private Integer getArticleIdByOai(String oai, String resultType) {
        Integer targetArticleId = null;
        // if not try to find it by oai
        if (oai != null && !oai.equals("")) {
            String escapedOai = escapeUris(oai);
            System.out.println("escapedOai = " + escapedOai);
            if (resultType.equals(ElasticSearchRecommendationService.RESULT_TYPE_OUTPUT)) {
                List<ElasticSearchArticleMetadata> targetArticles = elasticsearchArticleMetadataRepository.findListByOai(escapedOai);
                if (targetArticles.size() > 0) {
                    targetArticleId = Integer.parseInt(targetArticles.get(0).getId());
                }
            } else {
                List<ElasticSearchWorkMetadata> targetArticles = elasticsearchWorkMetadataRepository.findListByOaiIds(escapedOai);
                if (targetArticles.size() > 0) {
                    targetArticleId = targetArticles.get(0).getId();
                }
            }

            LOG.info("Found target article? " + (targetArticleId == null ? "NO" : "Yes, 1st one is:" + targetArticleId));
        }
        return targetArticleId;
    }

    private Integer getArticleIdByIdentifier(String identifier, String resultType) {
        Integer targetArticleId = null;
        if (!identifier.isEmpty() && identifier.startsWith("core:")) {
            String coreId = identifier.replace("core:", "");
            if (resultType.equals(ElasticSearchRecommendationService.RESULT_TYPE_OUTPUT)) {
                Optional<ElasticSearchArticleMetadata> opt = elasticsearchArticleMetadataRepository.findById(coreId);
                targetArticleId = (opt == null) ? null : Integer.valueOf(opt.get().getId());
            } else {
                Optional<ElasticSearchWorkMetadata> opt = elasticsearchWorkMetadataRepository.findById(Integer.valueOf(coreId));
                targetArticleId = (opt == null) ? null : opt.get().getId();

            }

        }
        return targetArticleId;
    }

    private Integer getArticleIdByReferer(String referer, String resultType) {
        ElasticSearchArticleMetadata targetArticle = null;
        Integer targetArticleId = null;
        // try to find CORE article by core id (if it comes from CORE display pages
        // this would be the best way)
        if (referer.startsWith("https://core.ac.uk/outputs/")) {
            String coreId = referer.split("outputs/")[1];
            if (coreId.contains("?")) {
                coreId = coreId.split("\\?")[0];
            }
            if (coreId.contains("/")) {
                coreId = coreId.split("\\/")[0];
            }
            targetArticleId = Integer.parseInt(coreId);
        }
        if (referer.startsWith("https://core.ac.uk/works/")) {
            String coreId = referer.split("works/")[1];
            if (coreId.contains("?")) {
                coreId = coreId.split("\\?")[0];
            }
            if (coreId.contains("/")) {
                coreId = coreId.split("\\/")[0];
            }
            targetArticleId = Integer.parseInt(coreId);
        }
        if (referer.startsWith("https://core.ac.uk/display/")) {
            String coreId = referer.split("display/")[1];
            if (coreId.contains("?")) {
                coreId = coreId.split("\\?")[0];
            }
            if (coreId.contains("/")) {
                coreId = coreId.split("\\/")[0];
            }
            targetArticleId = Integer.parseInt(coreId);
        }
        return targetArticleId;
    }

    private String escapeUris(String url) {
        String escapedUrl = url.replaceAll("\\:", "\\\\:").replaceAll("\\/", "\\\\/");
        return escapedUrl;
    }

    private void trackBrowserEventGaAlternative(
            RecommenderServiceRequest recommenderServiceRequest,
            String userAgent,
            String remoteAddressIp
    ) {
        // do not track PRTG requests
        if (userAgent.contains("PRTG Network Monitor")
                || recommenderServiceRequest.getReferer().equals("https://core.ac.uk/recommender/test")) {
            LOG.info("This is an automated request from PRTG monitor to check health of the service. It will not be tracked by GA");
            return;
        }

        // GAnalytics tracking
        try {
            String trackedClientId;

            if (!recommenderServiceRequest.getRepositoryId().isEmpty()) {
                trackedClientId = recommenderServiceRequest.getRecType() + recommenderServiceRequest.getRepositoryId();
            } else if (recommenderServiceRequest.getReferer().contains("core.ac.uk/display")) {
                trackedClientId = "display";
            } else if (recommenderServiceRequest.getReferer().contains("core.ac.uk/reader")) {
                trackedClientId = "reader";
            } else {
                trackedClientId = recommenderServiceRequest.getRecType();
            }

            String domainOfReferalUrl;
            try {
                domainOfReferalUrl = getDomainName(recommenderServiceRequest.getReferer());
            } catch (URISyntaxException ex) {
                LOG.warn(ex.getMessage(), ex);
                domainOfReferalUrl = "unknown";
            }

            String action = trackedClientId + "-" + domainOfReferalUrl;

            URIBuilder builder = new URIBuilder();
            builder
                    .setScheme("http")
                    .setHost("www.google-analytics.com")
                    .setPath("/collect")
                    .addParameter("v", "1") // API Version
                    .addParameter("t", "event") // Hit Type
                    .addParameter("tid", RecommendConstants.GA_TRACKING_ID) // Tracking ID
                    .addParameter("uid", recommenderServiceRequest.getIdRecommender()) // User ID
                    .addParameter("cid", UUID.randomUUID().toString()) // Client ID
                    .addParameter("ec", RecommendConstants.GA_RECOMMENDER_EVENT_CATEGORY) // Event Category
                    .addParameter("ea", action) // Event Action
                    .addParameter("ua", userAgent) // User Agent
                    .addParameter("uip", remoteAddressIp) // User IP
                    .addParameter("el", recommenderServiceRequest.getReferer()) // Event Label
                    .addParameter("ev", "0"); // Event Value

            URI uri = builder.build();
            final RestTemplate restTemplate = new RestTemplate();
            Future<String> futureResponse = CompletableFuture.supplyAsync(
                    () -> {
                        LOG.info("Sending request ...");
                        String response = restTemplate.getForObject(uri, String.class);
                        LOG.info("Sent");
                        LOG.info("Response = {}", response);
                        return response;
                    },
                    Executors.newSingleThreadExecutor()
            );
            LOG.info("GA browser event tracked");
        } catch (Exception e) {
            LOG.error("Exception while tracking browser event GA", e);
        }
    }

    private void trackBrowserEventGA(RecommenderServiceRequest recommenderServiceRequest,
                                     String userAgent,
                                     String remoteAddressIp) {

        // do not track PRTG requests
        if (userAgent.contains("PRTG Network Monitor")
                || recommenderServiceRequest.getReferer().equals("https://core.ac.uk/recommender/test")) {
            LOG.info("This is an automated request from PRTG monitor to check health of the service. It will not be tracked by GA");
            return;
        }

        // GAnalytics tracking
        GoogleAnalytics ga = GoogleAnalytics.builder()
                .withTrackingId(RecommendConstants.GA_TRACKING_ID)
                .build();

        String trackedClientId = "";

        if (!recommenderServiceRequest.getRepositoryId().isEmpty()) {
            trackedClientId = recommenderServiceRequest.getRecType() + recommenderServiceRequest.getRepositoryId();
//        } else if (recommenderServiceRequest.getRecType().equals("email")){
        } else if (recommenderServiceRequest.getReferer().contains("core.ac.uk/display")) {
            trackedClientId = "display";
        } else if (recommenderServiceRequest.getReferer().contains("core.ac.uk/reader")) {
            trackedClientId = "reader";
        } else {
            trackedClientId = recommenderServiceRequest.getRecType();
        }

        String domainOfReferalUrl = "";
        try {
            domainOfReferalUrl = getDomainName(recommenderServiceRequest.getReferer());
        } catch (URISyntaxException ex) {
            LOG.warn(ex.getMessage(), ex);
            domainOfReferalUrl = "unknown";
        }

        String action = trackedClientId + "-" + domainOfReferalUrl;

        EventHit eventHit = ga
                .event()
                .userId(recommenderServiceRequest.getIdRecommender())
                .clientId(UUID.randomUUID().toString())
                .eventCategory(RecommendConstants.GA_RECOMMENDER_EVENT_CATEGORY)
                .eventAction(action)
                .userAgent(userAgent)
                .userIp(remoteAddressIp)
                .eventLabel(recommenderServiceRequest.getReferer())
                .eventValue(0);
//
        Future<GoogleAnalyticsResponse> future = eventHit.sendAsync();
    }

    public static String getDomainName(String url) throws URISyntaxException {
        if (!url.isEmpty()) {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } else {
            return "";
        }
    }


}
