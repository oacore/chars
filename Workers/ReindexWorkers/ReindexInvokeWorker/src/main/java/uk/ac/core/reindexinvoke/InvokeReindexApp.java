package uk.ac.core.reindexinvoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.item.TaskItemBuilder;
import uk.ac.core.database.service.document.DocumentDAO;
import uk.ac.core.queue.QueueItemService;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author lucasanastasiou
 */
@SpringBootApplication
public class InvokeReindexApp implements CommandLineRunner {

    @Autowired
    DocumentDAO documentDAO;

    @Autowired
    QueueItemService queueItemService;
    @Autowired
    TaskItemBuilder taskItemBuilder;

    @Autowired
    DatabaseQuery databaseQuery;

    @Value("${elasticsearch.indexName.articles}")
    private String indexName;

    @Value("${elasticsearch.reindex.from:1}")
    private int reindexFrom;

    @Value("${elasticsearch.reindex.step:10000000}")
    private int reindexStep;

    @Value("${elasticsearch.reindex.to:1000000000}")// default value (if not set) 1 billion - should be enough :)
    private int reindexTo;

    Logger logger = LoggerFactory.getLogger("InvokeReindexApp");

    public static void main(String[] args) throws Exception {

        // We want to know if --elasticsearch.indexName.articles was explicitly 
        // set via a commandline argument so we can catch any errors or runtime 
        // mistakes (such as if a user runs this application, the default is the
        // /articles/ index so this would be bad as we would overwrite the index!).
        // However, it is easier to parse the value using the @Value syntax. 
        // This is why we check args directly and not indexName
        if (!Arrays.toString(args).contains("--elasticsearch.indexName.articles")) {
            throw new Exception("\r\n\r\nCommand line option --elasticsearch.indexName.articles not found.\r\n"
                    + "Please run this using java -jar app.java --elasticsearch.indexName.articles=your_new_article_index_name\r\n"
                    + "To overwrite the articles index, run java -jar app.java --elasticsearch.indexName.articles=articles\r\n");
        }
        SpringApplication.run(InvokeReindexApp.class, args);
    }

    @Override
    public void run(String... strings) {

        List<Integer> itemsToProcess = new ArrayList();
        Integer currentFrom = reindexFrom;
        Integer step = reindexStep;
        Integer currentTo = currentFrom + step;
        if (currentTo > reindexTo) {
            currentTo = reindexTo;
        }
        while (currentTo <= reindexTo) {
            logger.info("Indexing from " + currentFrom + " to " + currentTo);

            itemsToProcess = this.databaseQuery.getIndexableIds(currentFrom, currentTo);

            for (int item : itemsToProcess) {
                TaskDescription taskDescription = this.taskItemBuilder.buildReindexItem(indexName, item);
                this.queueItemService.publish(taskDescription);
            }
            currentFrom += step;
            currentTo = currentFrom + step;
            if (currentTo > reindexTo) {
                currentTo = reindexTo;
            }
        }

    }
}
