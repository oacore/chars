package uk.ac.core.supervisor.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.LoggerFactory;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.supervisor.client.exceptions.FailedRequestException;

import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author lucasanastasiou
 */
public class SupervisorClient {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(SupervisorClient.class);

    protected final String supervisorUrl;
    private final Client client = Client.create();

    public SupervisorClient(String supervisorUrl) {
        this.supervisorUrl = supervisorUrl;
    }

    private ClientResponse sendGetRequest(String path) {
        logger.debug(this.supervisorUrl + path);
        WebResource webResource = client
                .resource(this.supervisorUrl).path(path);

        ClientResponse response = webResource.get(ClientResponse.class);
        return response;
    }

    public void sendHarvestRepositoryRequest(Integer repositoryId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/repository/harvest/" + repositoryId);

        System.out.println("resp = " + resp);
        System.out.println(resp.toString());

        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            logger.info("Repository " + repositoryId + " has been scheduled");
        } else {
            logger.warn("Repository " + repositoryId + " failed to schedule");
            throw new CHARSException();
        }
    }

    public void sendHarvestRepositoryRequest(Integer repositoryId, Date fromDate) throws CHARSException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        ClientResponse resp = this.sendGetRequest("task/repository/harvest/" + repositoryId + "/fromdate/" + format.format(fromDate));

        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            logger.info("Repository " + repositoryId + " has been scheduled" + fromDate);
        } else {
            logger.warn("Repository " + repositoryId + " failed to schedule");
            throw new CHARSException();
        }
    }

    public void sendHarvestRepositoryRequest(Integer repositoryId, Date fromDate, Date toDate) throws CHARSException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        ClientResponse resp = this.sendGetRequest("task/repository/harvest/" + repositoryId + "/fromdate/"
                + format.format(fromDate) + "/todate/" + format.format(toDate));

        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            logger.info("Repository " + repositoryId + " has been scheduled" + fromDate);
        } else {
            logger.warn("Repository {} failed to schedule. Response status: {}, response body: {}", repositoryId,
                    resp.getStatus(), resp.getEntity(String.class));
            throw new CHARSException();
        }
    }

    public void sendIndexRepositoryRequest(Integer repositoryId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/repository/index/" + repositoryId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Yey!");
        } else {
            throw new FailedRequestException("Could not send index request");
        }
    }

    public void sendMetadataDownloadRepositoryRequest(Integer repositoryId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/repository/metadata_download/" + repositoryId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Yey!");
        } else {
            throw new FailedRequestException("Could not send metadata download request");
        }
    }

    public void sendMetadataDownloadRepositoryRequest(Integer repositoryId, Date fromDate) throws CHARSException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        logger.debug("Request to {}", "task/repository/metadata_download/" + repositoryId + "/fromDate/"
                + format.format(fromDate));

        ClientResponse resp = this.sendGetRequest("task/repository/metadata_download/" + repositoryId + "/fromDate/"
                + format.format(fromDate));
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            logger.info("Metadata download for repository {} has been scheduled with from date '{}'" + fromDate, repositoryId, fromDate);
        } else {
            throw new FailedRequestException("Could not send metadata download request");
        }
    }

    public void sendMetadataDownloadRepositoryRequest(Integer repositoryId, Date fromDate, Date toDate) throws CHARSException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        logger.debug("Request to {}", "task/repository/metadata_download/" + repositoryId + "/fromDate/"
                + format.format(fromDate) + "/toDate/" + format.format(toDate));

        ClientResponse resp = this.sendGetRequest("task/repository/metadata_download/" + repositoryId + "/fromDate/"
                + format.format(fromDate) + "/toDate/" + format.format(toDate));
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            logger.info("Metadata download for repository {} has been scheduled with from date '{}' and to date '{}'" + fromDate, repositoryId, fromDate, toDate);
        } else {
            throw new FailedRequestException("Could not send metadata download request");
        }
    }

    public void sendMetadataExtractRepositoryRequest(Integer repositoryId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/repository/extract-metadata/" + repositoryId);

        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Yey!");
        } else {
            throw new FailedRequestException("Could not send metadata extract request");
        }
    }

    public void sendMetadataExtractRepositoryRequest(Integer repositoryId, Date fromDate) throws CHARSException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        logger.debug("Request to {}", "task/repository/extract-metadata/" + repositoryId + "/fromDate/"
                + format.format(fromDate));

        ClientResponse resp = this.sendGetRequest("task/repository/extract-metadata/" + repositoryId + "/fromDate/"
                + format.format(fromDate));
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            logger.info("Metadata extract for repository {} has been scheduled with from date '{}'" + fromDate, repositoryId, fromDate);
        } else {
            throw new FailedRequestException("Could not send metadata extract request");
        }
    }

    public void sendMetadataExtractRepositoryRequest(Integer repositoryId, Date fromDate, Date toDate) throws CHARSException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        logger.debug("Request to {}", "task/repository/extract-metadata/" + repositoryId + "/fromDate/"
                + format.format(fromDate) + "/toDate/" + format.format(toDate));

        ClientResponse resp = this.sendGetRequest("task/repository/extract-metadata/" + repositoryId + "/fromDate/"
                + format.format(fromDate) + "/toDate/" + format.format(toDate));
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            logger.info("Metadata extract for repository {} has been scheduled with from date '{}' and to date '{}'" + fromDate, repositoryId, fromDate, toDate);
        } else {
            throw new FailedRequestException("Could not send metadata extract request");
        }
    }

    public void sendPdfDownloadRepositoryRequest(Integer repositoryId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/repository/download-document/" + repositoryId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            logger.info("Request for pdf download task was sent");
        } else {
            throw new FailedRequestException("Could not send pdf download request");
        }
    }

    public void sendPdfDownloadRepositoryRequest(Integer repositoryId, Date fromDate, Date toDate) throws CHARSException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        ClientResponse resp = this.sendGetRequest("task/repository/download-document/" + repositoryId
                 + "/fromDate/" + format.format(fromDate) + "/toDate/" + format.format(toDate) );
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            logger.info("Request for pdf download task was sent");
        } else {
            throw new FailedRequestException("Could not send pdf download request");
        }
    }

    public void sendIndexItemRequest(Integer articleId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/item/index/" + articleId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Request Successful");
        } else {
            throw new FailedRequestException("Could not send index item request");
        }
    }

    public void sendWorkIndexItemRequest(Integer articleId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/item/works-index/" + articleId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Request Successful");
        } else {
            logger.error("There are problem with sending request. Code: {}", resp.getStatus());
            throw new FailedRequestException("Could not send index item request");
        }
    }
    
    public void sendIndexItemRequest(Integer articleId, DeletedStatus deletedStatus) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/item/index/" + articleId + "/deleted/" + deletedStatus.getValue());
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Request Successful");
        } else {
            throw new FailedRequestException("Could not send index item request");
        }
    }

    public void sendTextExtractItemRequest(Integer articleId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/item/text-extract/" + articleId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Yey!");
        } else {
            throw new FailedRequestException("Could not send extract text item request");
        }
    }

    public void sendGrobidExtractItemRequest(Integer articleId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/item/grobid-extract/" + articleId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Yey!");
        } else {
            throw new FailedRequestException("Could not send grobid extract item request");
        }
    }

    public void sendThumbnailGenerateItemRequest(Integer articleId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/item/thumbnail-generate/" + articleId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Yey!");
        } else {
            throw new FailedRequestException("Could not send generate thumbnail request");
        }
    }

    public void sendItemProcessRequest(Integer articleId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/item/process/" + articleId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Yey!");
        } else {
            throw new FailedRequestException("Could not send process item request");
        }
    }

    public void sendReindexItemRequest(String indexName, Integer articleId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/item/reindex/" + indexName + "/" + articleId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Yey!");
        } else {
            System.out.println("resp = " + resp.toString());
            throw new FailedRequestException("Could not send reindex item request");
        }
    }

    public void sendRioxxComplianceRepositoryRequest(Integer repositoryId) throws CHARSException {

        ClientResponse resp = this.sendGetRequest("task/repository/rioxx-compliance/" + repositoryId);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Yey!");
        } else {
            throw new FailedRequestException("Could not send pdf download request");
        }
    }
}
