package uk.ac.core.supervisor.controller.tasks;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.tasks.metadataDownload.MetadataDownloadParameters;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.supervisor.controller.TaskController;
import uk.ac.core.supervisor.exceptions.RepositoryDoesNotExistException;
import uk.ac.core.supervisor.exceptions.RepositoryIsDisabledException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author samuel
 */
@RestController
@RequestMapping("/task/repository/metadata_download")
public class MetadataDownloadController extends TaskController {

    @RequestMapping("/{repositoryId}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        MetadataDownloadParameters metadataDownloadParameters = new MetadataDownloadParameters(repositoryId);

        TaskDescription taskDescription = this.getTaskDescriptionFactory().create(TaskType.METADATA_DOWNLOAD, metadataDownloadParameters);

        this.getQueueService().publish(taskDescription);

        return taskDescription;
    }

    @RequestMapping("/{repositoryId}/fromDate/{fromDate}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId,
            @PathVariable(value = "fromDate") final String fromDateStr) throws RepositoryDoesNotExistException, RepositoryIsDisabledException, ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = format.parse(fromDateStr);

        MetadataDownloadParameters metadataDownloadParameters = new MetadataDownloadParameters(repositoryId, fromDate, null);

        TaskDescription taskDescription = this.getTaskDescriptionFactory().create(TaskType.METADATA_DOWNLOAD, metadataDownloadParameters);
        taskDescription.setStartTime(fromDate.getTime());

        this.getQueueService().publish(taskDescription);

        return taskDescription;
    }

    @RequestMapping("/{repositoryId}/fromDate/{fromDate}/toDate/{toDate}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId,
            @PathVariable(value = "fromDate") final String fromDateStr,
            @PathVariable(value = "toDate") final String toDateStr) throws RepositoryDoesNotExistException, RepositoryIsDisabledException, ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = format.parse(fromDateStr);
        Date toDate = format.parse(toDateStr);

        MetadataDownloadParameters metadataDownloadParameters = new MetadataDownloadParameters(repositoryId, fromDate, toDate);

        TaskDescription taskDescription = this.getTaskDescriptionFactory().create(TaskType.METADATA_DOWNLOAD, metadataDownloadParameters);
        taskDescription.setStartTime(fromDate.getTime());
        taskDescription.setEndTime(toDate.getTime());

        this.getQueueService().publish(taskDescription);

        return taskDescription;
    }
}