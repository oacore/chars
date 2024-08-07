package uk.ac.core.notifications.database.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.notifications.database.NotificationEventDAO;
import uk.ac.core.notifications.model.NotificationEvent;
import uk.ac.core.notifications.model.UserNotificationProperties;

import java.util.List;
import java.util.Optional;

@Service
public class MySqlNotificationEventDAO implements NotificationEventDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Boolean insertNotificationEvent(NotificationEvent notificationEvent) {
        int result = this.jdbcTemplate.update("INSERT INTO notification_events  "
                        + "(id, organisation_id,repository_id, type, payload, created_date)"
                        + "VALUES (?,?,?,?,?,?)",
                null,
                notificationEvent.getOrganisation(),
                notificationEvent.getRepositoryId(),
                notificationEvent.getType(),
                notificationEvent.getPayload(),
                notificationEvent.getCreatedDate()
        );
        return result > 0;
    }

    @Override
    public List<NotificationEvent> getNotificationEvents(final UserNotificationProperties properties) {
        String sql = "select ne.* " +
                "from notification_events ne " +
                "where ne.organisation_id = ? and " +
                "      ne.type = ? and " +
                "      ne.created_date > ?";
        return this.jdbcTemplate.query(
                sql,
                preparedStatement -> {
                    preparedStatement.setInt(1, properties.getOrgId());
                    preparedStatement.setString(2, properties.getType());
                    preparedStatement.setDate(3,
                            java.sql.Date.valueOf(properties.getLastEmailSent().toLocalDate()));
                },
                (resultSet, i) -> {
                    NotificationEvent ne = new NotificationEvent();
                    ne.setId(resultSet.getInt("id"));
                    ne.setOrganisation(resultSet.getInt("organisation_id"));
                    ne.setRepositoryId(resultSet.getInt("repository_id"));
                    ne.setType(resultSet.getString("type"));
                    ne.setPayload(resultSet.getString("payload"));
                    ne.setCreatedDate(resultSet.getTimestamp("created_date"));
                    return ne;
                }
        );
    }

    @Override
    public Optional<NotificationEvent> getLatestNotificationEvent(final UserNotificationProperties properties) {
        String sql = "select ne.* " +
                "from notification_events ne " +
                "where ne.organisation_id = ? and " +
                "      ne.type = ? and " +
                "      ne.created_date > ? " +
                "order by ne.created_date desc " +
                "limit 1";
        List<NotificationEvent> result = this.jdbcTemplate.query(sql,
                (resultSet, i) -> {
                    NotificationEvent ne = new NotificationEvent();
                    ne.setId(resultSet.getInt("id"));
                    ne.setOrganisation(resultSet.getInt("organisation_id"));
                    ne.setRepositoryId(resultSet.getInt("repository_id"));
                    ne.setType(resultSet.getString("type"));
                    ne.setCreatedDate(resultSet.getTimestamp("created_date"));
                    ne.setPayload(resultSet.getString("payload"));
                    return ne;
                },
                properties.getOrgId(),
                properties.getType(),
                properties.getLastEmailSent());
        return Optional.ofNullable(result.isEmpty() ? null : result.get(0));
    }

    @Override
    public Optional<NotificationEvent> getLatestNotificationEvent(int orgId, int repoId, String type) {
        String sql = "select ne.* " +
                "from notification_events ne " +
                "where ne.organisation_id = ? and " +
                "      ne.type = ? and " +
                "      ne.repository_id = ? " +
                "order by ne.created_date desc " +
                "limit 1";
        List<NotificationEvent> result = this.jdbcTemplate.query(
                sql,
                (resultSet, i) -> {
                    NotificationEvent ne = new NotificationEvent();
                    ne.setId(resultSet.getInt("id"));
                    ne.setOrganisation(resultSet.getInt("organisation_id"));
                    ne.setRepositoryId(resultSet.getInt("repository_id"));
                    ne.setType(resultSet.getString("type"));
                    ne.setCreatedDate(resultSet.getTimestamp("created_date"));
                    ne.setPayload(resultSet.getString("payload"));
                    return ne;
                },
                orgId,
                type,
                repoId);
        return Optional.ofNullable(result.isEmpty() ? null : result.get(0));
    }
}
