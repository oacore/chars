package uk.ac.core.database.service.document.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.ac.core.database.service.document.FieldStudyDAO;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class FieldStudyDAOImpl implements FieldStudyDAO {

    private final JdbcTemplate jdbcTemplate;

    public FieldStudyDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<String> findFirstNormalizedNameByIdIn(List<Integer> docIds) {
        String inSql = String.join(",", Collections.nCopies(docIds.size(), "?"));

//        https://stackoverflow.com/questions/10606229/jdbctemplate-query-for-string-emptyresultdataaccessexception-incorrect-result
        return jdbcTemplate.queryForList(
                String.format("SELECT fos_normalizedname FROM document_field_of_study WHERE coreid in (%s) LIMIT 1", inSql),
                docIds.toArray(),
                String.class)
                .stream()
                .findFirst();
    }
}