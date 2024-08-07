/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.crossref;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.core.crossref.json.CrossRefDocument;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author mc26486
 */
@Service
public class CrossrefService {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(CrossrefService.class);

    public Timestamp downloadPublicationDate(String doi) throws IOException {
        String crossrefUrl = "https://api.crossref.org/works/" + doi;
        String content = getContent(crossrefUrl);
        try {
            JsonParser parser = new JsonParser();
            JsonElement parsed = parser.parse(content);
            JsonObject jsonObject = parsed.getAsJsonObject();
            JsonElement message = jsonObject.get("message");
            String s = message.toString();
            CrossRefDocument crossRefDocument = CrossRefDocument.fromString(s);
            return CrossRefDocument.datePartsToTimestamp(crossRefDocument.getIssued().getDateParts().get(0));
        } catch (JsonSyntaxException jsonSyntaxException) {
            logger.error("Broken Crossref response" + content, jsonSyntaxException);
        }
        return null;
    }

    private String getContent(String url) {
        //logger.info("Performing GET request to {}", url);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        return response;
    }

}
