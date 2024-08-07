package uk.ac.core.documentdownload.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.worker.BasicQueueWorker;
import java.util.Collections;
import java.util.List;

@Service
public class PubMedDownloadWorker extends BasicQueueWorker {

    @Autowired
    PubMedPdfDownloadWorker pubMedPdfDownloadWorker;

    @Override
    public List<TaskItem> collectData() {
        return Collections.emptyList();
    }

    //
    @Override
    public TaskItemStatus processSingleItem(TaskItem item) {
        pubMedPdfDownloadWorker.processPdfDownload();
        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(true);     
        taskItemStatus.setProcessedCount(pubMedPdfDownloadWorker.getItemsProcessed() - pubMedPdfDownloadWorker.getSkippedItems());
        taskItemStatus.setNumberOfItemsToProcess(pubMedPdfDownloadWorker.getItemsProcessed());
        return taskItemStatus;
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {

    }
}
