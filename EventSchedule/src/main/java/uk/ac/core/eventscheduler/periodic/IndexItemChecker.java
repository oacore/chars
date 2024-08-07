package uk.ac.core.eventscheduler.periodic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.item.TaskItemBuilder;
import uk.ac.core.common.model.task.parameters.ItemIndexParameters;
import uk.ac.core.queue.QueueInfoService;
import uk.ac.core.queue.QueueItemService;

import java.util.List;

/**
 * @deprecated Not really sure why the scheduling is off. Probably there was a reason :)
 */
@Deprecated
@Component
public class IndexItemChecker {

    private Logger logger = LoggerFactory.getLogger(IndexItemChecker.class);

    private final String queueName = TaskType.INDEX_ITEM.getName()  + "-queue";

    private final JdbcTemplate jdbcTemplate;
    private final QueueInfoService queueInfoService;
    private final QueueItemService queueItemService;
    private final TaskItemBuilder taskItemBuilder;

    @Autowired
    public IndexItemChecker(JdbcTemplate jdbcTemplate,
                                  QueueInfoService queueInfoService,
                                  QueueItemService queueItemService,
                                  TaskItemBuilder taskItemBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.queueInfoService = queueInfoService;
        this.queueItemService = queueItemService;
        this.taskItemBuilder = taskItemBuilder;
    }


    //@Scheduled(cron = "* */5 * * * *")
    public void invoke() {
        final int MESSAGES_LIMIT = 100000;

        logger.info("Started checking index item queue: {}", queueName);
        Integer countMessages = queueInfoService.getCountMessages(queueName);

        logger.info("There are {} messages in the {} queue.", countMessages, queueName);
        if(countMessages < MESSAGES_LIMIT) {
            getDocumentsWithoutArticleIndex().forEach(id -> {
                try {
                    logger.info("Sending request for document " + id);
                    ItemIndexParameters indexParameters = new ItemIndexParameters(id);
                    indexParameters.setIndexName("article");

                    TaskDescription taskDescription = this.taskItemBuilder.buildItemTask(TaskType.INDEX_ITEM, indexParameters);

                    this.queueItemService.publish(taskDescription);
                    logger.info("Request for document {} successfully sent", id);
                } catch (Exception e){
                    logger.error("Cannot send index item request for document: {}", id, e);
                }
            });
        }
    }

    private List<Integer> getDocumentsWithoutArticleIndex(){
        final Integer DOCUMENT_SIZE = 1000000;
        final String QUERY = "SELECT d.id_document from document d \n" +
                "inner join document_metadata dm on dm.id_document = d.id_document \n" +
                "where d.indexed = 0 and d.index_last_attempt_successful is null limit " + DOCUMENT_SIZE + ";";

        return jdbcTemplate.query(QUERY, (rs, rowNum) -> rs.getInt("id_document"));

    }
}
