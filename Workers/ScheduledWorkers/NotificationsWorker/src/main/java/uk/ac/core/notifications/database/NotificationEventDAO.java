package uk.ac.core.notifications.database;

import uk.ac.core.notifications.model.NotificationEvent;
import uk.ac.core.notifications.model.UserNotificationProperties;

import java.util.List;
import java.util.Optional;

public interface NotificationEventDAO {

    Boolean insertNotificationEvent(NotificationEvent notificationEvent);

    List<NotificationEvent> getNotificationEvents(UserNotificationProperties properties);

    Optional<NotificationEvent> getLatestNotificationEvent(UserNotificationProperties properties);

    Optional<NotificationEvent> getLatestNotificationEvent(int orgId, int repoId, String type);
}
