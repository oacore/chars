package uk.ac.core.notifications.database.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.notifications.database.UserNotificationPropertiesDAO;
import uk.ac.core.notifications.model.UserNotificationProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MySqlUserNotificationPropertiesDAO implements UserNotificationPropertiesDAO {
    private static final Logger log = LoggerFactory.getLogger(MySqlUserNotificationPropertiesDAO.class);

    private final JdbcTemplate template;

    @Autowired
    public MySqlUserNotificationPropertiesDAO(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public List<UserNotificationProperties> getUsersWaitingForEmail() {
        String sql = "select * from user_notification_properties unp";

        return this.template.query(sql, (resultSet, i) -> {
            UserNotificationProperties unp = new UserNotificationProperties();

            unp.setId(resultSet.getInt("id"));
            unp.setOrgId(resultSet.getInt("organisation_id"));
            unp.setUserId(resultSet.getInt("user_id"));
            unp.setType(resultSet.getString("type"));
            unp.setInterval(resultSet.getString("datetime_interval"));

            String lastEmailSentString = resultSet.getString("last_email_sent");
            if (lastEmailSentString != null) {
                unp.setLastEmailSent(LocalDateTime.parse(
                        resultSet.getString("last_email_sent"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
                ));
            }

            return unp;
        });
    }

    @Override
    public void updateLastEmailSent(UserNotificationProperties properties) {
        String sql = "" +
                "update user_notification_properties " +
                "set last_email_sent = NOW() " +
                "where id = ?";
        this.template.update(
                sql,
                ps -> ps.setInt(1, properties.getId()));
    }
}
