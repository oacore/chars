package uk.ac.core.common.model.task;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public class HarvestWorkflow {

    private final List<TaskType> taskList;

        
    
    public HarvestWorkflow() {
        taskList = new ArrayList<>();
        taskList.add(TaskType.METADATA_DOWNLOAD);
        taskList.add(TaskType.EXTRACT_METADATA);
		
		/*
		To speed up the process of increasing our stats on metadata, pdf and fulltext tasks done
		we will need to index right after these two finish. 
		*/
		
//		taskList.add(TaskType.INDEX);
        taskList.add(TaskType.DOCUMENT_DOWNLOAD);

        // Blocked by CORE-2871
        taskList.add(TaskType.EXTENDED_METADATA_PROCESS);

//        taskList.add(TaskType.EXTRACT_TEXT);
//        taskList.add(TaskType.INDEX);
//        taskList.add(TaskType.THUMBNAIL_GENERATION);
    }
    
    public List<TaskType> getWorkflowFrom(TaskType taskType){
        return taskList.subList(taskList.indexOf(taskType), taskList.size());
    }
    
    public List<TaskType> getCompleteWorkflow(){
        return getWorkflowFrom(TaskType.METADATA_DOWNLOAD);
    }
    
    public static void main(String... args){
        HarvestWorkflow f = new HarvestWorkflow();
        List<TaskType> ff = f.getWorkflowFrom(TaskType.DOCUMENT_DOWNLOAD);
        System.out.println("ff = " + ff);
    }
}
