package uk.ac.core.notifications.database.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import uk.ac.core.notifications.database.DashboardOrganisationDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class MySqlDashboardOrganisationDAO implements DashboardOrganisationDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static int[] INTERNAL_ORGS = {1010, 1122, 1131};
    @Override
    public Map<Integer, List<Integer>> loadRepositoryToOrganisationRelations() {
        String SQL = "SELECT * FROM organisation_repos WHERE org_id NOT IN (?);";
        Map<Integer, List<Integer>> results = this.jdbcTemplate.query(SQL, new Object[]{INTERNAL_ORGS}, new ResultSetExtractor<Map<Integer, List<Integer>>>() {
            @Override
            public Map<Integer, List<Integer>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<Integer, List<Integer>> result = new HashMap<>();
                while(resultSet.next()){
                    Integer organisationId = resultSet.getInt("org_id");
                    Integer repoId = resultSet.getInt("repo_id");
                    List<Integer> organisationForRepos = result.get(repoId);
                    if (organisationForRepos==null){
                        organisationForRepos = new ArrayList<>();
                    }
                    organisationForRepos.add(organisationId);
                    result.put(repoId, organisationForRepos);
                }
                return result;
            }
        });
        return results;
    }
}
