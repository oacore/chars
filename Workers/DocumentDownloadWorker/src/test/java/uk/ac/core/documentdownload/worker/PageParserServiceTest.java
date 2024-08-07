package uk.ac.core.documentdownload.worker;

import java.util.List;
import org.junit.jupiter.api.Test;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author mc26486
 */
public class PageParserServiceTest {

    public PageParserServiceTest() {
    }


    /**
     * Test of extractSignPostingUrls method, of class PageParserService.
     */
    @Test
    public void testExtractSignPostingUrls() {
        System.out.println("extractSignPostingUrls");
        String[] values = {"<http://orcid.org/0000-0002-0715-6126> ; rel=\"author\"",
            "<http://orcid.org/0000-0003-3749-8116> ; rel=\"author\"",
            "<http://orcid.org/0000-0002-0715-6127.pdf> ; rel=\"item\"",
            "<http://orcid.org/0000-0002-0715-6128.pdf> ; rel=\"item\"; type=\"application/pdf \"",
        "https://www.openstarts.units.it/bitstream/10077/15737/1/Futuribili_22_Gori.pdf; rel=\"item\"; type=\"application/pdf\""};
        PageParserService instance = new PageParserService();
        List<CrawlingUrl> crawlingUrls = instance.extractSignPostingUrls(values,"",1);
        System.out.println("res = " + crawlingUrls);
        assertEquals(3, crawlingUrls.size());
    }

}
