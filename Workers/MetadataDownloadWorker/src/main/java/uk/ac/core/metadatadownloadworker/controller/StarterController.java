/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.metadatadownloadworker.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.metadatadownloadworker.worker.MetadataDownloadWorker;
import uk.ac.core.worker.QueueWorker;
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

    private final Logger logger = LoggerFactory.getLogger(MetadataDownloadWorker.class);


    @RequestMapping("/metadata_download/{repositoryId}")
    public String document_download_starter(@PathVariable(value = "repositoryId") final Integer repositoryId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        RepositoryTaskParameters repositoryTaskParameters = new RepositoryTaskParameters(repositoryId);
        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.METADATA_DOWNLOAD);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    @RequestMapping("/metadata_download/{repositoryId}/{fromDate}/{toDate}")
    public String document_download_starter_with_date(
            @PathVariable(value = "repositoryId") final Integer repositoryId,
            @PathVariable(value = "fromDate") final String fromDate,
            @PathVariable(value = "toDate") final String toDate) throws UnsupportedEncodingException, ParseException {


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDateDate = format.parse(fromDate);
        Date toDateDate = format.parse(toDate);

        TaskDescription taskDescription = new TaskDescription();
        RepositoryTaskParameters repositoryTaskParameters = new RepositoryTaskParameters(repositoryId, fromDateDate, toDateDate);

        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.METADATA_DOWNLOAD);
        taskDescription.setStartTime(fromDateDate.getTime());
        taskDescription.setEndTime(toDateDate.getTime());
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }



}
