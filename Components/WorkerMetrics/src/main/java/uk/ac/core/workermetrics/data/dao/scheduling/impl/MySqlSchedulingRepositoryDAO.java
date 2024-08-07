package uk.ac.core.workermetrics.data.dao.scheduling.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import uk.ac.core.workermetrics.data.dao.scheduling.SchedulingRepositoryDAO;
import uk.ac.core.workermetrics.data.dao.scheduling.query.ScheduledRepositoryQuery;
import uk.ac.core.workermetrics.data.entity.ScheduledRepository;
import uk.ac.core.workermetrics.data.state.RepositoryPriority;
import uk.ac.core.workermetrics.data.state.ScheduledState;
import uk.ac.core.workermetrics.data.state.WatchedRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucasanastasiou
 */
@Component
public class MySqlSchedulingRepositoryDAO implements SchedulingRepositoryDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final Logger LOG = Logger.getLogger(MySqlSchedulingRepositoryDAO.class.getName());
    
    @Override
    public void insertSchedulingRepository(Integer idRepository, ScheduledState scheduledState, RepositoryPriority repositoryPriority, LocalDateTime lastScheduled) {
        String SQL = "INSERT INTO `core`.`scheduled_repository` (`id_repository`, `scheduled_state`, `repository_priority`, `last_time_scheduled`, `prevent_harvest_until`) "
                + "VALUES (?, ?, ?, ?, DATE_ADD(NOW(), INTERVAL 10 DAY)) ON DUPLICATE KEY UPDATE id_repository=id_repository";
        
        jdbcTemplate.update(SQL, idRepository, scheduledState.name(), repositoryPriority.name(), lastScheduled);
    }
    
    @Override
    public void insertDefaultSchedulingRepositoryEntry(Integer idRepository, ScheduledState scheduledState, RepositoryPriority repositoryPriority) {
        // default is 50 days ago
        LocalDateTime timestamp = LocalDate.now().minusDays(50).atStartOfDay();
        this.insertSchedulingRepository(idRepository, scheduledState, repositoryPriority, timestamp);
    }

    @Override
    public List<ScheduledRepository> getAllOrderedBySchedulingScore() {
        String SQL = new ScheduledRepositoryQuery().sqlWithFullScoring();
        List<ScheduledRepository> scheduledRepositories = jdbcTemplate.query(SQL, new ScheduledRepositoryRowMapper());
        return scheduledRepositories;
    }

    @Override
    public void updateTimeLastScheduledToNow(ScheduledRepository scheduledRepository) {
        String SQL = "UPDATE `scheduled_repository` "
                + "SET `last_time_scheduled` = NOW() "
                + "WHERE `scheduled_repository`.`id_repository` = ?";
        jdbcTemplate.update(SQL, scheduledRepository.getIdRepository());
    }
    
    @Override
    public void updateStatus(ScheduledRepository scheduledRepository, ScheduledState scheduledState) {
        //current state - just to log the state change before and after
        ScheduledState currentState = this.getScheduledRepositoryByRepositoryId(scheduledRepository.getIdRepository()).getScheduledState();
        
        LOG.log(Level.INFO, "Repository :{0} changing STATE from:{1} to {2}", new Object[]{scheduledRepository.getIdRepository(), currentState.name(), scheduledState.name()});
        
        String SQL = "UPDATE `scheduled_repository` "
                + "SET `scheduled_state` = ? "
                + "WHERE `scheduled_repository`.`id_repository` = ?";
        jdbcTemplate.update(SQL, scheduledState.name(), scheduledRepository.getIdRepository());
    }
    
    @Override
    public List<ScheduledRepository> getOngoingOrderedBySchedulingScore() {
        List<ScheduledRepository> all = this.getAllOrderedBySchedulingScore();
        List<ScheduledRepository> result = new ArrayList<>();
        for (ScheduledRepository s : all) {
            if (s.getScheduledState() != ScheduledState.PENDING) {
                result.add(s);
            }
        }
        return result;
    }
    
    @Override
    public List<ScheduledRepository> getNextInLineOrderedBySchedulingScore() {
        //TODO do it with filter of Java 8
        List<ScheduledRepository> all = this.getAllOrderedBySchedulingScore();
        List<ScheduledRepository> result = new ArrayList<>();
        for (ScheduledRepository s : all) {
            if (s.getScheduledState() == ScheduledState.PENDING) {
                result.add(s);
            }
        }
        return result;
    }
    
    @Override
    public List<ScheduledRepository> getNextInLineOrderedBySchedulingScore(int howMany) {
        List<ScheduledRepository> fullList = getNextInLineOrderedBySchedulingScore();
        if (!fullList.isEmpty() && fullList.size() > howMany) {
            return fullList.subList(0, howMany);            
        } else {
            return fullList;
        }
    }
    
    @Override
    public ScheduledRepository getScheduledRepositoryByRepositoryId(Integer repositoryId) {
        String SQL = new ScheduledRepositoryQuery().sqlWithIdRepository();
        ScheduledRepository s = null;
        try {
            s = jdbcTemplate.queryForObject(SQL, new Object[]{repositoryId}, new ScheduledRepositoryRowMapper());
        } catch (EmptyResultDataAccessException e) {
            LOG.warning("No Scheduled repository found for repository " + repositoryId.toString());
        }
        return s;
    }
    
    @Override
    public void updateWatchedStatus(int idRepository, int watchedRepository) {
        String SQL = "UPDATE scheduled_repository SET watched_repository=? WHERE id_repository=?";
        jdbcTemplate.update(SQL, watchedRepository, idRepository);
    }
    
    @Override
    public void updatePreventReharvest(ScheduledRepository scheduledRepository) {
        String SQL = "UPDATE scheduled_repository SET prevent_harvest_until = DATE_ADD(NOW(), INTERVAL 5 DAY) WHERE id_repository = ?";
        jdbcTemplate.update(SQL, scheduledRepository.getIdRepository());
    }
    
    @Override
    public List<Integer> getUnscheduledRepositories() {
        String SQL = "SELECT id_repository FROM repository WHERE disabled=0 AND source='oai' AND id_repository NOT IN (SELECT id_repository FROM scheduled_repository)";
        List<Integer> result = jdbcTemplate.queryForList(SQL, Integer.class);
        return result;
    }

    @Override
    public List<Integer> getPremiumRepositoriesOlderThan3Days() {
        String SQL = "SELECT id_repository FROM scheduled_repository WHERE repository_priority = 'VERY_HIGH' AND last_time_scheduled < DATE_ADD(CURDATE(), INTERVAL -3 DAY);";
        List<Integer> result = jdbcTemplate.queryForList(SQL, Integer.class);
        return result;
    }


    @Override
    public int syncNewRepositories() {
        String SQL = "SELECT id_repository FROM repository WHERE disabled=0 AND source='oai' AND id_repository NOT IN (SELECT id_repository FROM scheduled_repository)";
        return jdbcTemplate.update(""
                + "INSERT INTO scheduled_repository "
                + "(id_repository, scheduled_state, repository_priority, last_time_scheduled, prevent_harvest_until) "
                + "SELECT id_repository, \"NEW\", \"NORMAL\", DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 30 YEAR), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 30 YEAR) "
                + "FROM repository WHERE id_repository NOT IN (SELECT id_repository FROM scheduled_repository);");
    }
    
    class ScheduledRepositoryRowMapper implements RowMapper<ScheduledRepository> {
        
        @Override
        public ScheduledRepository mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScheduledRepository scheduledRepository = new ScheduledRepository();
            scheduledRepository.setIdRepository(rs.getInt("id_repository"));
            scheduledRepository.setLastScheduled(rs.getTimestamp("last_time_scheduled").toLocalDateTime());
            scheduledRepository.setRepositoryPriority(RepositoryPriority.valueOf(rs.getString("repository_priority")));
            
            // Note: We can't easily add a new Schdeuled Status as we have logic like
            //    s.getScheduledState() != ScheduledState.PENDING
            // This means we'll need to review a lot of logic to add a SchduledState.NEW to ensure things do not break
            if ("NEW".equals(rs.getString("scheduled_state"))) {
                scheduledRepository.setScheduledState(ScheduledState.PENDING);                
            } else {
                scheduledRepository.setScheduledState(ScheduledState.valueOf(rs.getString("scheduled_state")));
            }
            scheduledRepository.setWatchedRepository(WatchedRepository.getByFlag(rs.getInt("watched_repository")));
            scheduledRepository.setSchedulingScore(rs.getInt("SCORE"));
            
            return scheduledRepository;
        }
    }
    
    @Override
    public List<ScheduledRepository> getNonPendingRepositories() {
        String SQL = new ScheduledRepositoryQuery().sqlWithPendingRepositories();
        List<ScheduledRepository> scheduledRepositories = jdbcTemplate.query(SQL, new ScheduledRepositoryRowMapper());
        return scheduledRepositories;
    }

    @Override
    public void updatePriority(int repoId, RepositoryPriority priority) {
        String sql = "" +
                "UPDATE scheduled_repository " +
                "SET repository_priority = ? " +
                "WHERE id_repository = ?";
        this.jdbcTemplate.update(sql, priority.name(), repoId);
    }
}
