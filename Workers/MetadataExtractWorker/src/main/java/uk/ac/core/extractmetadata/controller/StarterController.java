package uk.ac.core.extractmetadata.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.extractmetadata.periodic.crossref.CrossrefReharvestScheduler;
import uk.ac.core.worker.QueueWorker;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mc26486
 */
@RestController
public class StarterController {

    @Autowired
    QueueWorker queueWorker;
    @Autowired
    private CrossrefReharvestScheduler scheduler;

    @RequestMapping("/metadata_extract/{repositoryId}")
    public String extractController(@PathVariable(value = "repositoryId") final Integer repositoryId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        RepositoryTaskParameters repositoryTaskParameters = new RepositoryTaskParameters(repositoryId);
        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.EXTRACT_METADATA);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    @RequestMapping("/metadata_extract/{repositoryId}/{fromDate}/{toDate}")
    public String metadata_extract_starter_with_date(
            @PathVariable(value = "repositoryId") final Integer repositoryId,
            @PathVariable(value = "fromDate") final String fromDate,
            @PathVariable(value = "toDate") final String toDate) throws UnsupportedEncodingException, ParseException {


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDateDate = format.parse(fromDate);
        Date toDateDate = format.parse(toDate);

        TaskDescription taskDescription = new TaskDescription();
        RepositoryTaskParameters repositoryTaskParameters = new RepositoryTaskParameters(repositoryId, fromDateDate, toDateDate);

        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.EXTRACT_METADATA);
        taskDescription.setStartTime(fromDateDate.getTime());
        taskDescription.setEndTime(toDateDate.getTime());
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    @GetMapping("/metadata_extract/crossref-reharvesting")
    public String triggerScheduler() {
        this.scheduler.start();
        return "Crossref re-harvesting scheduled";
    }

    /**
     * FOR TESTING PURPOSES ONLY
     * @return path to the file with corrected metadata
     */
    @GetMapping("/metadata_extract/crossref-reharvesting/test-escaping")
    public String testEscaping() {
        File file = this.scheduler.getService().getRawMetadataFile();
        return file.getPath();
    }
}
