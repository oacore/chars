package uk.ac.core.notification.api;

import uk.ac.core.common.model.task.TaskType;

/**
 *
 * @author lucasanastasiou
 */
public interface NotificationService {
    public void sendFinishTaskNotificationToWatchers(Integer idRepository, TaskType taskType) throws CannotSendEmailException;
    public void sendFinishHarvestingNotificationToWatchers(Integer idRepository) throws CannotSendEmailException;
}
