package uk.ac.core.ExtendedMetadataProcessWorker.DownloadStrategies;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.ExtendedMetadataProcessWorker.worker.MetadataPageProcessTaskItem;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.dataprovider.logic.entity.DataProvider;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class OxfordDownloadStrategy
        implements ExtendedMetadataDownloadStrategy<MetadataPageProcessTaskItem, Document> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OxfordDownloadStrategy.class);
    private static final int OXFORD_REPO_ID = 88;
    private static final String URL_PREFIX = "https://ora.ox.ac.uk/objects/";

    @Override
    public boolean isCompatible(DataProvider dataProvider) {
        return dataProvider.getId() == OXFORD_REPO_ID;
    }

    @Override
    public Optional<MetadataPageProcessTaskItem> repositoryDocumentToMetadataPageProcessTaskItem(
            RepositoryDocument repositoryDocument) {
        return Optional.of(
                new MetadataPageProcessTaskItem(repositoryDocument.getIdDocument(), repositoryDocument.getOai())
        );
    }

    @Override
    public Document downloadMetadataPage(MetadataPageProcessTaskItem taskItem, File saveLocation) throws Exception {
        String body = "";

        if (saveLocation.exists()) {
            body = new String(FileUtils.readFileToByteArray(saveLocation));
        } else {
            URI url = this.buildUrlFromOai(taskItem.getOai());
            if (url != null) {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpGet httpget = new HttpGet(url);
                    httpget.addHeader("User-Agent", "CORE Bot - Contact: theteam@core.ac.uk");

                    ResponseHandler<String> responseHandler = httpResponse -> {
                        int status = httpResponse.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = httpResponse.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    };

                    body = httpClient.execute(httpget, responseHandler);
                    FileUtils.writeByteArrayToFile(saveLocation, body.getBytes());
                } catch (Exception e) {
                    logger.error("Exception: ", e);
                }
            }
        }
        return Jsoup.parse(body);
    }

    private URI buildUrlFromOai(String oai) throws URISyntaxException {
        String[] parts = oai.split(":");
        if (parts.length == 4 && parts[2].equals("uuid")) {
            return new URI(URL_PREFIX.concat(parts[2]).concat(":").concat(parts[3]));
        }
        return null;
    }

    @Override
    public int attachmentCount(MetadataPageProcessTaskItem taskItem, Document data) {
        final String cssSelector = "a.download-full-text-link";
        Elements attachments = data.select(cssSelector);
        return attachments.size();
    }

    @Override
    public LocalDateTime repositoryMetadataRecordPublishDate(MetadataPageProcessTaskItem taskItem, Document data) {
        final String cssSelector = "span.text-record_created_date";
        Element depositDateElement = data.select(cssSelector).first();
        String dateString = depositDateElement.text();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, formatter).atStartOfDay();
    }
}
