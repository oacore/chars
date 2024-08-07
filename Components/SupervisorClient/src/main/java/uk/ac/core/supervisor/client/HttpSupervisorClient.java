package uk.ac.core.supervisor.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.common.model.article.DeletedStatus;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpSupervisorClient extends SupervisorClient {
    private static final Logger log = LoggerFactory.getLogger(HttpSupervisorClient.class);

    public HttpSupervisorClient(String supervisorUrl) {
        super(supervisorUrl);
    }

    private HttpResponse sendGetRequest(String path) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String fullUrl = this.supervisorUrl.concat(path);
            HttpGet get = new HttpGet(fullUrl);
            log.info("Sending Supervisor request ...");
            log.info("URL = {}", fullUrl);
            return httpClient.execute(get);
        } catch (Exception e) {
            log.error("Exception while sending Supervisor request: " + path, e);
            return null;
        }
    }

    private void logDetails(HttpResponse resp) {
        if (resp != null) {
            log.info("Response code: {}", resp.getStatusLine().getStatusCode());
        } else {
            log.error("Failed to send request");
            log.error("Check the logs above for details");
        }
    }

    @Override
    public void sendHarvestRepositoryRequest(Integer repositoryId) {
        HttpResponse resp = this.sendGetRequest("task/repository/harvest/" + repositoryId);
        this.logDetails(resp);
    }

    @Override
    public void sendHarvestRepositoryRequest(Integer repositoryId, Date fromDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        HttpResponse resp = this.sendGetRequest(
                "task/repository/harvest/" + repositoryId + "/fromdate/" + format.format(fromDate));
        this.logDetails(resp);
    }

    @Override
    public void sendHarvestRepositoryRequest(Integer repositoryId, Date fromDate, Date toDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        HttpResponse resp = this.sendGetRequest(
                "task/repository/harvest/" + repositoryId +
                        "/fromdate/" + format.format(fromDate) +
                        "/todate/" + format.format(toDate));
        this.logDetails(resp);
    }

    @Override
    public void sendIndexRepositoryRequest(Integer repositoryId) {
        HttpResponse resp = this.sendGetRequest("task/repository/index/" + repositoryId);
        this.logDetails(resp);
    }

    @Override
    public void sendMetadataDownloadRepositoryRequest(Integer repositoryId) {
        HttpResponse resp = this.sendGetRequest("task/repository/metadata_download/" + repositoryId);
        this.logDetails(resp);
    }

    @Override
    public void sendMetadataDownloadRepositoryRequest(Integer repositoryId, Date fromDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        HttpResponse resp = this.sendGetRequest(
                "task/repository/metadata_download/" + repositoryId + 
                        "/fromDate/" + format.format(fromDate));
        this.logDetails(resp);
    }

    @Override
    public void sendMetadataDownloadRepositoryRequest(Integer repositoryId, Date fromDate, Date toDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        HttpResponse resp = this.sendGetRequest(
                "task/repository/metadata_download/" + repositoryId + 
                        "/fromDate/" + format.format(fromDate) + 
                        "/toDate/" + format.format(toDate));
        this.logDetails(resp);
    }

    @Override
    public void sendMetadataExtractRepositoryRequest(Integer repositoryId) {
        HttpResponse resp = this.sendGetRequest("task/repository/extract-metadata/" + repositoryId);
        this.logDetails(resp);
    }

    @Override
    public void sendMetadataExtractRepositoryRequest(Integer repositoryId, Date fromDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        HttpResponse resp = this.sendGetRequest(
                "task/repository/extract-metadata/" + repositoryId + 
                        "/fromDate/" + format.format(fromDate));
        this.logDetails(resp);
    }

    @Override
    public void sendMetadataExtractRepositoryRequest(Integer repositoryId, Date fromDate, Date toDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        HttpResponse resp = this.sendGetRequest(
                "task/repository/extract-metadata/" + repositoryId + 
                        "/fromDate/" + format.format(fromDate) + 
                        "/toDate/" + format.format(toDate));
        this.logDetails(resp);
    }

    @Override
    public void sendPdfDownloadRepositoryRequest(Integer repositoryId) {
        HttpResponse resp = this.sendGetRequest("task/repository/download-document/" + repositoryId);
        this.logDetails(resp);
    }

    @Override
    public void sendPdfDownloadRepositoryRequest(Integer repositoryId, Date fromDate, Date toDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        HttpResponse resp = this.sendGetRequest(
                "task/repository/download-document/" + repositoryId + 
                        "/fromDate/" + format.format(fromDate) + 
                        "/toDate/" + format.format(toDate));
        this.logDetails(resp);
    }

    @Override
    public void sendIndexItemRequest(Integer articleId) {
        HttpResponse resp = this.sendGetRequest("task/item/index/" + articleId);
        this.logDetails(resp);
    }

    @Override
    public void sendWorkIndexItemRequest(Integer articleId) {
        HttpResponse resp = this.sendGetRequest("task/item/works-index/" + articleId);
        this.logDetails(resp);
    }

    @Override
    public void sendIndexItemRequest(Integer articleId, DeletedStatus deletedStatus) {
        HttpResponse resp = this.sendGetRequest("task/item/index/" + articleId + "/deleted/" + deletedStatus.getValue());
        this.logDetails(resp);
    }

    @Override
    public void sendTextExtractItemRequest(Integer articleId) {
        HttpResponse resp = this.sendGetRequest("task/item/text-extract/" + articleId);
        this.logDetails(resp);
    }

    @Override
    public void sendGrobidExtractItemRequest(Integer articleId) {
        HttpResponse resp = this.sendGetRequest("task/item/grobid-extract/" + articleId);
        this.logDetails(resp);
    }

    @Override
    public void sendThumbnailGenerateItemRequest(Integer articleId) {
        HttpResponse resp = this.sendGetRequest("task/item/thumbnail-generate/" + articleId);
        this.logDetails(resp);
    }

    @Override
    public void sendItemProcessRequest(Integer articleId) {
        HttpResponse resp = this.sendGetRequest("task/item/process/" + articleId);
        this.logDetails(resp);
    }

    @Override
    public void sendReindexItemRequest(String indexName, Integer articleId) {
        HttpResponse resp = this.sendGetRequest("task/item/reindex/" + indexName + "/" + articleId);
        this.logDetails(resp);
    }

    @Override
    public void sendRioxxComplianceRepositoryRequest(Integer repositoryId) {
        HttpResponse resp = this.sendGetRequest("task/repository/rioxx-compliance/" + repositoryId);
        this.logDetails(resp);
    }
}
