package uk.ac.core.ExtendedMetadataProcessWorker.UrlFilter;

import org.junit.Test;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.DocumentUrl;

import static org.junit.Assert.*;

public class DSpaceDocumentUrlFilterTest {

    @Test
    public void allow() {
        Filter documentUrl = new DSpaceDocumentUrlFilter();
        assertTrue(documentUrl.allow(new DocumentUrl(1, 1, "http://hdl.handle.com/123/123", 0, PDFUrlSource.OAIPMH)));
        assertTrue(documentUrl.allow(new DocumentUrl(1, 1, "http://repository.cam.ac.uk/123/123", 0, PDFUrlSource.OAIPMH)));
    }

    @Test
    public void locateDocumentUrlWithMultipleUrlsIncludingDoiOrg() {
        Filter documentUrl = new DSpaceDocumentUrlFilter();
        assertTrue(documentUrl.allow(new DocumentUrl(1, 1, "https://eresearch.qmu.ac.uk/handle/20.500.12289/4477", 0, PDFUrlSource.OAIPMH)));
        assertTrue(documentUrl.allow(new DocumentUrl(1, 1, "http://hdl.handle.net/2164/6156", 0, PDFUrlSource.OAIPMH)));
        assertFalse(documentUrl.allow(new DocumentUrl(1, 1, "http://doi.org/10.1016/S0031-9406(05)60055-7", 0, PDFUrlSource.OAIPMH)));
        assertFalse(documentUrl.allow(new DocumentUrl(1, 1, "http://doi:10.1016/S0031-9406(05)60055-7", 0, PDFUrlSource.OAIPMH)));
    }

    @Test
    public void locateDocumentUrlExcludeDoiLinks() {
        DSpaceDocumentUrlFilter documentUrl = new DSpaceDocumentUrlFilter();
        assertFalse(documentUrl.allow(new DocumentUrl(1, 1, "10.0042/amad-43003", 0, PDFUrlSource.OAIPMH)));
        assertFalse(documentUrl.allow(new DocumentUrl(1, 1, "http://doi:10.1016/S0031-9406(05)60055-7", 0, PDFUrlSource.OAIPMH)));
    }
}