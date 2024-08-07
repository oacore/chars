package uk.ac.core.opendoar.importer;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.opendoar.importer.connector.OpenDOARConnector;
import uk.ac.core.opendoar.importer.connector.json.Item;
import uk.ac.core.opendoar.importer.connector.model.OpenDOARRepository;
import uk.ac.core.worker.ScheduledWorker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class OpenDoarImporterWorker extends ScheduledWorker {

    @Autowired
    OpenDOARConnector openDOARConnector;

    List<String> errors = new ArrayList<>();

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OpenDoarImporterWorker.class);

    private TaskType taskType = TaskType.OPENDOAR_IMPORT;

    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {
        Integer success = 0;
        Integer numberOfReposTotal = 0;
        for (TaskItemStatus result : results) {
            if (result.isSuccess()) {
                success++;
            }
            numberOfReposTotal = result.getNumberOfItemsToProcess();
        }
        String errormsgs = "";
        for (String errorMsg : errors) {
            errorMsg = errorMsg + "<br>" + errormsgs;
        }
        String mailMsg = String.format("%s --- Inserted %d over %d repositories in OpenDOAR ", new Object[]{new Date().toString(), success, numberOfReposTotal});

        mailMsg = mailMsg + "<br>---<br>" + errormsgs;
        logger.debug(mailMsg);
        return mailMsg;
    }

    @Override
    public List<TaskItem> collectData() {
        List<TaskItem> openDOARTaskItems = new ArrayList<>();

        try {
            List<Item> repositories = openDOARConnector.downloadRepositories();
            repositories.stream().map((repository) -> {
                OpenDOARTaskItem openDOARTaskItem = new OpenDOARTaskItem();
                OpenDOARRepository openDOARRepository = new OpenDOARRepository();
                openDOARRepository.setId(repository.getSystemMetadata().getId());
                openDOARRepository.setItem(repository);
                openDOARTaskItem.setOpenDOARRepository(openDOARRepository);
                return openDOARTaskItem;
            }).forEachOrdered((openDOARTaskItem) -> {
                openDOARTaskItems.add(openDOARTaskItem);
            });

        } catch (IOException ex) {
            Logger.getLogger(OpenDoarImporterWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return openDOARTaskItems;
    }

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        List<TaskItemStatus> statuses = new ArrayList<>();
        for (TaskItem taskItem : taskItems) {

            OpenDOARTaskItem openDOARTaskItem = (OpenDOARTaskItem) taskItem;
            OpenDOARRepository openDOARRepository = openDOARTaskItem.getOpenDOARRepository();
            try {
                Boolean wasUpdated = this.openDOARConnector.synchronize(openDOARRepository);
                TaskItemStatus taskItemStatus = new TaskItemStatus();
                taskItemStatus.setSuccess(wasUpdated);
                taskItemStatus.setNumberOfItemsToProcess(taskItems.size());
                statuses.add(taskItemStatus);
            } catch (Exception e) {
                String errormsg = String.format("Problem with processing OPENDOAR ID %s Exceptions %s", openDOARRepository.getId(), e.getMessage());
                logger.error(errormsg, e);
                errors.add(errormsg);
            } finally {
                TaskItemStatus taskItemStatus = new TaskItemStatus();
                taskItemStatus.setSuccess(false);
                taskItemStatus.setNumberOfItemsToProcess(taskItems.size());
                statuses.add(taskItemStatus);
            }
        }
        return statuses;
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {

    }

    //execute at midnight on Monday
    @Override
    @Scheduled(cron = "0 0 0 * * MON")
    public void scheduledStart() {
        this.start();
    }

    @Override
    public TaskDescription generateTaskDescription() {
        TaskDescription task = new TaskDescription();
        task.setCreationTime(System.currentTimeMillis());
        task.setStartTime(System.currentTimeMillis());
        task.setType(taskType);
        return task;
    }

    @Override
    public TaskType getTaskType() {
        return this.taskType;
    }

}
