package uk.ac.core.supervisor.controller.tasks;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.DocumentDownloadParameters;
import uk.ac.core.supervisor.controller.TaskController;
import uk.ac.core.supervisor.exceptions.RepositoryDoesNotExistException;
import uk.ac.core.supervisor.exceptions.RepositoryIsDisabledException;

/**
 *
 * @author samuel
 */
@RestController
@RequestMapping("/task/repository/download-document")
public class DownloadDocumentController extends TaskController {

    @RequestMapping("/{repositoryId}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        DocumentDownloadParameters pdfDownloadParameters = new DocumentDownloadParameters(repositoryId);

        TaskDescription taskDescription = this.getTaskDescriptionFactory().create(TaskType.DOCUMENT_DOWNLOAD, pdfDownloadParameters);

        this.getQueueService().publish(taskDescription);

        return taskDescription;
    }

    @RequestMapping("/{repositoryId}/priority/{priority}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId, @PathVariable(value = "priority") int priority) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        DocumentDownloadParameters pdfDownloadParameters = new DocumentDownloadParameters(repositoryId);

        TaskDescription taskDescription = this.getTaskDescriptionFactory().create(TaskType.DOCUMENT_DOWNLOAD, pdfDownloadParameters, priority);

        this.getQueueService().publish(taskDescription);

        return taskDescription;
    }
}
