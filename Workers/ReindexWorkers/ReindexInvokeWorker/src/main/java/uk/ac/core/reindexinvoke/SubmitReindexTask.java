package uk.ac.core.reindexinvoke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.task.item.TaskItemBuilder;
import uk.ac.core.queue.QueueItemService;
import uk.ac.core.supervisor.client.SupervisorClient;
import java.util.concurrent.Callable;

/**
 *
 * @author lucasanastasiou
 */
public class SubmitReindexTask implements Callable<Boolean>{

    private final String indexName;
    private final Integer coreInternalId;
    private SupervisorClient supervisorClient;
    
//    private final SupervisorClient supervisorClient;
    
    QueueItemService queueItemService;
    
    TaskItemBuilder taskItemBuilder;
    
    private Logger logger = LoggerFactory.getLogger("SubmitReindexTask");

//    public SubmitReindexTask(SupervisorClient supervisorClient, String indexName, Integer coreInternalId) {
//        this.supervisorClient = supervisorClient;
//        this.indexName = indexName;
//        this.coreInternalId = coreInternalId;
//    }
    
    public SubmitReindexTask(QueueItemService queueItemService, TaskItemBuilder taskItemBuilder, String indexName, Integer coreInternalId){
        this.queueItemService = queueItemService;
        this.taskItemBuilder = taskItemBuilder;
        this.indexName = indexName;
        this.coreInternalId = coreInternalId;
    }

    @Override
    public Boolean call() throws Exception {
        
        //TaskDescription taskDescription = this.taskItemBuilder.buildReindexItem(indexName, coreInternalId);
        //this.queueItemService.publish(taskDescription);

        try {
            logger.info("sending request for article "+coreInternalId.toString());
            supervisorClient.sendIndexItemRequest(coreInternalId);
        }catch (CHARSException e){
            logger.error("cannot send reindex request for article:"+coreInternalId.toString(),e);
            e.printStackTrace();
            return false;
        }
        return true;
    }





}
