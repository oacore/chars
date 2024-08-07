
package uk.ac.core.metadatadownloadworker.worker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns a Clean URL ready for harvesting
 * @author samuel
 */
public class CleanUrl {

    private final String URI;

    protected static final Logger logger = LoggerFactory.getLogger(CleanUrl.class);

    /**
     * A URI for harvesting
     * @param URI 
     */
    public CleanUrl(String URI) {
        this.URI = URI;
    }

    @Override
    public String toString() {
        String cleanUrl = this.URI;
        if (cleanUrl.contains("?")) {
            cleanUrl = this.URI.substring(0, this.URI.lastIndexOf("?"));
            if (!this.URI.equals(cleanUrl)) {
                logger.debug("SOFTFAIL: Stored URI of repository " + this.URI + " endpoint contains GET parameters. Expected: " + cleanUrl);
            }
        }
        return cleanUrl;
    }

}
