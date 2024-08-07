/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.workers.item.doiresolutionworker.controller;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.queue.QueueService;
import uk.ac.core.worker.QueueWorker;

/**
 *
 * @author Samuel Pearce <samuel.pearce@open.ac.uk>
 */
@RestController
public class StarterController {

    @Autowired
    QueueWorker queueWorker;

    @Autowired
    QueueService queueService;

    @RequestMapping("/item_doi_resolution/{documentId}")
    public String doiResolution(@PathVariable(value = "documentId") Integer documentId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        SingleItemTaskParameters singleItemTaskParameters = new SingleItemTaskParameters(documentId);
        taskDescription.setTaskParameters(new Gson().toJson(singleItemTaskParameters));
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    @RequestMapping("/item_doi_resolution/all/{start}/{totaldocuments}")
    public String addAllToQueue(
            @PathVariable(value = "start") Integer start,
            @PathVariable(value = "totaldocuments") Integer totalDocuments) 
            throws UnsupportedEncodingException {

        for (int i = start; i <= totalDocuments; i++) {

            TaskType fromType = TaskType.ITEM_DOI_RESOLUTION;

            TaskDescription taskDescription = new TaskDescription();
            taskDescription.setUniqueId(UUID.randomUUID().toString());
            taskDescription.setType(fromType);
            taskDescription.setTaskParameters(new Gson().toJson(new SingleItemTaskParameters(i)));
            taskDescription.setRoutingKey(fromType.getName());
            List<TaskType> taskList = new ArrayList<>();
            taskList.add(TaskType.ITEM_DOI_RESOLUTION);
            taskDescription.setTaskList(taskList);

            this.queueService.publish(taskDescription);
        }
        return "Success";
        
    }

}