package uk.ac.core.supervisor.controller.tasks;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.MetadataExtractParameters;
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
@RequestMapping("/task/repository/extract-metadata")
public class MetadataExtractController extends TaskController {

    // this is the pattern to be used in URLs Date parameters
    final static private String PATH_VARIABLE_DATE_PATTERN = "yyyy-MM-dd";

    @RequestMapping("/{repositoryId}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        return this.execute(repositoryId, null);
    }

    @RequestMapping("/{repositoryId}/fromDate/{fromDate}")
    public TaskDescription execute(@PathVariable(value = "repositoryId") int repositoryId,
            @PathVariable(value = "fromDate") @DateTimeFormat(pattern = PATH_VARIABLE_DATE_PATTERN) Date fromDate) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        MetadataExtractParameters metadataExtractController = new MetadataExtractParameters(repositoryId, fromDate, null);

        TaskDescription taskDescription = this.getTaskDescriptionFactory().create(TaskType.EXTRACT_METADATA, metadataExtractController);
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

        MetadataExtractParameters metadataExtractParameters = new MetadataExtractParameters(repositoryId, fromDate, toDate);

        TaskDescription taskDescription = this.getTaskDescriptionFactory().create(TaskType.EXTRACT_METADATA, metadataExtractParameters);
        taskDescription.setStartTime(fromDate.getTime());
        taskDescription.setEndTime(toDate.getTime());

        this.getQueueService().publish(taskDescription);

        return taskDescription;
    }

}
