package uk.ac.core.eventscheduler.broker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import uk.ac.core.common.constants.CHARSConstants;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskEvent;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.database.service.repositories.RepositoriesDAO;
import uk.ac.core.database.service.updates.TaskUpdatesDAO;
import uk.ac.core.queue.QueueInfoService;
import uk.ac.core.queue.QueueService;
import uk.ac.core.supervisor.client.SupervisorClient;
import uk.ac.core.workermetrics.data.dao.scheduling.SchedulingRepositoryDAO;
import uk.ac.core.workermetrics.data.entity.ScheduledRepository;
import uk.ac.core.workermetrics.data.state.RepositoryPriority;
import uk.ac.core.workermetrics.data.state.ScheduledState;

import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public class EventSchedulerQueueBroker {

    @Autowired
    QueueService queueService;

    @Autowired
    QueueInfoService queueInfoService;

    @Autowired
    TaskUpdatesDAO taskUpdatesDAO;

    @Autowired
    SchedulingRepositoryDAO schedulingRepositoryDAO;

    @Autowired
    SupervisorClient supervisorClient;

    @Autowired
    RepositoriesDAO repositoriesDAO;


    private static final Logger logger = LoggerFactory.getLogger("EventSchedulerQueueBroker");

    /**
     * Event message received from task-event queue (coming from workers)
     *
     * @param message
     */
    public void eventReceived(Object message) {
        try {
            String messageString = (String) message;
            logger.info("    [x] Received event : " + messageString);
            // deserialise to TaskEvent 
            TaskEvent taskEvent = new Gson().fromJson(messageString, TaskEvent.class);
            if (taskEvent.getTaskDescription() != null) {
                this.taskEventReceived(taskEvent);
            }
        } catch (Exception e) {
            System.out.println("Error while receiving task event message." + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Process taskEvent consumed from queue
     *
     * @param taskEvent
     * @throws Exception
     */
    public void taskEventReceived(TaskEvent taskEvent) throws Exception {
        if (taskEvent.getEvent().equals("START")) {
            receivedStartEventHandler(taskEvent);
        }
        if (taskEvent.getEvent().equals("FINISH")) {
            receivedFinishEventHandler(taskEvent);
        }
        scheduleNewOrOutdatedTasks();

    }

    private void receivedStartEventHandler(TaskEvent taskEvent) {
        // update status in scheduled_repository DB table
        ScheduledRepository scheduledRepository = new ScheduledRepository();
        Integer repositoryId = getIdRepositoryFromTaskEvent(taskEvent);
        scheduledRepository.setIdRepository(repositoryId);
        TaskType taskType = taskEvent.getTaskDescription().getType();
        ScheduledState newScheduledState = ScheduledState.fromTaskType(taskType);
        schedulingRepositoryDAO.updateStatus(scheduledRepository, newScheduledState);
    }

    private Integer getIdRepositoryFromTaskEvent(TaskEvent taskEvent) throws JsonSyntaxException {
        String taskParameters = taskEvent.getTaskDescription().getTaskParameters();
        RepositoryTaskParameters repositoryTaskParameters = new Gson().fromJson(taskParameters, RepositoryTaskParameters.class);
        return repositoryTaskParameters.getRepositoryId();
    }

    private void receivedFinishEventHandler(TaskEvent taskEvent) {
        ScheduledState newScheduledState = ScheduledState.PENDING;
        Integer repositoryId = getIdRepositoryFromTaskEvent(taskEvent);
        ScheduledRepository scheduledRepository = schedulingRepositoryDAO.getScheduledRepositoryByRepositoryId(repositoryId);
        if (taskEvent.getTaskStatus().isSuccess()) {
            //
            // schedule Next Task In Workflow
            //
            TaskDescription taskDescription = taskEvent.getTaskDescription();

            List<TaskType> taskList = taskDescription.getTaskList();

            //TODO all the queue publish shall happen through supervisor using the client - avoid publishing directly..
            if (taskList != null && !taskList.isEmpty()) {
                TaskType nextTaskType = null;
                for (int i = 0; i < taskList.size() - 1; i++) {
                    if (taskList.get(i) == taskDescription.getType()) {
                        // found
                        nextTaskType = taskList.get(i + 1);// no need to check for arrayindexoutofbounds since i am iterating until the pre-last element
                    }
                }

                if (nextTaskType != null) {

                    taskDescription.setType(nextTaskType);
                    taskDescription.setRoutingKey(nextTaskType.getName());
                    newScheduledState = ScheduledState.fromNextTaskType(nextTaskType);
                    logger.info("Repository {} changing STATE from {} to {}", scheduledRepository.getIdRepository(), newScheduledState.ordinal(), scheduledRepository.getScheduledState().ordinal());

                    schedulingRepositoryDAO.updateStatus(scheduledRepository, newScheduledState);
                    queueService.publish(taskDescription);
                    logger.info("newScheduledState = " + newScheduledState.name());

                }
            }
        }
        // update status in scheduled_repository DB table

        TaskType taskType = taskEvent.getTaskDescription().getType();
        handleNotifications(taskType, scheduledRepository);
    }

    /**
     * Schedule new or outdated repositories
     */
    @Scheduled(fixedRate = 600000)// every two minutes
    public void scheduleNewOrOutdatedTasks() {

        final int MAX_DOCUMENT_DOWNLOAD_QUEUE_SIZE = CHARSConstants.MAX_DOCUMENT_DOWNLOAD_QUEUE_SIZE;
        final TaskType DOWNLOAD_DOCUMENT_TASK_TYPE = TaskType.DOCUMENT_DOWNLOAD;
        final int MAX_METADATA_DOWNLOAD_QUEUE_SIZE = CHARSConstants.MAX_METADATA_DOWNLOAD_QUEUE_SIZE;
        final TaskType METADATA_DOWNLOAD_TASK_TYPE = TaskType.METADATA_DOWNLOAD;

        Integer itemsInDocumentDownloadQueue = queueInfoService.getCountMessages(DOWNLOAD_DOCUMENT_TASK_TYPE.getName() + CHARSConstants.QUEUE_SUFFIX);
        Integer itemsInMetadataDownloadQueue = queueInfoService.getCountMessages(METADATA_DOWNLOAD_TASK_TYPE.getName() + CHARSConstants.QUEUE_SUFFIX);

        if (itemsInDocumentDownloadQueue < MAX_DOCUMENT_DOWNLOAD_QUEUE_SIZE
            && itemsInMetadataDownloadQueue < MAX_METADATA_DOWNLOAD_QUEUE_SIZE) {

            int schedulingCapacity = MAX_METADATA_DOWNLOAD_QUEUE_SIZE - itemsInMetadataDownloadQueue;
            List<ScheduledRepository> scheduledRepositories = schedulingRepositoryDAO.getNextInLineOrderedBySchedulingScore(schedulingCapacity);
            for (ScheduledRepository sr : scheduledRepositories) {
                try {
                    this.scheduleHarvestingForRepository(sr);
                } catch (CHARSException e) {
                    logger.error("Exception: ", e);
                }
            }
        } else {
            logger.info("No space in one or both queues");
            logger.info(DOWNLOAD_DOCUMENT_TASK_TYPE.toString() + " queue (=" + itemsInDocumentDownloadQueue + "  > " + MAX_DOCUMENT_DOWNLOAD_QUEUE_SIZE + ")");
            logger.info(METADATA_DOWNLOAD_TASK_TYPE.toString() + " queue (=" + itemsInMetadataDownloadQueue + "  > " + MAX_METADATA_DOWNLOAD_QUEUE_SIZE + ")");
        }
    }

    private void scheduleHarvestingForRepository(ScheduledRepository scheduledRepository) throws CHARSException {
        if (scheduledRepository.getRepositoryPriority().equals(RepositoryPriority.SKIP)) {
            logger.info("Skipping repository {} due to priority: {}",
                    scheduledRepository.getIdRepository(),
                    scheduledRepository.getRepositoryPriority().name());
            return;
        }
        logger.debug("Scheduling repository: " + scheduledRepository.toString());
        // db updates
        schedulingRepositoryDAO.updateTimeLastScheduledToNow(scheduledRepository);
        schedulingRepositoryDAO.updatePreventReharvest(scheduledRepository);
        schedulingRepositoryDAO.updateStatus(scheduledRepository, ScheduledState.IN_DOWNLOAD_METADATA_QUEUE);
        // send it to the queue 
        supervisorClient.sendHarvestRepositoryRequest(scheduledRepository.getIdRepository());
    }

    private void handleNotifications(TaskType taskType, ScheduledRepository scheduledRepository) {


    }

}
