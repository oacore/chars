package uk.ac.core.workers.item.grobid;

import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.grobid.processor.exceptions.GrobidProcessingException;
import uk.ac.core.singleitemworker.SingleItemWorker;
/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
public class CitationParserWorker extends SingleItemWorker {

    Logger logger = Logger.getLogger(CitationParserWorker.class.getName());
    
    @Autowired
    CitationParserService grobidProcessorService;
    
    @Override
    public TaskItemStatus process(TaskDescription taskItemDescription) {
        String params = taskItemDescription.getTaskParameters();
        SingleItemTaskParameters singleItemTaskParameters = new Gson().fromJson(params, SingleItemTaskParameters.class);
        final Integer articleId = singleItemTaskParameters.getArticle_id();

        boolean success;
        try {
            success = grobidProcessorService.process(articleId);
        } catch (GrobidProcessingException grobidProcessingException) {
            logger.log(Level.ALL,grobidProcessingException.getMessage());
            success = false;
        } catch (IllegalAccessException grobidProcessingException) {
            logger.log(Level.ALL,grobidProcessingException.getMessage());
            success = false;
        }
        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(success);
        taskItemStatus.setTaskId(taskItemDescription.getUniqueId());
        return taskItemStatus;
    }

}
