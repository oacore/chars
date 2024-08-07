package uk.ac.core.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.worker.QueueWorker;

import java.io.UnsupportedEncodingException;
import java.util.List;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;

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

    @RequestMapping("/item/{documentId}")
    public String indexItemController(@PathVariable(value = "documentId") final Integer documentId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        SingleItemTaskParameters repositoryTaskParameters = new SingleItemTaskParameters(documentId);
        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.THUMBNAIL_GENERATION_ITEM);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    @RequestMapping("/repository/{id}")
    public String indexRepositoryController(@PathVariable(value = "id") final Integer repositoryId) throws UnsupportedEncodingException {

        List<RepositoryDocument> documents = repositoryDocumentDAO.getRepositoryDocumentsByRepositoryId(repositoryId);

        for (RepositoryDocument doc : documents) {
            this.indexItemController(doc.getIdDocument());
        }
        TaskDescription taskDescription = new TaskDescription();        
        return new Gson().toJson(taskDescription);
    }

}
