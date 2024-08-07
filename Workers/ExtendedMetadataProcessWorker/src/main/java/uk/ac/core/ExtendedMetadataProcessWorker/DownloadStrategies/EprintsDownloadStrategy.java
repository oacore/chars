/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.ExtendedMetadataProcessWorker.DownloadStrategies;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.ExtendedMetadataProcessWorker.worker.ExtendedMetadataProcessWorker;
import uk.ac.core.ExtendedMetadataProcessWorker.worker.MetadataPageProcessTaskItem;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.util.TextToDateTime;
import uk.ac.core.dataprovider.logic.entity.DataProvider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author samuel
 */
@Service
public class EprintsDownloadStrategy implements ExtendedMetadataDownloadStrategy<MetadataPageProcessTaskItem, Document> {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(EprintsDownloadStrategy.class);
    private DataProvider dataProvider;


    public EprintsDownloadStrategy() {
    }

    @Override
    public boolean isCompatible(DataProvider dataProvider) {
        // Save this for later...
        // @todo can we inject this through a proper method interface?        
        this.dataProvider = dataProvider;
        return dataProvider.getSoftware().toLowerCase().contains("eprints");
    }

    @Override
    public Optional<MetadataPageProcessTaskItem> repositoryDocumentToMetadataPageProcessTaskItem(RepositoryDocument repositoryDocument) {
        String[] parts = repositoryDocument.getOai().replace("http://", "").split(":");
        // We only support EPrints and the oai:domain.com:[id] format
        if (parts.length == 3 && "oai".equals(parts[0])) {
            return Optional.of(
                    new MetadataPageProcessTaskItem(
                            repositoryDocument.getIdDocument(),
                            repositoryDocument.getOai())
            );
        } else {
            // if an eprints OAI does not have a 3 part OAI, we don't yet know 
            // how to deal with it!

            // Good:
            //  oai:oro.open.ac.uk:132
            //  oai:eprints.mdx.ac.uk:234
            // Bad:
            //  Not yet found any examples!
            return Optional.empty();
        }

    }

    /**
     * Download the metadata page from the repository.
     * <p>
     * For EPrints we:
     * <p>
     * Convert the OAI into a url to get the metadata page
     * Save the downloaded metadata page to saveLocation
     * Convert the downloaded metadata page to a Document to be used later
     *
     * @param taskItem
     * @param saveLocation
     * @return
     * @throws URISyntaxException
     * @todo can we inject the HTTPClient as a service?
     */
    @Override
    public Document downloadMetadataPage(MetadataPageProcessTaskItem taskItem, File saveLocation) throws URISyntaxException {

        String body = "";

        String oai = taskItem.getOai();
        final int id = taskItem.getDocumentId();
        // Convert OAI to a useable url
        String[] parts = oai.replace(".OAI2", "").replace("http://", "").split(":");
        if (parts.length == 3 && "oai".equals(parts[0])) {
            String urlToTry = parts[1] + "/" + parts[2];
        }
        URL repositoryOaiPmhUrl = null;
        String protocol = "http";
        try {
            repositoryOaiPmhUrl = new URL(this.dataProvider.getUrlOaipmh()); // don't log and use the default protocol
        } catch (MalformedURLException ex) {
            Logger.getLogger(EprintsDownloadStrategy.class.getName()).log(Level.SEVERE, null, ex);
        }
        protocol = repositoryOaiPmhUrl.getProtocol();

        // todo, harvest url content
        if (saveLocation.exists()) {
            try {
                // @todo do we want to just skip these documents once they are processed?
                // @todo do we want to harvest every 30 days?
                body = FileUtils.readFileToString(saveLocation);
            } catch (IOException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        } else {
            try {

                URI urlToTry = this.composeUrlToTry(protocol, parts);

                try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                    HttpGet httpget = new HttpGet(urlToTry);
                    httpget.addHeader("User-Agent", "CORE Bot - Contact: theteam@core.ac.uk");

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ExtendedMetadataProcessWorker.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    // Create a custom response handler
                    ResponseHandler<String> responseHandler = (final HttpResponse response) -> {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    };
                    body = httpclient.execute(httpget, responseHandler);
                    FileUtils.writeByteArrayToFile(saveLocation, body.getBytes());
                } catch (IOException ex) {
                    Logger.getLogger(EprintsDownloadStrategy.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(ExtendedMetadataProcessWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Jsoup.parse(body);
    }

    protected URI composeUrlToTry(String protocol, String[] parts) throws URISyntaxException, MalformedURLException {
        return new URL(protocol, parts[1], "/" + parts[2]).toURI();
    }

    @Override
    public int attachmentCount(MetadataPageProcessTaskItem taskItem, Document data) {
        Set<String> urlList = new HashSet<>();
        final List<String> attachmentMetaTags = new ArrayList<>(Arrays.asList(
                "eprints.document_url"
        ));
        attachmentMetaTags.stream().map((tagName) -> data.select("meta[name='" + tagName + "']")).forEachOrdered((metalinks) -> {
            metalinks.forEach((Element element) -> {
                String metaLink = element.attr("content");
                if (!metaLink.isEmpty()) {
                    logger.info("#{} -> {}", taskItem.getDocumentId(), metaLink);
                    if (!urlList.add(metaLink)) {
                        logger.info("#{} already added.", taskItem.getDocumentId(), metaLink);
                    }
                    ;
                }
            });
        });
        return urlList.size();
    }

    /**
     * @param taskItem
     * @param data
     * @return
     */
    @Override
    public LocalDateTime repositoryMetadataRecordPublishDate(MetadataPageProcessTaskItem taskItem, Document data) {
        Elements firstCompliantDeposit = data.getElementsByAttributeValueContaining("name", "eprints.hoa_date_fcd");
        if (firstCompliantDeposit.size() > 0) {
            String dateFcd = firstCompliantDeposit.attr("content");
            if (!dateFcd.isEmpty()) {
                return new TextToDateTime(dateFcd).asLocalDateTime();
            }
        }

        Elements trs = data.body().getElementsMatchingOwnText("Date deposited|Date Deposited|Deposit Date|date deposited/i");
        if (trs.isEmpty()) {
            trs = data.body().getElementsContainingOwnText("Deposited On:");
        }
        if (trs.isEmpty()) {
            trs = data.body().getElementsContainingOwnText("Date Deposited");
        }

        for (Element element : trs) {
            Elements elm = element.parent().getAllElements();
            String elmText = elm.get(2).text();
            logger.debug("Document #{} ({}) Deposit Date: {}", taskItem.getDocumentId(), taskItem.getOai(), elmText);
            return new TextToDateTime(elmText).asLocalDateTime();
        }
        trs = data.body().getElementsContainingOwnText("Accepted");

        for (Element element : trs) {
            Elements elm = element.parent().getAllElements();
            String elmText = elm.get(1).text();
            logger.debug("Document #{} ({}) Deposit Date: {}", taskItem.getDocumentId(), taskItem.getOai(), elmText);
            LocalDateTime localDateTime;
            try {
                localDateTime = new TextToDateTime(elmText).asLocalDateTime();
            } catch (Exception e) {
                continue;
            }
            return localDateTime;
        }

        trs = data.body().getElementsMatchingOwnText("Source Publication Date:");
        for (Element element : trs) {
            try {
                Element parent = element.parent();
                TextNode node = (TextNode) parent.childNode(1);
                logger.debug("Document #{} ({}) Deposit Date: {}", taskItem.getDocumentId(), taskItem.getOai(), node.text());
                return new TextToDateTime(node.text()).asLocalDateTime();
            } catch (Exception e) {
                continue;
            }
        }

        return null;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
}
