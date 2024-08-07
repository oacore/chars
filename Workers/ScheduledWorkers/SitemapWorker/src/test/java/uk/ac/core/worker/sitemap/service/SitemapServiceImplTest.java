package uk.ac.core.worker.sitemap.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.ac.core.worker.sitemap.collection.ESDumpDeserializerImpl;
import uk.ac.core.worker.sitemap.factory.ArticleUrls;
import uk.ac.core.worker.sitemap.generation.SitemapGenerator;
import uk.ac.core.worker.sitemap.generation.entity.sitemap.SitemapFile;
import uk.ac.core.worker.sitemap.generation.entity.sitemap.SitemapUrlProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SitemapServiceImplTest {

    @Mock
    private SitemapGenerator sitemapGenerator;

    @Mock
    private ESDumpDeserializerImpl esDumpDeserializer;

    @Captor
    private ArgumentCaptor<SitemapFile> sitemapsCaptor;

    @InjectMocks
    private SitemapServiceImpl sitemapService;

    private static final int FAKE_DOCUMENT_ID_1 = 1;
    private static final int FAKE_DOCUMENT_ID_2 = 2;

    private static final String PDF_EXTENSION = ".pdf";

    private static final String FULL_TEXT_DOWNLOAD_LINK_1 = "https://core.ac.uk/download/pdf/" + FAKE_DOCUMENT_ID_1 + PDF_EXTENSION;
    private static final String PREVIEW_LINK_1 = "https://core.ac.uk/display/" + FAKE_DOCUMENT_ID_1;
    private static final String PREVIEW_LINK_2 = "https://core.ac.uk/display/" + FAKE_DOCUMENT_ID_2;
    private static final String READER_LINK_1 = "https://core.ac.uk/reader/" + FAKE_DOCUMENT_ID_1;

    @Test
    // can't mock generic classes
    @SuppressWarnings("unchecked")
    public void sitemapsCorrectlyDefinedTest() throws Exception {

        doAnswer(invocation -> {
            Consumer<ArticleUrls> receiver = (Consumer<ArticleUrls>)invocation.getArguments()[1];
            receiver.accept(mockDocumentResponse());
            return null;
        }).when(esDumpDeserializer).deserialize(anyString(), any(Consumer.class));

        sitemapService.generateSitemaps();

        verify(esDumpDeserializer).deserialize(anyString(), any(Consumer.class));
        verify(sitemapGenerator).generateOne(sitemapsCaptor.capture(), any());
        verify(sitemapGenerator).generateIndexFile(any());

        assertTrue(areDefinedSitemapsValid(sitemapsCaptor.getValue()));
    }

    private ArticleUrls mockDocumentResponse() {
        List<String> stringUrls = new ArrayList<>();
        stringUrls.add(FULL_TEXT_DOWNLOAD_LINK_1);
        stringUrls.add(PREVIEW_LINK_1);
        stringUrls.add(READER_LINK_1);
        stringUrls.add(PREVIEW_LINK_2);

        return new ArticleUrls(stringUrls);
    }

    public boolean areDefinedSitemapsValid(SitemapFile sitemapFile) {

        if (sitemapFile.getUrls().size() != 4) {
            return false;
        }
        List<SitemapUrlProperty> sitemapUrls = sitemapFile.getUrls();

        return sitemapUrls.get(0).getLocation().equals(FULL_TEXT_DOWNLOAD_LINK_1) &&
                sitemapUrls.get(1).getLocation().equals(PREVIEW_LINK_1) &&
                sitemapUrls.get(2).getLocation().equals(READER_LINK_1) &&
                sitemapUrls.get(3).getLocation().equals(PREVIEW_LINK_2);
    }
}