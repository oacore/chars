package uk.ac.core.bigworksindexing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.item.TaskItemBuilder;
import uk.ac.core.common.model.task.parameters.ItemIndexParameters;
import uk.ac.core.queue.QueueInfoService;
import uk.ac.core.queue.QueueItemService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorksIndexingScheduler {
    private Logger logger = LoggerFactory.getLogger(WorksIndexingScheduler.class);

    private final String queueName = TaskType.WORKS_INDEX_ITEM.getName()  + "-queue";

    private final JdbcTemplate jdbcTemplate;
    private final QueueInfoService queueInfoService;
    private final QueueItemService queueItemService;
    private final TaskItemBuilder taskItemBuilder;

    private static int page = 0;

    @Autowired
    public WorksIndexingScheduler(JdbcTemplate jdbcTemplate,
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
        String path = "/data/user-data/mat522/works_dump.csv";

        logger.info("Started checking works index queue: {}", queueName);
        Integer countMessages = queueInfoService.getCountMessages(queueName);

        logger.info("There are {} messages in the {} queue.", countMessages, queueName);
        if(countMessages < MESSAGES_LIMIT) {
            getDocumentsFromCsv(path).forEach(id -> {
                try {
                    logger.info("Sending request for document " + id);
                    ItemIndexParameters indexParameters = new ItemIndexParameters(id);
                    indexParameters.setIndexName("works_2022_04_14");

                    TaskDescription taskDescription = this.taskItemBuilder.buildItemTask(TaskType.WORKS_INDEX_ITEM, indexParameters);

                    this.queueItemService.publish(taskDescription);
                    logger.info("Request for document {} successfully sent", id);
                } catch (Exception e){
                    logger.error("Cannot send works index request for document: {}", id, e);
                }
            });
            page++;
        }
    }

    private List<Integer> getDocumentsWithoutIndexingInPage(int page) {
        final Integer DOCUMENT_SIZE = 4000000;
        final String QUERY = "SELECT d.id_document from document d " +
                "LEFT JOIN work_to_document wd ON wd.document_id = d.id_document " +
                "WHERE d.deleted = 0 AND d.indexed = 1 AND wd.document_id IS NULL " +
                "ORDER BY d.id_document limit "+ DOCUMENT_SIZE;

        return jdbcTemplate.query(QUERY, (rs, rowNum) -> rs.getInt("id_document"));
    }

    private List<Integer> getDocumentsFromCsv(String path) {
        logger.info("Start getting documents from the file");
        List<Integer> documents = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;
            while((line = br.readLine()) != null){
                logger.info("Add document {} to the list", line);
                documents.add(Integer.parseInt(line));
            }
        } catch (IOException e){
            logger.error("File was not found");
        }
        return documents;
    }
}
