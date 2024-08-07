package uk.ac.core.documentdownload.downloader.crawling;

import java.net.MalformedURLException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author mc26486
 */
public class CrawlingDelayMultitonTest {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(CrawlingDelayMultitonTest.class);
    
    public CrawlingDelayMultitonTest() {
    }


    /**
     * Test of isAllowed method, of class CrawlingDelayMultiton.
     */
    @Test
    @Disabled
    public void testIsAllowed() {
        //@Test
        try {

            CrawlingDelayMultiton instance = CrawlingDelayMultiton.getInstanceFromUrl("https://cgspace.cgiar.org", null);

            assertFalse(instance.isAllowed("https://cgspace.cgiar.org/discover"));
            assertTrue(instance.isAllowed("https://cgspace.cgiar.org/htmlmap"));

        } catch (MalformedURLException ex) {
            logger.error(ex.getMessage());
        }

    }

}
