package uk.ac.core.services.web.recommender.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.document.DocumentDAO;
import uk.ac.core.elasticsearch.entities.ElasticSearchSimilarDocument;

/**
 * @author mc26486
 */
@Service
public class PostfilteringService {

    @Autowired
    private DocumentDAO documentDAO;

    HttpClient httpClient = HttpClientBuilder
            .create()
            .disableRedirectHandling()
            .build();

    public List<ElasticSearchSimilarDocument> postFilter(List<ElasticSearchSimilarDocument> results, int size) {
        List<ElasticSearchSimilarDocument> filtered = new ArrayList<>();
        int counter = 0;
        for (ElasticSearchSimilarDocument similarDocument : results) {
//            if (documentDAO.getPreviewStatus(similarDocument.getId()).equals(Boolean.TRUE)) {
            if (!similarDocument.getDownloadUrl().isEmpty()) {

                String documentTitle = similarDocument.getTitle();
                // filter for upperCase titles
                if (!documentTitle.equals(documentTitle.toUpperCase())) {
                    filtered.add(similarDocument);
                    counter++;
                }
            }
            if (counter >= size) {
                break;
            }
        }
        return filtered;
    }

    /**
     * Returns a boolean to determine whether a image preview exists for an item
     *
     * @param documentId
     * @return
     */
    private Boolean getPreviewStatus(String documentId) {

        HttpHead headRequest = new HttpHead("https://fileserver.core.ac.uk/previews/" + documentId + "/medium");

        try {
            HttpResponse response = httpClient.execute(headRequest);
//            System.out.println(documentId + "response.getStatusLine().getStatusCode() = " + response.getStatusLine().getStatusCode());
//            Header[] headers = response.getAllHeaders();
//            for (Header header : headers) {
//                System.out.println("header = " + header.getName() + "\t" + header.getValue());
//            }
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch (IOException ex) {
            System.out.println(documentId + "" + ex.getMessage());
            return false;
        } finally {
            headRequest.releaseConnection();
        }
    }
}
