/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.parameters.PurgeDocumentParameters;
import uk.ac.core.database.service.document.ArticleMetadataDAO;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.workers.item.purgedocument.PurgeDocumentWorker;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author mc26486
 */
@RestController
public class StarterController {
    @Autowired
    private QueueWorker service;
    @Autowired
    private ArticleMetadataDAO documentDAO;

    @RequestMapping("/purge_document/{documentId}")
    public void purgeDocumentController(@PathVariable(value = "documentId") final Integer documentId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        taskDescription.setTaskParameters(new Gson().toJson(new PurgeDocumentParameters(documentId, documentDAO.getArticleMetadata(documentId).getRepositoryId())));
        ((PurgeDocumentWorker)service).process(taskDescription);
    }
}
