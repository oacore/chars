/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.ExtendedMetadataProcessWorker.DownloadStrategies;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.ExtendedMetadataProcessWorker.UrlFilter.DSpaceDocumentUrlFilter;
import uk.ac.core.ExtendedMetadataProcessWorker.UrlFilter.Filter;
import uk.ac.core.ExtendedMetadataProcessWorker.worker.MetadataPageProcessTaskItem;
import uk.ac.core.common.model.legacy.DocumentUrl;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.util.TextToDateTime;
import uk.ac.core.common.util.downloader.DownloadResult;
import uk.ac.core.common.util.downloader.HttpFileDownloader;
import uk.ac.core.database.service.document.DocumentUrlDAO;
import uk.ac.core.dataprovider.logic.entity.DataProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author samuel
 */
@Service
public class DSpaceDownloadStrategy implements ExtendedMetadataDownloadStrategy<MetadataPageProcessTaskItem, Document> {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(DSpaceDownloadStrategy.class);

    DataProvider dataProvider;

    @Autowired
    DocumentUrlDAO documentUrlDAO;

    public DSpaceDownloadStrategy() {
    }

    @Override
    public boolean isCompatible(DataProvider dataProvider) {
        // Save this for later...
        // @todo can we inject this through a proper method interface?        
        this.dataProvider = dataProvider;
        return dataProvider.getSoftware().toLowerCase().contains("dspace");
    }

    @Override
    public Optional<MetadataPageProcessTaskItem> repositoryDocumentToMetadataPageProcessTaskItem(RepositoryDocument repositoryDocument) {
        String[] parts = repositoryDocument.getOai().split("/");
        return Optional.of(
                new MetadataPageProcessTaskItem(
                        repositoryDocument.getIdDocument(),
                        repositoryDocument.getOai())
        );

    }

    /**
     * Download the metadata page from the repository.
     * <p>
     * For EPrints we:
     * <p>
     * Convert the OAI into a url to get the metadata page Save the downloaded
     * metadata page to saveLocation Convert the downloaded metadata page to a
     * Document to be used later
     *
     * @param taskItem
     * @param saveLocation
     * @return
     * @throws URISyntaxException
     * @todo can we inject the HTTPClient as a service?
     */
    @Override
    public Document downloadMetadataPage(MetadataPageProcessTaskItem taskItem, File saveLocation) throws Exception {

        String body = "";

        final String oai = taskItem.getOai();
        final int id = taskItem.getDocumentId();

        if (saveLocation.exists()) {
            try {
                // @todo do we want to just skip these documents once they are processed?
                // @todo do we want to harvest every 30 days?
                body = FileUtils.readFileToString(saveLocation);
            } catch (IOException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        } else {
            List<DocumentUrl> urls = documentUrlDAO.load(id);

            Optional<DocumentUrl> metadataPageUrl = locateDocumentUrl(urls, oai);
            String repositoryDocumentUrl;
            if (metadataPageUrl.isPresent()) {

                try {
                    repositoryDocumentUrl = metadataPageUrl.get().getUrl();
                    if (repositoryDocumentUrl.contains("hdl.handle")) {
                        repositoryDocumentUrl = this.convertHdlHandleToRepositoryUrl(repositoryDocumentUrl);
                    }
                    String showFull = "show=full";
                    if (oai.contains("brunel") || oai.contains("stir.ac.uk") || oai.contains("spiral.imperial.ac.uk")) {
                        showFull = "mode=full";
                    }
                    String toDownload = repositoryDocumentUrl + ";jsessionid=123?" + showFull;
                    // special rule for Apollo repository (ID 27)
                    if (oai.contains("cam.ac.uk")) {
                        toDownload = repositoryDocumentUrl + "/full;jsessionid=123";
                    }
                    logger.info("Downloading from {}", toDownload);

                    DownloadResult downloadResult = HttpFileDownloader.downloadFileFromUrl(toDownload);
                    if (!downloadResult.getBaseUrl().contains("?" + showFull)) {

                        downloadResult = HttpFileDownloader.downloadFileFromUrl(downloadResult.getBaseUrl() + "?" + showFull);

                    }
                    FileUtils.writeByteArrayToFile(saveLocation, downloadResult.getContent());
                    body = new String(downloadResult.getContent());
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DSpaceDownloadStrategy.class.getName()).log(Level.SEVERE, "Interrupted while sleeping", ex);
                    }
                } catch (IOException e) {
                    logger.error(String.format("Failed to write to {1}", saveLocation));
                    throw e;
                }

            }
        }
        return Jsoup.parse(body);
    }

    public Optional<DocumentUrl> locateDocumentUrl(List<DocumentUrl> urls, String oai) {
        Filter filter = new DSpaceDocumentUrlFilter();
        String domain = "";
        if (oai.startsWith("oai:")) {
            domain = oai.replace("oai:", "").substring(0, oai.indexOf(":"));
        } else {
            domain = "";
        }
        return urls.stream().filter(documentUrl -> filter.allow(documentUrl)).sorted(new Comparator<DocumentUrl>() {
            @Override
            public int compare(DocumentUrl o1, DocumentUrl o2) {
                if (o1.getUrl().contains(oai)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }).findFirst();
    }

    private String convertHdlHandleToRepositoryUrl(String repositoryDocumentUrl) {
        String[] split = repositoryDocumentUrl.split("/");
        // special rule for Spiral
        if (this.dataProvider.getId() == 105) {
            return repositoryDocumentUrl.replace("hdl.handle.net", "spiral.imperial.ac.uk/handle");
        }
        if (split.length > 4) {
            return dataProvider.getUrlHomepage() + "/handle/" + split[3] + "/" + split[4];
        }
        return repositoryDocumentUrl;
    }


    @Override
    public int attachmentCount(MetadataPageProcessTaskItem taskItem, Document data) {
        Elements trs = data.body().getElementsByAttributeValueContaining("href", "/bitstream/");
        Set<String> urlList = new HashSet<>();
        for (Element element : trs) {
            String url = element.attr("href");
            urlList.add(url);

        }
        logger.debug("Document #{} ({}) Attachments count: {}", taskItem.getDocumentId(), taskItem.getOai(), urlList.size());

        return urlList.size();
    }

    /**
     * @param taskItem
     * @param data
     * @return
     */
    @Override
    public LocalDateTime repositoryMetadataRecordPublishDate(MetadataPageProcessTaskItem taskItem, Document data
    ) {
        boolean hasAttachment = false;
        Elements trs = data.body().getElementsContainingOwnText("dc.date.available");
        for (Element element : trs) {
            Elements elm = element.parent().getAllElements();
            String elmText = elm.get(2).text();
            logger.debug("Document #{} ({}) Deposit Date: {}", taskItem.getDocumentId(), taskItem.getOai(), elmText);
            return new TextToDateTime(elmText).asLocalDateTime();
        }
        return null;
    }

}
