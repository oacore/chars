package uk.ac.core.database.service.updates.impl;

import com.google.gson.Gson;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import uk.ac.core.common.constants.CHARSConstants;
import uk.ac.core.common.model.legacy.ActionType;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskStatus;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.common.model.util.Converter;
import uk.ac.core.database.model.TaskUpdate;
import uk.ac.core.database.model.TaskUpdateReporting;
import uk.ac.core.database.model.TaskUpdateStatus;
import uk.ac.core.database.service.updates.TaskUpdatesDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MySqlTaskUpdatesDAO implements TaskUpdatesDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySqlTaskUpdatesDAO.class);

    @Override
    public TaskUpdate getLastRepositoryUpdateByRepositoryId(Integer repositoryId) {
        return getLastRepositoryUpdateByRepositoryId(repositoryId, null, null);
    }

    @Override
    public TaskUpdate getLastRepositoryUpdateByRepositoryId(Integer repositoryId, TaskUpdateStatus status) {
        return getLastRepositoryUpdateByRepositoryId(repositoryId, status, null);
    }

    @Override
    public TaskUpdate getLastRepositoryUpdateByRepositoryId(Integer repositoryId, TaskUpdateStatus status, ActionType type) {
        return this.getLastRepositoryUpdateByRepositoryId(repositoryId, status, type, null);
    }

    /**
     * Get last repository update with specified status and operation, which was
     * done after given stringDate.
     *
     * @param repositoryId
     * @param status
     * @param type
     * @param date
     * @return
     */
    public TaskUpdate getLastRepositoryUpdateByRepositoryId(
            Integer repositoryId, TaskUpdateStatus status, ActionType type, Date date) {

        String statusString = (status != null) ? " AND status = ? " : "";
        String typeString = (type != null) ? " AND operation = ? " : "";
        String dateString = (date != null) ? " AND last_update_time >= ?" : "";
        Integer index = 0;

        String sql = "SELECT id_document, id_update, last_update_time, id_repository, "
                + "status, operation, created "
                + "FROM `update` "
                + "WHERE id_repository = ? " + statusString + typeString + dateString + " "
                + "ORDER BY id_update DESC LIMIT 1";

        List<Object> paramsList = new ArrayList<>();

        paramsList.add(index++, repositoryId);

        if (status != null) {
            paramsList.add(index++, status.toString());
        }

        if (type != null) {
            paramsList.add(index++, type.toString());
        }

        if (date != null) {
            paramsList.add(index++, new java.sql.Date(date.getTime()));
        }

        RowMapper<TaskUpdate> mapper = new TaskUpdatesMapper();
        TaskUpdate taskUpdate = jdbcTemplate.queryForObject(sql, paramsList.toArray(), mapper);
        return taskUpdate;
    }

    @Override
    public void testCon() {
        System.out.println("Testing mySQL connection");
        String TEST_CONN_SQL = "SELECT * FROM `update` LIMIT 2";
        RowMapper<TaskUpdate> rowMapper = new TaskUpdatesMapper();
        List<TaskUpdate> updates = jdbcTemplate.query(TEST_CONN_SQL, rowMapper);
        for (TaskUpdate tu : updates) {
            System.out.println(tu.toString());
        }

    }

    final String[] LARGE_REPOSITORIES = {
        "143",// DOAJ old
        "144",// arxiv
        "145",// CiteSeerX
        "645",// DOAJ new
        "153",// Repec
        "150"// PubMed
    };

    @Override
    public List<Integer> getOutdatedUKRepositories(Integer limit) {

        String query = "SELECT f.id_repository FROM (" + FRESHNESS_QUERY + ") f WHERE f.freshness > ? LIMIT ?";

        List<Integer> updates = jdbcTemplate.queryForList(query, new Object[]{String.join(",", LARGE_REPOSITORIES), "GB", CHARSConstants.MAX_REPOSITORY_AGE, limit}, Integer.class);

        return updates;

    }

    @Override
    public List<Integer> getOutdatedNonUKRepositories(Integer limit) {
        String query = "SELECT f.id_repository FROM (" + FRESHNESS_QUERY + ") f WHERE f.freshness > ? AND f.country_code <> 'GB' LIMIT ?";

        List<Integer> updates = jdbcTemplate.queryForList(query, new Object[]{String.join(",", LARGE_REPOSITORIES), "%%", CHARSConstants.MAX_REPOSITORY_AGE, limit}, Integer.class);

        return updates;

    }

    private static final String FRESHNESS_QUERY = ""
            + "SELECT a.id_repository,\n"
            + " country_code, \n"
            + " DATEDIFF(NOW(), MAX(last_update_time)) AS `freshness`,\n"
            + " MAX(last_update_time) AS last_update,\n"
            + " a.metadata_format\n"
            + "FROM\n"
            + "    `core`.`update` AS b\n"
            + "    LEFT JOIN\n"
            + "    `core`.`repository` AS a ON a.id_repository = b.id_repository\n"
            + "        LEFT JOIN\n"
            + "    core.repository_location AS c ON a.id_repository = c.id_repository\n"
            + "WHERE\n"
            + "    `operation` = 'pdfs'\n"
            + "     AND a.disabled = 0\n"
            + "     AND a.id_repository NOT IN (?)"
            + "     AND `country_code` LIKE ?\n"
            + "GROUP BY a.name\n"
            + "ORDER BY last_update ASC";

    @Override
    public void saveTaskUpdate(final TaskStatus taskStatus, final TaskDescription taskDescription) {
        final RepositoryTaskParameters repositoryTaskParameters = new Gson().fromJson(taskDescription.getTaskParameters(), RepositoryTaskParameters.class
        );
        String SAVE_UPDATE_SQL
                = "INSERT INTO `update` (id_repository, created, last_update_time, "
                + "status, operation) "
                + "VALUES (?, ?, NOW(), ?, ?)";
        PreparedStatementSetter pss = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, repositoryTaskParameters.getRepositoryId());
                ps.setTimestamp(2,  new java.sql.Timestamp(taskDescription.getCreationTime()));
                ps.setString(3, (taskStatus.isSuccess()) ? "successful" : "unsuccessful");
                ps.setString(4, Converter.fromTaskTypeToActionTypeString(taskDescription.getType()));
            }
        };
        jdbcTemplate.update(SAVE_UPDATE_SQL, pss);

    }

    @Override
    public void saveSingleItemTaskUpdate(final TaskDescription taskDescription, final TaskItemStatus taskItemStatus) {
        //MC: deleted because is destroying the Update table ad we don\t care of updates on single item, if you care, think twice.

    }

    @Override
    public List<TaskUpdateReporting> getUpdatesOfTheDayForReporting() {
        String query = "SELECT * FROM core.`update` u INNER JOIN core.`repository_location` rl ON rl.id_repository=u.id_repository \n"
                + "WHERE u.last_update_time >= ( CURDATE() - INTERVAL 1 DAY )\n"
                + "AND u.operation IN ('pdfs', 'metadata_download', 'metadata_extract') ORDER BY u.last_update_time LIMIT 10000;";
        this.jdbcTemplate.setQueryTimeout(600);
        List<TaskUpdateReporting> taskUpdatesReporting = null;
        try {
            taskUpdatesReporting = this.jdbcTemplate.query(query, new TaskUpdatesReportingMapper());
        } catch (QueryTimeoutException e) {
            logger.info("Updates of the day cancelled because of timeout, we don't want to hit too much the db");
        } finally {
            this.jdbcTemplate.setQueryTimeout(-1);
        }
        return taskUpdatesReporting;
    }

    @Override
    public Double getAverageFreshnessWithCountryCode(String countryCode) {

        String query = "SELECT AVG(f.freshness) FROM (" + FRESHNESS_QUERY + ") f";
        return this.jdbcTemplate.queryForObject(query, new Object[]{LARGE_REPOSITORIES, countryCode}, Double.class);
    }

    @Override
    public Double getAverageFreshness() {
        return getAverageFreshnessWithCountryCode("%%");
    }

    @Override
    public List<Map<String, Object>> getRepositoriesWithBadFreshness() {

        String sql = "SELECT fq.id_repository, fq.freshness, fq.last_update, fq.metadata_format " +
                "FROM (" + FRESHNESS_QUERY + ") fq " +
                "left join scheduled_repository sr on fq.id_repository = sr.id_repository " +
                "where sr.repository_priority <> 'SKIP'" +
                "order by fq.last_update";

        // temporary limit set to 25
        return this.jdbcTemplate.queryForList(
                sql,
                String.join(",", LARGE_REPOSITORIES),
                "%%"
        ).subList(0, 25);
    }

    private static final class TaskUpdatesMapper implements RowMapper<TaskUpdate> {

        @Override
        public TaskUpdate mapRow(ResultSet rs, int rowNum) throws SQLException {
            TaskUpdate tu = new TaskUpdate();
            tu.setIdUpdate(rs.getLong("id_update"));
            tu.setRepositoryId(rs.getInt("id_repository"));
            tu.setArticleId(rs.getInt("id_document"));
            tu.setCreated(rs.getDate("created"));
            tu.setLastUpdateTime(rs.getDate("last_update_time"));
            tu.setStatus(TaskUpdateStatus.fromString(rs.getString("status")));
            tu.setOperation(ActionType.fromString(rs.getString("operation")));
            return tu;
        }
    }

    private static final class TaskUpdatesReportingMapper implements RowMapper<TaskUpdateReporting> {

        @Override
        public TaskUpdateReporting mapRow(ResultSet rs, int rowNum) throws SQLException {
            TaskUpdateReporting tu = new TaskUpdateReporting();
            tu.setIdUpdate(rs.getLong("id_update"));
            tu.setRepositoryId(rs.getInt("id_repository"));
            tu.setArticleId(rs.getInt("id_document"));
            tu.setCreated(rs.getTimestamp("created"));
            tu.setLastUpdateTime(rs.getTimestamp("last_update_time"));
            tu.setStatus(TaskUpdateStatus.fromString(rs.getString("status")));
            tu.setOperation(ActionType.fromString(rs.getString("operation")));
            tu.setCountryCode(rs.getString("country_code"));
            return tu;
        }
    }
}
