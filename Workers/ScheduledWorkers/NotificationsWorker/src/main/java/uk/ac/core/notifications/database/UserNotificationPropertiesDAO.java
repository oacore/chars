package uk.ac.core.notifications.database;

import uk.ac.core.notifications.model.UserNotificationProperties;

import java.util.List;

public interface UserNotificationPropertiesDAO {
    List<UserNotificationProperties> getUsersWaitingForEmail();
    void updateLastEmailSent(UserNotificationProperties properties);
}
