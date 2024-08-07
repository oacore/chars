package uk.ac.core.workers.item.purgedocument;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.PurgeDocumentParameters;
import uk.ac.core.database.service.document.RepositoryMetadataDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.singleitemworker.SingleItemWorker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author lucasanastasiou
 */
public class PurgeDocumentWorker extends SingleItemWorker {

    @Autowired
    private RepositoryMetadataDAO repositoryMetadataDAO;
    @Autowired
    private FilesystemDAO filesystemDAO;

    private final Logger logger = LoggerFactory.getLogger(PurgeDocumentWorker.class);

    public PurgeDocumentWorker() {
    }

    @Override
    public void taskReceived(Object task, @Header(AmqpHeaders.CHANNEL) Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
        workerStatus.setChannel(channel);
        workerStatus.setDeliveryTag(deliveryTag);
        super.taskReceived(task, channel, deliveryTag);
    }

    @Override
    public TaskItemStatus process(TaskDescription taskDescription) {
        String params = taskDescription.getTaskParameters();
        PurgeDocumentParameters singleItemTaskParameters = new Gson().fromJson(params, PurgeDocumentParameters.class);
        final Integer articleId = singleItemTaskParameters.getArticle_id();
        final Integer repoId = singleItemTaskParameters.getRepositoryId();

        logger.info("Purging {}", articleId);

        try {
            deleteFromIndex("articles_indexable", articleId);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }

        repositoryMetadataDAO.deleteDocument(articleId);
        try {
            filesystemDAO.deleteDocument(articleId, repoId);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }

        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(true);
        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;
    }

    private boolean deleteFromIndex(String index, int articleId) throws IOException {
        String url = "https://index.core.ac.uk/" + index + "/article/" + articleId;
        logger.debug("Executing {}", url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("DELETE");
        return connection.getResponseCode() == 200;
    }
}
