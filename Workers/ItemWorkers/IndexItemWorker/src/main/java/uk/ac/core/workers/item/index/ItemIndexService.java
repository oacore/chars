package uk.ac.core.workers.item.index;

import com.google.gson.Gson;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.worker.QueueWorker;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class ItemIndexService {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ItemIndexService.class);

    private final QueueWorker queueWorker;
    private final RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    public ItemIndexService(QueueWorker queueWorker, RepositoryDocumentDAO repositoryDocumentDAO) {
        this.queueWorker = queueWorker;
        this.repositoryDocumentDAO = repositoryDocumentDAO;
    }

    public String indexOneDocument(Integer documentId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        SingleItemTaskParameters repositoryTaskParameters = new SingleItemTaskParameters(documentId);
        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.INDEX_ITEM);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    public String indexRepository(Integer repositoryId) throws UnsupportedEncodingException {
        logger.info("Start index repository {}", repositoryId);
        final int BATCH_SIZE = 1000;
        int offset = 0;
        List<RepositoryDocument> documents;

        do {
            logger.info("Start query offset {} limit {}", offset, BATCH_SIZE);
            documents = repositoryDocumentDAO.getRepositoryDocumentsByRepositoryId(repositoryId, offset, BATCH_SIZE);
            offset += documents.size();

            logger.info("Query fetched {} documents", documents.size());

            for (RepositoryDocument doc : documents) {
                indexOneDocument(doc.getIdDocument());
            }
            logger.info("Batch indexed");
        } while (documents.size() == BATCH_SIZE);

        TaskDescription taskDescription = new TaskDescription();
        logger.info("Repository {} indexed", repositoryId);
        return new Gson().toJson(taskDescription);
    }

}
