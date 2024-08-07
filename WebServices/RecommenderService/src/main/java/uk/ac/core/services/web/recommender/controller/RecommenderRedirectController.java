package uk.ac.core.services.web.recommender.controller;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.request.EventHit;
import com.brsanthu.googleanalytics.request.GoogleAnalyticsResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.ac.core.services.web.recommender.configuration.RecommenderConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * @author lucas
 */
@RestController
public class RecommenderRedirectController {

    private static final Logger LOGGER = Logger.getLogger("RecommenderRedirectController");

    @RequestMapping(value = "/redirect", method = {RequestMethod.GET, RequestMethod.POST})
    public void redirect(HttpServletResponse httpServletResponse,
                         @RequestParam(value = "url", defaultValue = "") String url,
                         @RequestHeader(value = "X-Client-Id", defaultValue = "") String clientId,
                         @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                         HttpServletRequest request) {

        String remoteAddressIp = request.getRemoteAddr();

        LOGGER.info("redirect() with url :" + url
                + " clientId:" + clientId
                + " userAgent:" + userAgent
                + " remote address:" + remoteAddressIp);
        if (url != null && (url.contains("core.ac.uk") || url.contains("arxiv"))) {

            if (!userAgent.contains("PRTG Network Monitor")) {
                trackRedirectEventGaAlternative(clientId, remoteAddressIp, userAgent, url);
            }
            // redirect
            httpServletResponse.setHeader("Location", url);
            httpServletResponse.setStatus(302);
        } else {
            httpServletResponse.setStatus(401);
        }

    }

    private void trackRedirectEventGaAlternative(
            String clientId, String remoteAddressIp, String userAgent, String url) {
        // GAnalytics tracking
        try {
            // if we receive empty client ID then tack anonymously according to IP address
            String trackedClientId = clientId.isEmpty()
                    ? RecommenderConstants.GA_TRACKING_ANONYMOUS_PREFIX + DigestUtils.sha1Hex(remoteAddressIp)
                    : clientId;
            URIBuilder builder = new URIBuilder();
            builder
                    .setScheme("http")
                    .setHost("www.google-analytics.com")
                    .setPath("/collect")
                    .addParameter("v", "1") // API Version
                    .addParameter("t", "event") // Event hit type
                    .addParameter("tid", RecommenderConstants.GA_TRACKING_ID) // Tracking ID
                    .addParameter("cid", trackedClientId) // Client ID
                    .addParameter("ec", RecommenderConstants.GA_EVENT_CATEGORY) // Event category
                    .addParameter("ea", RecommenderConstants.GA_DISCOVERY_REDIRECT_EVENT_ACTION) // Event action
                    .addParameter("ua", userAgent) // User Agent
                    .addParameter("uip", remoteAddressIp) // User IP Address
                    .addParameter("el", url) // Event Label
                    .addParameter("ev", "0"); // Event Value
            URI uri = builder.build();
            RestTemplate restTemplate = new RestTemplate();
            Future<String> futureResponse = CompletableFuture.supplyAsync(
                    () -> {
                        LOGGER.info("Sending request ...");
                        String response = restTemplate.getForObject(uri, String.class);
                        LOGGER.info("Sent");
                        LOGGER.info("Response = " + response);
                        return response;
                    },
                    Executors.newSingleThreadExecutor()
            );
            LOGGER.info("GA redirect event tracked");
        } catch (Exception e) {
            LOGGER.info("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void trackRedirectEventGA(
            String clientId, String remoteAddressIp, String userAgent, String url) {
        // GAnalytics tracking
        try (GoogleAnalytics ga =
                     GoogleAnalytics.builder()
                             .withTrackingId(RecommenderConstants.GA_TRACKING_ID)
                             .build()) {
            // if we receive empty client ID then tack anonymously according to IP address
            String trackedClientId = clientId.isEmpty()
                    ? RecommenderConstants.GA_TRACKING_ANONYMOUS_PREFIX + DigestUtils.sha1Hex(remoteAddressIp)
                    : clientId;

            EventHit eventHit = ga
                    .event()
                    .clientId(trackedClientId)
                    .eventCategory(RecommenderConstants.GA_EVENT_CATEGORY)
                    .eventAction(RecommenderConstants.GA_DISCOVERY_REDIRECT_EVENT_ACTION)
                    .userAgent(userAgent)
                    .userIp(remoteAddressIp)
                    .eventLabel(url)
                    .eventValue(0);

            Future<GoogleAnalyticsResponse> future = eventHit.sendAsync();
        } catch (Exception e) {
            LOGGER.info("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
