package uk.ac.core.common.model.task.item;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.ReindexItemTaskParameters;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class TaskItemBuilder {

    public TaskItemBuilder() {
    }

    public TaskDescription buildSingleItemWorkflow(Integer articleId) {
        return this.buildSingleItemWorkflow(articleId, Boolean.TRUE);
    }

    public TaskDescription buildSingleItemWorkflow(Integer articleId, Boolean wasSuccessful) {
        TaskDescription taskItemDescription = new TaskDescription();

        List<TaskType> workflowTaskList = new ArrayList<>();
        if (wasSuccessful) {
            workflowTaskList.add(TaskType.EXTRACT_TEXT_ITEM);
        }
//        workflowTaskList.add(TaskType.GROBID_EXTRACTION_ITEM);
        workflowTaskList.add(TaskType.ITEM_LANG_DETECTION);
        workflowTaskList.add(TaskType.INDEX_ITEM);
        workflowTaskList.add(TaskType.WORKS_INDEX_ITEM);
//        workflowTaskList.add(TaskType.PDF_DECORATE_ITEM);
        
        // We want indexing to happen ASAP
        // This can be a bottleneck so let's do this after index
        if (wasSuccessful) {
            workflowTaskList.add(TaskType.THUMBNAIL_GENERATION_ITEM);
        }
//       workflowTaskList.add(TaskType.ITEM_DOI_RESOLUTION);

        taskItemDescription.setTaskList(workflowTaskList);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("article_id", articleId);

        SingleItemTaskParameters singleItemTaskParameters = new SingleItemTaskParameters(articleId);
        taskItemDescription.setTaskParameters(new Gson().toJson(singleItemTaskParameters));

        // - start from
        if (wasSuccessful) {
            taskItemDescription.setType(TaskType.EXTRACT_TEXT_ITEM);
        } else {
            taskItemDescription.setType(TaskType.INDEX_ITEM);
        }

        return taskItemDescription;
    }

    public TaskDescription buildSingleItemTask(TaskType taskType, Integer articleId) {
        SingleItemTaskParameters singleItemTaskParameters = new SingleItemTaskParameters(articleId);        
        return this.buildItemTask(taskType, singleItemTaskParameters);
    }

    public TaskDescription buildReindexItem(String indexName, Integer coreInternalId) {
        ReindexItemTaskParameters reindexItemTaskParameters = new ReindexItemTaskParameters(indexName, coreInternalId);
        return this.buildItemTask(TaskType.REINDEX_ITEM, reindexItemTaskParameters);
    }
    
    public TaskDescription buildItemTask(TaskType taskType, SingleItemTaskParameters taskParameters) {
        TaskDescription taskItemDescription = new TaskDescription();
        taskItemDescription.setTaskParameters(new Gson().toJson(taskParameters));
        taskItemDescription.setType(taskType);
        return taskItemDescription;
    }
}
