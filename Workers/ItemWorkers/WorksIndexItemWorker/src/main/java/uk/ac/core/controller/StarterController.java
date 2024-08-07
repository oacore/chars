/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.dto.ExcludeDto;
import uk.ac.core.worker.QueueWorker;

import java.io.UnsupportedEncodingException;
import java.util.List;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.worker.WorksIndexItemWorker;

/**
 *
 * @author MTarasyuk
 */
@RestController
public class StarterController {

    @Autowired
    QueueWorker queueWorker;

    @Autowired
    RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    WorksIndexItemWorker worksIndexItemWorker;


    @GetMapping("/work_index_item")
    public String indexItemController(@RequestParam("documentId") final Integer documentId,
                                      @RequestParam(value = "forceReindex", defaultValue = "true") final boolean forceReindex ) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        SingleItemTaskParameters repositoryTaskParameters = new SingleItemTaskParameters(documentId);
        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.INDEX_ITEM);
        taskDescription.setForceWorksReindex(forceReindex);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    @GetMapping("/work_index_item/repository")
    public String indexRepositoryController(@RequestParam("repositoryId") final Integer repositoryId,
                                            @RequestParam(value = "forceReindex", defaultValue = "false") final boolean forceReindex) throws UnsupportedEncodingException {

        List<RepositoryDocument> documents = repositoryDocumentDAO.getRepositoryDocumentsByRepositoryId(repositoryId);

        for (RepositoryDocument doc : documents) {
            this.indexItemController(doc.getIdDocument(), forceReindex);
        }
        TaskDescription taskDescription = new TaskDescription();        
        return new Gson().toJson(taskDescription);
    }

    @PutMapping("/reindexDocumentWithDeletingFromCurrentWorkId")
    public void reindexDocumentWithDeletingFromCurrentWorkId(@RequestBody ExcludeDto excludeDto) {
        worksIndexItemWorker.reindexDocumentWithDeletingFromCurrentWorkId(excludeDto.getExcludedDocumentId(),
                excludeDto.getDocumentIds(), excludeDto.getIndexName());
    }

}
