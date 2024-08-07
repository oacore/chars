package uk.ac.core.filesystemdocumentworker;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.worker.QueueWorker;

import java.io.UnsupportedEncodingException;

@Service
public class DocumentFilesystemService {

    private final QueueWorker queueWorker;

    @Autowired
    public DocumentFilesystemService(QueueWorker queueWorker) {
        this.queueWorker = queueWorker;
    }

    public String processOneDocument(Integer documentId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        SingleItemTaskParameters repositoryTaskParameters = new SingleItemTaskParameters(documentId);
        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.DOCUMENT_FILESYSTEM_ITEM);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }
}
