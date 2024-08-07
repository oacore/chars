package uk.ac.core.workers.item.grobid;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.singleitemworker.SingleItemWorker;
import uk.ac.core.workers.item.grobid.exceptions.GrobidExtractionException;

/**
 *
 * @author lucasanastasiou
 */
public class GrobidExtractorWorker extends SingleItemWorker {

    @Autowired
    private GrobidExtractionService grobidExtractionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GrobidExtractorWorker.class);

    @Override
    public TaskItemStatus process(TaskDescription taskDescription) {
        String params = taskDescription.getTaskParameters();
        SingleItemTaskParameters singleItemTaskParameters = new Gson().fromJson(params, SingleItemTaskParameters.class);
        final Integer articleId = singleItemTaskParameters.getArticle_id();

        boolean success;
        try {
            success = grobidExtractionService.extractAndStore(articleId);
        } catch (GrobidExtractionException grobidExtractionException) {
            grobidExtractionException.printStackTrace();
            LOGGER.info(grobidExtractionException.getMessage());
            success = false;
        }
        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(success);
        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;
    }

}
