package uk.ac.core.oadiscover.configuration;

/**
 *
 * @author lucas
 */
public class OADiscoverConstants {

    // GA tracking IDs of properties
    public static final String GA_TRACKING_ID = "UA-66779096-4";//Backend service tracking property
//    public static final String GA_TRACKING_ID = "UA-11307192-6";//CORE Website Raw Data
    
    public static final String GA_EVENT_CATEGORY = "discovery";
    
    // GA actions
    public static final String GA_DISCOVERY_BROWSER_IMPRESSION_EVENT_ACTION = "discover-browser-extension";
    public static final String GA_DISCOVERY_IR_IMPRESSION_EVENT_ACTION = "discover-repository";
    public static final String GA_DISCOVERY_REDIRECT_EVENT_ACTION = "redirect";
    
    public static final String GA_TRACKING_ANONYMOUS_PREFIX = "anonymous-";
    public static final String GA_TRACKING_ANONYMOUS_IR_PREFIX = "anonymous-repository-";
}
