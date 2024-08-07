package uk.ac.core.ExtendedMetadataProcessWorker.DownloadStrategies;

import org.junit.Test;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.DocumentUrl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class DSpaceDownloadStrategyTest {

    @Test
    public void locateDocumentUrl() {
        DSpaceDownloadStrategy dSpaceDownloadStrategy = new DSpaceDownloadStrategy();
        String url = "http://hdl.handle.com/123/123";
        List<DocumentUrl> list = Arrays.asList(new DocumentUrl(1, 1, url, 0, PDFUrlSource.OAIPMH));
        assertEquals(url, dSpaceDownloadStrategy.locateDocumentUrl(list, "").get().getUrl());

        url= "http://repository.cam.ac.uk/123/123";
        list = Arrays.asList(new DocumentUrl(1, 1, url, 0, PDFUrlSource.OAIPMH));
        Pattern compile = Pattern.compile("/\\d+/\\d+$");
        assertTrue(dSpaceDownloadStrategy.locateDocumentUrl(list, "").isPresent());
                //"\\/[0-9]+\\/[0-9]+$")); // '/[0-9]+/[0-9]+$'

    }

    @Test
    public void locateDocumentUrlWithMultipleUrlsIncludingDoiOrg() {
        DSpaceDownloadStrategy dSpaceDownloadStrategy = new DSpaceDownloadStrategy();
        String expectedUrl = "https://eresearch.qmu.ac.uk/handle/20.500.12289/4477";
        List<DocumentUrl> list = Arrays.asList(
                new DocumentUrl(1, 2, "http://doi.org/10.1016/S0031-9406(05)60055-7", 0, PDFUrlSource.OAIPMH),
                new DocumentUrl(2, 2, expectedUrl, 0, PDFUrlSource.OAIPMH),
                new DocumentUrl(3, 2, "http://doi:10.1016/S0031-9406(05)60055-7", 0, PDFUrlSource.OAIPMH) // This is a value in our real metadata
        );
        assertEquals(expectedUrl, dSpaceDownloadStrategy.locateDocumentUrl(list, "").get().getUrl());
    }

    @Test
    public void locateDocumentUrlExcludeDoiLinks() {
        DSpaceDownloadStrategy dSpaceDownloadStrategy = new DSpaceDownloadStrategy();
        List<DocumentUrl> list = Arrays.asList(
                new DocumentUrl(1, 2, "http://doi.org/10.1016/S0031-9406(05)60055-7", 0, PDFUrlSource.OAIPMH),
                new DocumentUrl(3, 2, "http://doi:10.1016/S0031-9406(05)60055-7", 0, PDFUrlSource.OAIPMH) // This is a value in our real metadata
        );
        assertEquals(Optional.empty(), dSpaceDownloadStrategy.locateDocumentUrl(list, ""));
    }
}