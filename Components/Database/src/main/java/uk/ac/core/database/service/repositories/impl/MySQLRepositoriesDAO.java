package uk.ac.core.database.service.repositories.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.LegacyRepository;
import uk.ac.core.database.model.mappers.RepositoryRowMapper;
import uk.ac.core.database.service.repositories.RepositoriesDAO;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MySQLRepositoriesDAO implements RepositoriesDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String NEW_REPOSITORIES_QUERY = "SELECT \n"
            + "    id_repository\n"
            + "FROM\n"
            + "    core.repository\n"
            + "WHERE\n"
            + "    disabled = 0\n"
            + "        AND id_repository NOT IN (SELECT DISTINCT\n"
            + "            (id_repository)\n"
            + "        FROM\n"
            + "            core.`update`\n"
            + "        WHERE\n"
            + "            id_repository IS NOT NULL\n"
            + "                AND operation = 'pdfs')\n"
            + "        AND id_repository NOT IN (SELECT \n"
            + "            id_repository\n"
            + "        FROM\n"
            + "            core.repository\n"
            + "        WHERE\n"
            + "            disabled = 0\n"
            + "                AND id_repository NOT IN (SELECT \n"
            + "                    id_repository\n"
            + "                FROM\n"
            + "                    core.`update` u\n"
            + "                WHERE\n"
            + "                    operation = 'metadata_download'\n"
            + "                        AND u.`status` = 'successful'))";

    @Override
    public List<Integer> getNewRepositories() {
        List<Integer> newRepos = jdbcTemplate.queryForList(NEW_REPOSITORIES_QUERY, Integer.class);
        return newRepos;
    }

    @Override
    public boolean repositoryExists(final Integer repositoryId) {
        String CHECK_REPOSITORY_EXISTS_SQL = "SELECT COUNT(*) "
                + "FROM repository "
                + "WHERE id_repository=?";
        try {
            Integer count_repo = jdbcTemplate.queryForObject(CHECK_REPOSITORY_EXISTS_SQL, Integer.class, repositoryId);
            return (count_repo != null && count_repo > 0);
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    @Override
    public boolean isRepositoryEnabled(Integer repositoryId) {
        String CHECK_REPOSITORY_ENABLED = "SELECT disabled "
                + "FROM repository "
                + "WHERE id_repository=?";
        try {
            Integer disabledFlag = jdbcTemplate.queryForObject(CHECK_REPOSITORY_ENABLED, Integer.class, repositoryId);
            return disabledFlag == 0;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    @Override
    public String getRepositoryName(Integer idRepository) {
        String sql = "SELECT name FROM repository WHERE id_repository = ?";
        try {
            String name = jdbcTemplate.queryForObject(sql, String.class, idRepository);
            return name;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public LegacyRepository getRepositoryByOpenDOARId(String openDOARId) {
        LegacyRepository r = null;

        String sql = "SELECT * FROM repository WHERE id_opendoar = ?";
        List<LegacyRepository> repositories = jdbcTemplate.query(sql, new Object[]{openDOARId}, new RepositoryRowMapper());
        if (repositories.size() > 0) {
            return repositories.get(0);
        } else {
            return null;
        }
    }

    @Override
    public LegacyRepository getRepositoryByROARId(Integer id) {
        LegacyRepository r = null;

        String sql = "SELECT * FROM repository WHERE id_roar = ?";
        List<LegacyRepository> repositories = jdbcTemplate.query(sql, new Object[]{id}, new RepositoryRowMapper());
        if (repositories.size() > 0) {
            return repositories.get(0);
        } else {
            return null;
        }
    }
    
    @Override
    public LegacyRepository getRepositoryByBaseId(Integer id) {
        LegacyRepository r = null;

        String sql = "SELECT * FROM repository WHERE id_base = ?";
        List<LegacyRepository> repositories = jdbcTemplate.query(sql, new Object[]{id}, new RepositoryRowMapper());
        if (repositories.size() > 0) {
            return repositories.get(0);
        } else {
            return null;
        }
    }

    @Override
    public LegacyRepository getRepositoryById(String repoId) {
        return this.getRepositoryByIdAndType(repoId, LegacyRepository.class);
    }

    @Override
    public LegacyRepository getRepositoryWithSimilarUri(String similarUri) {
        LegacyRepository r = null;

        String sql = "SELECT * FROM `repository` WHERE uri LIKE ?";

        List<LegacyRepository> repositories = jdbcTemplate.query(sql, new Object[]{"%" + similarUri + "%"}, new RepositoryRowMapper());
        if (repositories.size() > 0) {
            return repositories.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void disableRepository(String id) {
        String sql = "UPDATE repository "
                + "SET disabled = 1 "
                + "WHERE id_repository=?";
        this.jdbcTemplate.update(sql, new Object[]{id});
    }

    @Override
    public <R extends LegacyRepository> R getRepositoryByIdAndType(String repoId, Class<R> repositoryType) {
        String sql = "SELECT * FROM repository WHERE id_repository = ?";

        return this.jdbcTemplate.queryForObject(sql, new Object[]{repoId}, new RepositoryRowMapper<>(repositoryType));
    }

    @Override
    public void updateUri(String id, String newUri) {
        String sql = "UPDATE repository "
                + "SET uri = ? "
                + "WHERE id_repository=?";
        this.jdbcTemplate.update(sql, new Object[]{newUri, id});
    }

    @Override
    public List<Integer> getBigRepositories() {
        String sql = "SELECT id_repository FROM large_repositories;";
        return this.jdbcTemplate.queryForList(sql, Integer.class);
    }

}
