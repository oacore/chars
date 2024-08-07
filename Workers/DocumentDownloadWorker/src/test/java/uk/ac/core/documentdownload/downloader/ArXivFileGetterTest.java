package uk.ac.core.documentdownload.downloader;

import crawlercommons.fetcher.AbortedFetchException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrlBucket;
import uk.ac.core.documentdownload.worker.DocumentDownloadIssueException;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ArXivFileGetterTest {

    @Test
    public void obtainFile() {

    }

    @Test
    public void parseArXivIdTest() {
        ArXivFileGetter g = new ArXivFileGetter(null, true);

        assertEquals("0704.0024", g.parseArXivId("http://arxiv.org/abs/0704.0024"));
        assertEquals("supr-con/9609004", g.parseArXivId("http://arxiv.org/abs/supr-con/9609004"));
        assertEquals("hep-th/9901001", g.parseArXivId("http://arxiv.org/abs/hep-th/9901001"));
        assertEquals("supr-con/9509001", g.parseArXivId("http://arxiv.org/pdf/supr-con/9509001.pdf"));
    }

    @Test
    public void parseArXivFolderDateTest() {
        ArXivFileGetter g = new ArXivFileGetter(null, true);

        assertEquals("0704", g.parseArXivFolderDate("0704.0024").get());
        assertEquals("9609", g.parseArXivFolderDate("supr-con/9609004").get());
        assertEquals("9901", g.parseArXivFolderDate("hep-th/9901001").get());
    }

    @Test
    public void obtainFileOldStyleUrlTest() throws Exception {
        ArXivFileGetter g = new ArXivFileGetter(new ObtainsFile() {
            @Override
            public boolean prepare(long documentId, String oai, CrawlingUrl crawlingUrl, CrawlingUrlBucket crawlingUrlBucket) throws DocumentDownloadIssueException, InterruptedException {
                return false;
            }

            @Override
            public DownloadResult obtainFile(String currentUrl, String path) throws AbortedFetchException {
                String url = "http://export.arxiv.org/pdf/supr-con/9609004";
                assertEquals(url, currentUrl);
                DownloadResult downloadResult = new DownloadResult();
                downloadResult.setBaseUrl(url);
                return downloadResult;
            }
        }, true);

        g.obtainFile("http://export.arxiv.org/abs/supr-con/9609004", System.getProperty("user.dir") + "/temp.pdf");
    }

    @Test
    public void getArXivFileLocationTest() throws AbortedFetchException {
        ArXivFileGetter g = new ArXivFileGetter(new ObtainsFile() {
            @Override
            public boolean prepare(long documentId, String oai, CrawlingUrl crawlingUrl, CrawlingUrlBucket crawlingUrlBucket) throws DocumentDownloadIssueException, InterruptedException {
                return false;
            }

            @Override
            public DownloadResult obtainFile(String currentUrl, String path) throws AbortedFetchException {
                assertEquals("http://export.arxiv.org/pdf/supr-con/9609004", currentUrl);
                DownloadResult downloadResult = new DownloadResult();
                downloadResult.setBaseUrl(currentUrl);
                return downloadResult;
            }
        }, true);

        assertEquals("/data/remote/arxiv/extracted/9901/hep-th9901001.pdf", g.getArXivFileLocation("hep-th/9901001").getAbsolutePath());
        assertEquals("/data/remote/arxiv/extracted/0704/0704.0024.pdf", g.getArXivFileLocation("0704.0024").getAbsolutePath());

    }
}
