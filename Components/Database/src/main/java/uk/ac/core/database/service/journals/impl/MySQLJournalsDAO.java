package uk.ac.core.database.service.journals.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.model.JournalISSN;
import uk.ac.core.database.service.journals.JournalsDAO;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MySQLJournalsDAO implements JournalsDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;//"%issn%"

    private static final String CUSTOM_DELIMITER = ",";

    @Override
    public String getJournalTitleByIdentifier(String journalIdentifier) {
        String SQL = "SELECT title FROM journals where identifiers like ?";
        String title = null;
        try {
            title = jdbcTemplate.queryForObject(SQL, String.class, journalIdentifier);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }
        return title;
    }

    @Override
    public void saveAll(List<JournalISSN> journalISSNS) {
        final String SQL = "INSERT INTO journals_issn (title, subject, issn) " +
                "VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(SQL, journalISSNS, 200,
                (ps, issn) -> {
                    ps.setString(1, issn.getTitle());
                    ps.setString(2, issn.getSubject());
                    ps.setString(3, String.join(CUSTOM_DELIMITER, issn.getIssnList()));
                });
    }

    @Override
    public JournalISSN findByIdentifier(String identifier) {
        String identifierToSearch = identifier.replaceAll("issn:", "");
        final String SQL = "select * from journals_issn where issn like ?";

        List<JournalISSN> issnList = jdbcTemplate.query(SQL,
                (rs, i) ->
                        new JournalISSN(rs.getLong("id"),
                                rs.getString("title"), rs.getString("subject"),
                                Arrays.asList(rs.getString("issn").split(CUSTOM_DELIMITER))),
                "%" + identifierToSearch + "%");

        if(issnList.isEmpty()) {
            return null;
        } else if (issnList.size() == 1) {
            return issnList.get(0);
        } else {
            return issnList.stream()
                    .filter(i -> i.getIssnList().contains(identifierToSearch))
                    .findFirst().orElse(null);
        }
    }

    @Override
    public List<JournalISSN> findAllIssns() {
        final String SQL = "select * from journals_issn";

        return jdbcTemplate.query(SQL,
                (rs, i) -> new JournalISSN(rs.getLong("id"),
                        rs.getString("title"), rs.getString("subject"),
                        Arrays.asList(rs.getString("issn").split(CUSTOM_DELIMITER))));
    }
}
