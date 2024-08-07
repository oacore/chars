package uk.ac.core.workers.item.languagedetection.controller;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.worker.QueueWorker;

/**
 *
 * @author mc26486
 */
@RestController
public class StarterController {

    @Autowired
    QueueWorker queueWorker;

    @Autowired
    RepositoryDocumentDAO repositoryDocumentDAO;

    /**
     * Run language detection for a specific article (skips the queue)
     * @param documentId
     * @return
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping("/item_detect_language/{documentId}")
    public String itemDetectLanguage(@PathVariable(value = "documentId") Integer documentId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        SingleItemTaskParameters singleItemTaskParameters = new SingleItemTaskParameters(documentId);
        taskDescription.setTaskParameters(new Gson().toJson(singleItemTaskParameters));
        taskDescription.setType(TaskType.ITEM_LANG_DETECTION);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    /**
     * Run language detection for all documents of a repository (skips the queue)
     * @param repositoryId
     * @return
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping("/item_detect_language/repository/{id}")
    public String repositoryItemsDetectLanguage(@PathVariable(value = "id") Integer repositoryId) throws UnsupportedEncodingException {

        List<RepositoryDocument> documents = repositoryDocumentDAO.getRepositoryDocumentsByRepositoryId(repositoryId);

        for (RepositoryDocument doc : documents) {
            TaskDescription taskDescription = new TaskDescription();
            SingleItemTaskParameters singleItemTaskParameters = new SingleItemTaskParameters(doc.getIdDocument());
            taskDescription.setTaskParameters(new Gson().toJson(singleItemTaskParameters));
            taskDescription.setType(TaskType.ITEM_LANG_DETECTION);
            queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        }
        TaskDescription taskDescription = new TaskDescription();
        return new Gson().toJson(taskDescription);

    }

}
