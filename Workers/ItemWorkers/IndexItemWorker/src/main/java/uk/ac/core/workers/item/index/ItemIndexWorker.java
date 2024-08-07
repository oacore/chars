package uk.ac.core.workers.item.index;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.ItemIndexParameters;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.elasticsearch.exceptions.IndexException;
import uk.ac.core.elasticsearch.services.legacy.IndexLegacyService;
import uk.ac.core.singleitemworker.SingleItemWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author lucasanastasiou
 */
public class ItemIndexWorker extends SingleItemWorker {

    // should not be simulated, don't use field injection.
    @Autowired
    IndexLegacyService elasticSearchIndexLegacyService;

    @Autowired
    RepositoryDocumentDAO repoDocumentDAO;

    private final List<Integer> lastItems = new ArrayList<>(10);
    private final String ARTICLE_INDEX_NAME = "articles_indexable";

    private static final boolean INDEXED_SUCCESS = true;
    private static final boolean INDEXED_FAIL = false;
    private final ExecutorService executor;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ItemIndexWorker.class);

    public ItemIndexWorker() {
        this.executor = new ThreadPoolExecutor(3,
                5, 2, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());
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
        ItemIndexParameters singleItemTaskParameters = new Gson().fromJson(params, ItemIndexParameters.class);
        final Integer articleId = singleItemTaskParameters.getArticle_id();
        boolean success = true;
        if (!this.lastItems.contains(articleId)) {
            executor.execute(() -> {
                try {
                    logger.info("Starting indexing of article : " + articleId);
                    long t0 = System.currentTimeMillis();

                    elasticSearchIndexLegacyService.indexArticle(ARTICLE_INDEX_NAME, articleId);
                    long t00 = System.currentTimeMillis();
                    repoDocumentDAO.setDocumentIndexStatus(articleId, INDEXED_SUCCESS);
                    long t01 = System.currentTimeMillis();
                    logger.info("Set document index status for: " + articleId + "\tTook : " + (t01 - t00) + " ms");

                    long t1 = System.currentTimeMillis();
                    logger.info("Finished indexing of article: " + articleId + "\tTook : " + (t1 - t0) + " ms");
                } catch (Exception ex) {
                    repoDocumentDAO.setDocumentIndexStatus(articleId, INDEXED_FAIL);
                    logger.error(ex.getMessage(), ex);
                }
            });

            if (lastItems.size() > 9) {
                // Remove the 10th item (don't forget its zero indexed)
                this.lastItems.remove(9);
            }
            this.lastItems.add(articleId);

        } else {
            logger.warn("Document ID " + articleId + " was index within the last 10 items, Skipping");
        }
        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(success);
        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;
    }
}
