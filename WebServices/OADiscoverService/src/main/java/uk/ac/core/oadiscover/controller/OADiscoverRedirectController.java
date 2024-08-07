package uk.ac.core.oadiscover.controller;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.request.EventHit;
import com.brsanthu.googleanalytics.request.GoogleAnalyticsResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.oadiscover.configuration.OADiscoverConstants;
import uk.ac.core.oadiscover.services.OADiscoveryHashingService;

import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lucas
 */
@RestController
public class OADiscoverRedirectController {

    private static final Logger LOGGER = Logger.getLogger("OADiscoverRedirectController");

    @Autowired
    private OADiscoveryHashingService oaDiscoveryHashingService;

    @RequestMapping(value = "/redirect", method = {RequestMethod.GET, RequestMethod.POST})
    public void redirect(HttpServletResponse httpServletResponse,
                         @RequestParam(value = "url", defaultValue = "") String url,
                         @RequestParam(value = "key", defaultValue = "") String key,
                         @RequestHeader(value = "X-Client-Id", defaultValue = "") String clientId,
                         @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                         HttpServletRequest request) {

        String remoteAddressIp = request.getRemoteAddr();

        LOGGER.info("redirect() with url :" + url
                + " clientId:" + clientId
                + " userAgent:" + userAgent
                + " remote address:" + remoteAddressIp);

        if (!userAgent.contains("PRTG Network Monitor")) {
            trackRedirectEventGA(clientId, remoteAddressIp, userAgent, url);
        }

        if (!oaDiscoveryHashingService.isValid(url, key)){
            httpServletResponse.setStatus(401);
            return;
        }

        // redirect
        httpServletResponse.setHeader("Location", url);
        httpServletResponse.setStatus(302);
    }

    private void trackRedirectEventGA(String clientId, String remoteAddressIp, String userAgent, String url) {
        // GAnalytics tracking
        GoogleAnalytics ga = GoogleAnalytics.builder()
                .withTrackingId(OADiscoverConstants.GA_TRACKING_ID)
                .build();

        // if we receive empty client ID then tack anonymously according to IP address
        String trackedClientId = clientId.isEmpty()
                ? OADiscoverConstants.GA_TRACKING_ANONYMOUS_PREFIX + DigestUtils.sha1Hex(remoteAddressIp)
                : clientId;

        EventHit eventHit = ga
                .event()
                .clientId(trackedClientId)
                .eventCategory(OADiscoverConstants.GA_EVENT_CATEGORY)
                .eventAction(OADiscoverConstants.GA_DISCOVERY_REDIRECT_EVENT_ACTION)
                .userAgent(userAgent)
                .userIp(remoteAddressIp)
                .eventLabel(url)
                .eventValue(0);

        Future<GoogleAnalyticsResponse> future = eventHit.sendAsync();
    }

}
