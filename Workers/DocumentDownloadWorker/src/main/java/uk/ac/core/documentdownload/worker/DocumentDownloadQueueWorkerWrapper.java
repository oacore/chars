package uk.ac.core.documentdownload.worker;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.DocumentDownloadParameters;
import uk.ac.core.worker.BasicQueueWorker;
import java.util.List;

/**
 * Wraps DocuemntDownload implementations to load the correct instance based on
 * repository ID
 * @author samuel
 */
@Primary
@Service
public class DocumentDownloadQueueWorkerWrapper extends BasicQueueWorker {

    BasicQueueWorker instance;

    private DocumentDownloadParameters pdfDownloadParameters;
    
    @Autowired
    PubMedDownloadWorker pubMedDownloadWorker;
    
    @Autowired
    DefaultDocumentDownloadWorker defaultDocumentDownloadWorker;
    
    @Override
    public void prepare() {
        super.prepare();
        this.pdfDownloadParameters = new Gson().fromJson(this.currentWorkingTask.getTaskParameters(), DocumentDownloadParameters.class);

        int repositoryId = this.pdfDownloadParameters.getRepositoryId();
        if (repositoryId == 150) {
            this.instance = pubMedDownloadWorker;
        } else {
            this.instance = defaultDocumentDownloadWorker;
        }
        this.instance.setCurrentWorkingTask(currentWorkingTask);
    }
    
    @Override
    public List<TaskItem> collectData() {
        return instance.collectData();
    }

    @Override
    public TaskItemStatus processSingleItem(TaskItem item) {
        return instance.processSingleItem(item);
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {
        this.instance.collectStatistics(results);
    }

    public BasicQueueWorker getInstance() {
        return instance;
    }

    public void setInstance(BasicQueueWorker instance) {
        this.instance = instance;
    }
}
