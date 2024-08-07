package uk.ac.core.ExtendedMetadataProcessWorker.controller;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.worker.QueueWorker;

/**
 *
 * @author mc26486
 */
@RestController
public class StarterController {

    @Autowired
    QueueWorker queueWorker;

    @RequestMapping("/extended_metadata_process/{repositoryId}")
    public String document_download_starter(@PathVariable(value = "repositoryId") final Integer repositoryId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        RepositoryTaskParameters repositoryTaskParameters = new RepositoryTaskParameters(repositoryId);
        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.EXTENDED_METADATA_PROCESS);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }
}
