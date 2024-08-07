package uk.ac.core.eventscheduler.monitoring;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author lucasanastasiou
 */
@Component
public class WorkersInfoService {

    private static final Pattern REPOSITORY_ID_REGEX_PATTERN = Pattern.compile(".*\\\\\\\"repositoryId\\\\\\\":([0-9]+).*");

    public List<Integer> getRepositoriesInWorkers(List<String> workersEndpoints) {

        List<Integer> repositoryIds = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        for (String workerEndpoint : workersEndpoints) {
            String workerEndpointClean = extractWorkerCleanUrl(workerEndpoint);
            String extractedHost = extractHost(workerEndpointClean);
            int port = extractPort(workerEndpointClean);
            HttpEntity entity1 = null;
            try {
                HttpGet httpGet = new HttpGet(new URI("http", null, extractedHost, port, "/status", null, null));

                HttpResponse response1 = httpClient.execute(httpGet);

//                System.out.println(response1.getStatusLine());
                entity1 = response1.getEntity();
                String response = IOUtils.toString(entity1.getContent(), "UTF-8");
//                System.out.println("response = " + response);

                Integer repositoryId = extractRepositoryIdFromWorkerStatusResponse(response);
                if (repositoryId != null) {
                    repositoryIds.add(repositoryId);
                }

            } catch (IOException ioe) {
                System.out.println("ioe" + ioe.getMessage());
                return null;
            } catch (URISyntaxException ex) {
                Logger.getLogger(WorkersInfoService.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                EntityUtils.consumeQuietly(entity1);
            }
        }
        return repositoryIds;
    }

    private String extractWorkerCleanUrl(String workerEndpoint) {
        String hashStripped = workerEndpoint.split("#")[0];
        if (hashStripped.contains("@")) {
            String atStripped = hashStripped.split("@")[1];
            return atStripped;
        } else {
            return hashStripped;
        }
    }

    private Integer extractRepositoryIdFromWorkerStatusResponse(String response) {
        Matcher matcher = REPOSITORY_ID_REGEX_PATTERN.matcher(response);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)); // returns null if not integer
        } else {
            return null;
        }
    }

    private String extractHost(String workerEndpointClean) {
        String host = workerEndpointClean.split(":")[0];
        return host;
    }

    private int extractPort(String workerEndpointClean) {
        String port = workerEndpointClean.split(":")[1];
        Integer iPort = Integer.parseInt(port);
        return iPort;
    }

}
