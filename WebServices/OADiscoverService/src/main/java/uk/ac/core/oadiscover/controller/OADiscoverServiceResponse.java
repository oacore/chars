package uk.ac.core.oadiscover.controller;

import uk.ac.core.oadiscover.services.OADiscoveryHashingService;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mc26486
 */
public class OADiscoverServiceResponse {

    private static final String REDIRECT_PREFIX = "http://core.ac.uk/labs/oadiscovery/redirect";

    private String fullTextLink;
    /**
     * Where was the link discovered from, e.g. from Crossref, from CORE - what
     * repository, publisher, journal,...
     */
    private String source;

    public String getFullTextLink() {
        return fullTextLink;
    }

    public void setFullTextLink(String fullTextLink, OADiscoveryHashingService oaDiscoveryHashingService) {
        if (fullTextLink != null && !fullTextLink.isEmpty()) {
            try {

                this.fullTextLink = REDIRECT_PREFIX + "?url=" + URLEncoder.encode(fullTextLink, "UTF-8");

                this.fullTextLink += "&key="+ oaDiscoveryHashingService.getKey(fullTextLink);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(OADiscoverServiceResponse.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException e) {
                Logger.getLogger(OADiscoverServiceResponse.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }



    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
