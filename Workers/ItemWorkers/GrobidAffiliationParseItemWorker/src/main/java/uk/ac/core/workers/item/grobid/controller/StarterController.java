/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.workers.item.grobid.controller;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.filesystem.services.FilesystemDAO;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
@RestController
public class StarterController {
    
    @Autowired
    FilesystemDAO filesystemDAO;
    
    @Autowired
    QueueWorker queueWorker;
    
    Logger logger = LoggerFactory.getLogger("StarterController");

    @RequestMapping("/grobid/affiliation/article/{articleId}")
    public String startTasksArticleId(@PathVariable(value = "articleId")Integer articleId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();

        taskDescription.setType(TaskType.fromString("grobid-affiliation-parser-item"));
        SingleItemTaskParameters taskParameters = new SingleItemTaskParameters(articleId);
        taskDescription.setTaskParameters(new Gson().toJson(taskParameters));
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return null;
    }
    
    @RequestMapping("/grobid/affiliation/repository/{repositoryId}")
    public String startTasksRepositoryId(@PathVariable(value = "repositoryId")Integer repositoryId) throws UnsupportedEncodingException {
        List<Integer> teiFilesId = filesystemDAO.getTeiFilesIdByRepositoryID(repositoryId);
        StopWatch articlesProcess = new StopWatch();

        int i = 0;
        int testArticlesDumpNum = 20; // count of articles for testing dump
        boolean wholeRepository = true; // set true for test whole repository
        
        int articlesNumLimit = (wholeRepository) ? teiFilesId.size() : testArticlesDumpNum;
        
        articlesProcess.start("setOfArticles");
        for(Integer articleId : teiFilesId){
            i++;
            StopWatch signleArticleProcess = new StopWatch();
            
            TaskDescription taskDescription = new TaskDescription();
            taskDescription.setType(TaskType.fromString("grobid-affiliation-parser-item"));
            signleArticleProcess.start("singleArticle");
            SingleItemTaskParameters taskParameters = new SingleItemTaskParameters(articleId);
            taskDescription.setTaskParameters(new Gson().toJson(taskParameters));
            queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
            signleArticleProcess.stop();
            logger.debug("Article finished in time: " + signleArticleProcess.getLastTaskTimeMillis());
            
            if(i == articlesNumLimit){
                articlesProcess.stop();
                logger.debug(articlesNumLimit + " articles finished in time: " + articlesProcess.getTotalTimeMillis());
                logger.debug("Average for one article process is: " + articlesProcess.getTotalTimeMillis()/articlesNumLimit);
                break;
            }
        }
        return null;
    }

}
