package uk.ac.core.extractmetadata.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RepositoryExcludedSetsRepositoryImpl implements RepositoryExcludedSetsRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public Set<String> getSetSpecsForExclude(Long repositoryId) {
        String SQL = "SELECT set_spec FROM repository_excluded_sets WHERE id_repository = ? OR id_repository IS NULL";

        List<String> list;
        try {
            list = jdbcTemplate.queryForList(SQL, new Object[] {repositoryId}, String.class);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            list = new ArrayList<>();
        }

        return new HashSet<>(list);
    }
}
