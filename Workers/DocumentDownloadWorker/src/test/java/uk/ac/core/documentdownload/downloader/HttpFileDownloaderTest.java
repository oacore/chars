package uk.ac.core.documentdownload.downloader;

import crawlercommons.fetcher.AbortedFetchException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import uk.ac.core.database.service.repositories.RepositoryDomainException;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author samuel
 */
public class HttpFileDownloaderTest {
    
    public HttpFileDownloaderTest() {
    }
 
    /**
     * Test of downloadFileFromUrl method, of class HttpFileDownloader.
     */
    @Test @Disabled
    public void testDownloadFileFromUrl() throws Exception {
        System.out.println("downloadFileFromUrl");
        String currentUrl = "https://ore.exeter.ac.uk/repository/bitstream/10036/12415/2/Author%20version.pdf?";
        List<RepositoryDomainException> domainExceptions = new ArrayList<>();
        String repositoryDomain = "ore.exeter.ac.uk";
        List<String> additonalMimeTypesToAccept = new ArrayList<>();
        DownloadResult expResult = new DownloadResult();
        expResult.setStatusCode(200);
        DownloadResult result = new DomainAwareHttpFileDownload(true, domainExceptions, repositoryDomain, additonalMimeTypesToAccept, null)
                .obtainFile(currentUrl, System.getProperty("user.dir") + "/temp.pdf");
        assertEquals(expResult.getStatusCode(), result.getStatusCode());
    }
    
    @Test @Disabled
    public void testDownloadFile2FromUrl() throws Exception {
        System.out.println("downloadFileFromUrl");
        String currentUrl = "https://ore.exeter.ac.uk/repository/bitstream/10036/12415/2/Author%20version.pdf";
        List<RepositoryDomainException> domainExceptions = new ArrayList<>();
        String repositoryDomain = "ore.exeter.ac.uk";
        List<String> additonalMimeTypesToAccept = new ArrayList<>();
        DownloadResult expResult = new DownloadResult();
        expResult.setStatusCode(200);
        DownloadResult result = new DomainAwareHttpFileDownload(true, domainExceptions, repositoryDomain, additonalMimeTypesToAccept, null)
                .obtainFile(currentUrl, System.getProperty("user.dir") + "/temp.pdf");
        assertEquals(expResult.getStatusCode(), result.getStatusCode());
    }
    
    @Test @Disabled
    public void testDownloadFile3FromUrl() throws Exception {
        System.out.println("downloadFileFromUrl");
        String currentUrl = "https://ore.exeter.ac.uk/repository/bitstream/10036/12415/2/Author version.pdf";
        List<RepositoryDomainException> domainExceptions = new ArrayList<>();
        String repositoryDomain = "ore.exeter.ac.uk";
        List<String> additonalMimeTypesToAccept = new ArrayList<>();
        DownloadResult expResult = new DownloadResult();
        expResult.setStatusCode(200);
        DownloadResult result = new DomainAwareHttpFileDownload(true, domainExceptions, repositoryDomain, additonalMimeTypesToAccept, null)
                .obtainFile(currentUrl, System.getProperty("user.dir") + "/temp.pdf");
        assertEquals(expResult.getStatusCode(), result.getStatusCode());
    }
}
