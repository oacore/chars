package uk.ac.core.extractmetadata.worker;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.extractmetadata.worker.extractMetadataService.ExtractMetadataFactoryService;
import uk.ac.core.extractmetadata.worker.extractMetadataService.ExtractMetadataService;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersist;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersistFactory;
import uk.ac.core.extractmetadata.worker.taskitem.ExtractMetadataTaskItem;
import uk.ac.core.extractmetadata.worker.taskitem.ExtractMetadataTaskItemStatus;
import uk.ac.core.extractmetadata.worker.taskitem.ExtractMetadataTaskItemStatusFactory;
import uk.ac.core.worker.QueueWorker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Giorgio Basile
 * @since 04/04/2017
 */
public class ExtractMetadataWorker extends QueueWorker {

    private RepositoryTaskParameters repositoryTaskParameters;
    @Autowired
    ExtractMetadataFactoryService extractMetadataFactory;
    @Autowired
    ExtractMetadataTaskItemStatusFactory extractMetadataTaskItemStatusFactory;
    @Autowired
    ArticleMetadataPersistFactory articleMetadataPersistFactory;

    ArticleMetadataPersist persist;


    @Override
    public List<TaskItem> collectData() {

        this.repositoryTaskParameters = new Gson().fromJson(this.currentWorkingTask.getTaskParameters(), RepositoryTaskParameters.class);
        Integer repositoryId = repositoryTaskParameters.getRepositoryId();
        Date fromDate = repositoryTaskParameters.getFromDate();
        Date untilDate = repositoryTaskParameters.getToDate();

        logger.info("Creating metadata extractor");

        persist = articleMetadataPersistFactory.create(workerStatus.getTaskStatus(), repositoryId);
        ExtractMetadataService extractor = extractMetadataFactory.createExtractor(repositoryId, fromDate, untilDate,persist);

        logger.info("Collecting extraction task item");
        List<TaskItem> taskItems = new ArrayList<>();
        ExtractMetadataTaskItem extractMetadataTaskItem = new ExtractMetadataTaskItem();
        extractMetadataTaskItem.setExtractor(extractor);
        taskItems.add(extractMetadataTaskItem);
        logger.info("Finalizing collectData");
        return taskItems;
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {

    }


    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        logger.info("Start worker processing");
        // this is a single-item worker, therefore we set a TaskItemStatus as a TaskStatus for the worker
        ExtractMetadataTaskItemStatus extractMetadataTaskStatus = extractMetadataTaskItemStatusFactory.create(repositoryTaskParameters.getRepositoryId());
        workerStatus.setTaskStatus(extractMetadataTaskStatus);

        ExtractMetadataTaskItem extractMetadataTaskItem = (ExtractMetadataTaskItem) taskItems.get(0);

        extractMetadataTaskItem.getExtractor().run();
        persist.finalise(repositoryTaskParameters.getRepositoryId() != 4786);

        extractMetadataTaskStatus.setSuccess(evaluateExtraction(extractMetadataTaskStatus, extractMetadataTaskItem));

        return Arrays.asList(new TaskItemStatus[]{extractMetadataTaskStatus});

    }

    public boolean evaluateExtraction(TaskItemStatus result, TaskItem taskItem) {
        if(this.repositoryTaskParameters.getFromDate() != null){
            return result.getSuccessfulCount() >= 0;
        } else {
            return result.getSuccessfulCount() > 0;
        }
    }

}
