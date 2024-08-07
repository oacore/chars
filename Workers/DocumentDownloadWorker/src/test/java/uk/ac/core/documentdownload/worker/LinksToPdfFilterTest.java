package uk.ac.core.documentdownload.worker;

import org.junit.jupiter.api.Test;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author samuel
 */
public class LinksToPdfFilterTest {
    
    public LinksToPdfFilterTest() {
    }

    /**
     * Test of canDownloadUrl method, of class LinksToPdfFilter.
     */
    @Test
    public void testMediaInUrl() {
        CrawlingUrl url = new CrawlingUrl(
                "https://repository.rothamsted.ac.uk/item/87q41/environmental-applications-of-soil-geochemical-data", 
                "https://repository.rothamsted.ac.uk/item/86x2w/in-situ-bioremediation-of-metal-contaminated-soils-using-crops-of-hyperaccumulator-plants-potentials-and-future-prospects-for-a-developing-technology", 
                1);
        LinksToPdfFilter instance = new LinksToPdfFilter();
        boolean expResult = false;
        boolean result = instance.canDownloadUrl(url);
        assertEquals(expResult, result);
    }
    
        /**
     * Test of canDownloadUrl method, of class LinksToPdfFilter.
     */
    @Test
    public void testMediaGoodInUrl() {
        CrawlingUrl url = new CrawlingUrl(
                "https://www.neliti.com/publications/1045/rekrutmen-calon-anggota-legislatif-partai-demokrat-kabupaten-bolaang-mongondow-t", 
                "https://media.neliti.com/media/publications/1045-ID-rekrutmen-calon-anggota-legislatif-partai-demokrat-kabupaten-bolaang-mongondow-t.pdf", 
                1);
        LinksToPdfFilter instance = new LinksToPdfFilter();
        boolean expResult = true;
        boolean result = instance.canDownloadUrl(url);
        assertEquals(expResult, result);
    }
    
}
