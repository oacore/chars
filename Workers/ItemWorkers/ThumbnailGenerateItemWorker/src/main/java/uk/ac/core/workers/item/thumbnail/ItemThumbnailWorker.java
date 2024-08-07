package uk.ac.core.workers.item.thumbnail;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.core.common.model.legacy.PreviewStatus;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.database.service.document.DocumentDAO;
import uk.ac.core.singleitemworker.SingleItemWorker;
import uk.ac.core.workers.item.thumbnail.service.GenerateThumbnailService;

/**
 *
 * @author lucasanastasiou
 */
public class ItemThumbnailWorker extends SingleItemWorker {

    @Autowired
    GenerateThumbnailService generateThumbnailService;

    @Autowired
    DocumentDAO documentDAO;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ItemThumbnailWorker.class);

    @Autowired
    DocumentDAO mySQLDocumentDAO;

    @Override
    public TaskItemStatus process(TaskDescription taskDescription) {

        String params = taskDescription.getTaskParameters();
        SingleItemTaskParameters singleItemTaskParameters = new Gson().fromJson(params, SingleItemTaskParameters.class);
        final Integer articleId = singleItemTaskParameters.getArticle_id();
        boolean success = false;
        if (!mySQLDocumentDAO.getPreviewStatus(articleId.toString())) {
            long startTimeProcess = System.currentTimeMillis();
            try {
                success = generateThumbnailService.createImagePreview(articleId);
            } catch (FileNotFoundException ex) {
                logger.debug("File Not Found: " + ex.getMessage(), ex);
                success = false;
            }
            long endTimeProcess = System.currentTimeMillis();
            logger.debug("Process status for " + articleId + ": " + success + ". Took: " + (endTimeProcess - startTimeProcess) + " ms");

            long startTimeSql = System.currentTimeMillis();
            if (success) {
                documentDAO.setPreviewStatus(articleId, PreviewStatus.PROCESSED);
            } else {
                documentDAO.setPreviewStatus(articleId, PreviewStatus.ERROR);
            }
            long endTimesql = System.currentTimeMillis();
            logger.debug("Saved " + articleId + ": " + success + ". Took: " + (endTimesql - startTimeSql) + " ms");
        } else {
            // We skipped it, but we need to signal upstream that nothing went wrong
            success = true;
        }
        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(success);
        return taskItemStatus;

    }

}
