package uk.ac.core.indexbigrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.item.TaskItemBuilder;
import uk.ac.core.common.model.task.parameters.ItemIndexParameters;
import uk.ac.core.queue.QueueInfoService;
import uk.ac.core.queue.QueueItemService;
import uk.ac.core.supervisor.client.SupervisorClient;

import java.util.List;

@Service
public class MuccIndexerScheduler {

    private final TaskType taskType = TaskType.REPORTING;

    private final String queueName = TaskType.INDEX_ITEM.getName() + "-queue";
    @Value("${indexing.repository:4786}")
    private int coreInternalId;
    private final Integer DOCUMENT_SIZE = 150000;

    private final QueueInfoService queueInfoService;
    private final JdbcTemplate jdbcTemplate;
    private final SupervisorClient supervisorClient;
    private TaskItemBuilder taskItemBuilder;
    private QueueItemService queueItemService;


    private Logger logger = LoggerFactory.getLogger("MuccIndexerScheduler");
    @Autowired
    public MuccIndexerScheduler(QueueInfoService queueInfoService,
                                SupervisorClient supervisorClient,
                                JdbcTemplate jdbcTemplate,
                                TaskItemBuilder taskItemBuilder,
                                QueueItemService queueItemService) {
        this.queueInfoService = queueInfoService;
        this.supervisorClient = supervisorClient;
        this.jdbcTemplate = jdbcTemplate;
        this.taskItemBuilder = taskItemBuilder;
        this.queueItemService = queueItemService;
    }

    @Scheduled(cron = "* */10 * * * *")
    public void invoke() {
        logger.info("Indexing was started for repository: {} ", coreInternalId);
        Integer countMessages = queueInfoService.getCountMessages(queueName);

        logger.info("There are {} messages in the index-item queue.", countMessages);
        if(countMessages < 2000000) {
            List<Integer> documents = getDocumentsWithoutIndexing();
            for (int i = 0; i < documents.size(); i++){
                try {
                    logger.info("Sending request for article " + documents.get(i));
                    long startTime = System.currentTimeMillis();
                    ItemIndexParameters indexParameters = new ItemIndexParameters(documents.get(i));

                    TaskDescription taskDescription = this.taskItemBuilder.buildItemTask(TaskType.INDEX_ITEM, indexParameters);

                    this.queueItemService.publish(taskDescription);
                    logger.info("Sending index item request takes {}",System.currentTimeMillis() - startTime );
                }catch (Exception e){
                    logger.error("Cannot send index request for article:" + documents.get(i),e);
                }
            }
        }
    }

    private List<Integer> getDocumentsWithoutIndexing() {
        final String QUERY = "SELECT d.id_document from document_metadata db \n" +
                "inner JOIN document d on d.id_document = db.id_document\n" +
                "where d.id_repository = " + coreInternalId +
                " and d.indexed = 0 and d.index_last_attempt is NULL limit " + DOCUMENT_SIZE;

        return jdbcTemplate.query(QUERY, (rs, rowNum) -> rs.getInt("id_document"));
    }
}
