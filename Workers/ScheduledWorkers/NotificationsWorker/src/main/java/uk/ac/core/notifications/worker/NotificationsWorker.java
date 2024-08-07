package uk.ac.core.notifications.worker;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.database.service.task.TaskDAO;
import uk.ac.core.notifications.database.DashboardOrganisationDAO;
import uk.ac.core.notifications.database.NotificationEventDAO;
import uk.ac.core.notifications.model.NotificationEvent;
import uk.ac.core.worker.ScheduledWorker;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class NotificationsWorker extends ScheduledWorker {
    protected static final Logger logger = LoggerFactory.getLogger(NotificationsWorker.class);
    private static final String HARVEST_COMPLETED = "harvest-completed";
    private static final String DEDUPLICATION_COMPLETED = "deduplication-completed";
    private static final int PERIOD_IN_DAYS = 7;
    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private DashboardOrganisationDAO dashboardOrganisationDAO;
    @Autowired
    private NotificationEventDAO notificationEventDAO;

    private Map<Integer, List<Integer>> repositoriesToOrganisationMap;


    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        List<TaskDescription> harvestCompletedTaskDescriptions = this.taskDAO.findSuccessfulTasksByPeriod(
                TaskType.EXTRACT_METADATA.getName(), PERIOD_IN_DAYS);
        List<TaskDescription> deduplicationTaskDescriptions = this.taskDAO.findSuccessfulTasksByPeriod(
                TaskType.DOCUMENT_DOWNLOAD.getName(), PERIOD_IN_DAYS);
        List<TaskItemStatus> statuses = new ArrayList<>();
        logger.info("Creating notification events for `{}` emails", HARVEST_COMPLETED);
        for (TaskDescription taskDescription : harvestCompletedTaskDescriptions) {
            statuses.add(createNotificationsForTask(taskDescription, HARVEST_COMPLETED));
        }
        logger.info("Processed {} records", harvestCompletedTaskDescriptions.size());
        logger.info("Creating notification events for `{}` emails", DEDUPLICATION_COMPLETED);
        for (TaskDescription taskDescription : deduplicationTaskDescriptions) {
            statuses.add(createNotificationsForTask(taskDescription, DEDUPLICATION_COMPLETED));
        }
        logger.info("Processed {} records", deduplicationTaskDescriptions.size());
        return statuses;
    }

    private TaskItemStatus createNotificationsForTask(TaskDescription taskDescription, String type) {
        TaskItemStatus result = new TaskItemStatus();
        RepositoryTaskParameters repositoryTaskParameters =
                new Gson().fromJson(taskDescription.getTaskParameters(), RepositoryTaskParameters.class);
        Integer repositoryId = repositoryTaskParameters.getRepositoryId();
        List<Integer> organisationToNotify = this.getOrganisationsForRepo(repositoryId);
        if (organisationToNotify == null) {
            return result;
        }
        for (Integer organisationId : organisationToNotify) {
            NotificationEvent latest =
                    this.notificationEventDAO.getLatestNotificationEvent(organisationId, repositoryId, type)
                            .orElse(null);
            if (latest != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime created = latest.getCreatedDate().toLocalDateTime();
                long diffDays = Duration.between(created, now).toDays();
                logger.info("Previous notification created {} days ago", diffDays);
                if (diffDays >= PERIOD_IN_DAYS) {
                    logger.info("Creating notification event for `{}` email", type);
                    this.insertNotificationEvent(organisationId, repositoryId, type);
                }
            } else {
                this.insertNotificationEvent(organisationId, repositoryId, type);
            }
        }
        result.setSuccess(true);
        return result;
    }

    private void insertNotificationEvent(int orgId, int repoId, String type) {
        NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        notificationEvent.setOrganisation(orgId);
        notificationEvent.setRepositoryId(repoId);
        notificationEvent.setType(type);
        notificationEvent.setPayload(null);
        this.notificationEventDAO.insertNotificationEvent(notificationEvent);
    }

    private List<Integer> getOrganisationsForRepo(Integer repositoryId) {
        return this.repositoriesToOrganisationMap.get(repositoryId);

    }


    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {
        return "Notifications events are collected";
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.NOTIFICATIONS;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledStart() {
        this.start();
    }

    @Override
    public TaskDescription generateTaskDescription() {
        TaskDescription task = new TaskDescription();
        task.setCreationTime(System.currentTimeMillis());
        task.setStartTime(System.currentTimeMillis());
        task.setType(getTaskType());
        return task;
    }

    @Override
    public List<TaskItem> collectData() {

        this.repositoriesToOrganisationMap = this.dashboardOrganisationDAO.loadRepositoryToOrganisationRelations();


        return Collections.emptyList();
    }


    @Override
    public void collectStatistics(List<TaskItemStatus> results) {

    }
}
