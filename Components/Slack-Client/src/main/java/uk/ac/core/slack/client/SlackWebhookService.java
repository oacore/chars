package uk.ac.core.slack.client;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import uk.ac.core.slack.client.model.SlackMessage;

/**
 *
 * @author lucas
 */
public class SlackWebhookService {

    private static final String REPORT_WEB_HOOK_ENDPOINT = "https://hooks.slack.com/services/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";
    private static final String OPERATIONS_REPORT_WEB_HOOK_ENDPOINT = "https://hooks.slack.com/services/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";
    private static final String OPERATIONS_HARVESTING_EVENTS_ENDPOINT = "https://hooks.slack.com/services/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";
    private static final String DATA_PROVIDER_REPORT_ENDPOINT = "https://hooks.slack.com/services/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";
    private static final String DATA_PROVIDER_REPORT_MOCK_ENDPOINT = "https://hooks.slack.com/services/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";

    private static final Logger LOG = Logger.getLogger(SlackWebhookService.class.getName());

    private static final Map<String, String> CHANNEL_WEBHOOK_ENDPOINTS = new HashMap<String, String>() {
        {
            put("report", REPORT_WEB_HOOK_ENDPOINT);
            put("operations-report", OPERATIONS_REPORT_WEB_HOOK_ENDPOINT);
            put("harvesting-events", OPERATIONS_HARVESTING_EVENTS_ENDPOINT);
            put("data-provider-report", DATA_PROVIDER_REPORT_ENDPOINT);
            put("data-provider-report-mock", DATA_PROVIDER_REPORT_MOCK_ENDPOINT);

            put("random-channel", "??");
        }
    };

    public static void sendMessage(SlackMessage message) {
        sendMessage(message, "report");
    }

    public static void sendMessage(SlackMessage message, String channel) {

        LOG.info("Sending Slack message to " + channel);
        LOG.info("Web Hook : " + CHANNEL_WEBHOOK_ENDPOINTS.get(channel));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(CHANNEL_WEBHOOK_ENDPOINTS.get(channel));

        try {
            Gson gson = new Gson();
            String json = gson.toJson(message);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            client.execute(httpPost);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message, String channel) {

        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setText(message);

        sendMessage(slackMessage, channel);
    }
}
