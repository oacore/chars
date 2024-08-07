package uk.ac.core.database.service.bigRepository;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.model.BigRepoHarvestingStatistic;
import uk.ac.core.database.model.mappers.BigRepositoryMetricMapper;
import uk.ac.core.database.model.mappers.TaskHistoryMigrationMapper;

import java.sql.SQLNonTransientConnectionException;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class MySQLBigRepositoryDAO implements BigRepositoryDAO {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(BigRepositoryDAO.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public Pair<Date, Date> getLastSuccessDates(Integer repositoryId) {
        String sql = "SELECT min(from_date) as from_date, max(to_date) as to_date from task_history \n" +
                "where id_repository = ? and success = 1 GROUP by unique_id HAVING COUNT(*) = 2\n" +
                "ORDER BY max(id) desc limit 1";

        List<Pair<Date, Date>> result = jdbcTemplate.query(sql, (rs, num) ->
                        Pair.of(rs.getDate("from_date"), rs.getDate("to_date")),
                repositoryId);

        return !result.isEmpty() ? result.get(0) : null;
    }

    public List<String> getTaskParametersForFailedDD(Integer repositoryId, Date date){
        String sql = "SELECT MAX(task_parameters) " +
                "from task_history " +
                "WHERE id_repository = ? " +
                "  and task_type = \"download-document\"" +
                "  and creation_time > ? " +
                "GROUP BY task_parameters " +
                "HAVING sum(success)=0 " +
                "UNION\n" +
                "SELECT max(task_parameters) " +
                "from task_history " +
                "where id_repository = ? " +
                "GROUP by unique_id " +
                "HAVING COUNT(*) = 2 " +
                "   and sum(success) = 2 " +
                "   and MAX(creation_time) > ?;";
        List<String> response = jdbcTemplate.queryForList(sql, String.class, repositoryId,
                date, repositoryId, date);

        return !response.isEmpty() ? response : Collections.emptyList();
    }

    @Override
    public void updateLastHarvestingDate(Integer repositoryId, Date date) {
        this.jdbcTemplate.update(
                "UPDATE large_repositories SET last_harvesting_date = ? WHERE id_repository = ?",
                date,
                repositoryId
        );
    }

    @Override
    public List<BigRepoHarvestingStatistic> getBigRepoHarvestingSuccessStatistic() {
        int originalValue = this.jdbcTemplate.getQueryTimeout();
        this.jdbcTemplate.setQueryTimeout(300000);
        String sql =
                "with " +
                "large_repository_history as ( " +
                    "select l.id_repository, t.id, t.unique_id, t.task_type, t.task_parameters " +
                    "from task_history t " +
                    "inner join large_repositories l ON t.task_parameters like concat('%',  l.id_repository, ',%') " +
                    "where t.task_type = 'download-document' or t.task_type = 'extract-metadata' and t.success = 1), " +
                "large_repository_statistic as ( " +
                    "select id, id_repository, task_type, unique_id, task_parameters, " +
                    "rank() over (partition by id_repository, task_type order by id desc) as task_number " +
                    "from large_repository_history) " +
                "select id_repository, task_type, unique_id, task_parameters " +
                "from large_repository_statistic " +
                "where task_number = 1 " +
                "order by id_repository desc, id desc";

        List<BigRepoHarvestingStatistic> result = Collections.emptyList();
        try {
            result = this.jdbcTemplate.query(sql, new BigRepositoryMetricMapper());
        } catch (Exception e) {
            logger.error("Error while fetching big repository harvesting success statistic", e);
        }finally {
            this.jdbcTemplate.setQueryTimeout(originalValue);
        }

        return result;
    }

    public List<Pair<Integer, String>> getTaskHistoryRecordsForDatesMigration(Integer repositoryId){
        String sql = "SELECT id, task_parameters from task_history " +
                "where task_parameters LIKE '%:" + repositoryId + ",%' and creation_time > \"2021-08-19\";\n";
        List<Pair<Integer, String>> recordsForUpdating =
                this.jdbcTemplate.query(sql, new TaskHistoryMigrationMapper());

        return !recordsForUpdating.isEmpty() ? recordsForUpdating : Collections.emptyList();
    }

    public void updateDates(Integer taskHistoryId, LocalDateTime fromDate, LocalDateTime toDate, Integer repositoryId){
        String sql = "UPDATE task_history SET from_date = ?, to_date = ?, id_repository = ? where id = ?";
        this.jdbcTemplate.update(sql, fromDate, toDate , repositoryId,taskHistoryId);
    }
}
