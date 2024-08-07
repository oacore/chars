package uk.ac.core.database.service.publishername.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.model.PublisherName;
import uk.ac.core.database.service.publishername.PublisherNameDAO;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MySQLPublisherNameDAO implements PublisherNameDAO {
    private static final String CUSTOM_DELIMITER = "<del>";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void save(PublisherName entity) {
        final String SQL = "INSERT INTO publisher_name (doi_prefix, canonical_name, names) " +
                "VALUES (?, ?, ?)";

        jdbcTemplate.update(
                SQL, entity.getDoiPrefix(), entity.getPrimaryName(),
                String.join(CUSTOM_DELIMITER, entity.getNames()));
    }

    @Override
    public void saveAll(List<PublisherName> publisherNames) {
        final String SQL = "INSERT INTO publisher_name (doi_prefix, canonical_name, names) " +
                "VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(SQL, publisherNames, 200,
                (ps, p) -> {
                    ps.setString(1, p.getDoiPrefix());
                    ps.setString(2, p.getPrimaryName());
                    ps.setString(3, String.join(CUSTOM_DELIMITER, p.getNames()));
                });
    }

    /**
     * Returns a publisher by DOI prefix.
     * <p>
     * Where there multiple entries, the lowest/oldest ID is returned
     *
     * @param prefix of format 10.[anything]
     * @return
     */
    @Override
    public Optional<PublisherName> findByPrefix(String prefix) {
        final String SQL = "select * from publisher_name where doi_prefix = ? ORDER BY id ASC LIMIT 1";

        return jdbcTemplate.query(SQL, new Object[]{prefix}, (rs, rowNum) -> new PublisherName(rs.getLong("id"),
                rs.getString("doi_prefix"), rs.getString("canonical_name"),
                Arrays.asList(rs.getString("names").split(CUSTOM_DELIMITER)))).stream().findFirst();
    }

    @Override
    public Optional<PublisherName> findByName(String name) {
        final String SQL = "select * from publisher_name where `names` like ? ORDER BY id ASC LIMIT 1";

        List<PublisherName> exactMatchingNameList = jdbcTemplate.query(
                SQL,
                (rs, i) -> new PublisherName(
                        rs.getString("doi_prefix"),
                        rs.getString("canonical_name"),
                        Arrays.asList(rs.getString("names").split(CUSTOM_DELIMITER))),
                name);

        List<PublisherName> nameLikeMatchingList = jdbcTemplate.query(SQL, (rs, i) ->
                        new PublisherName(rs.getLong("id"),
                                rs.getString("doi_prefix"), rs.getString("canonical_name"),
                                Arrays.asList(rs.getString("names").split(CUSTOM_DELIMITER))),
                "%" + name + "%");

        if (exactMatchingNameList.size() == 1) {
            return Optional.of(exactMatchingNameList.get(0));
        }

        if (nameLikeMatchingList.isEmpty()) {
            return Optional.empty();
        } else if (nameLikeMatchingList.size() == 1) {
            return Optional.of(nameLikeMatchingList.get(0));
        } else {
            return nameLikeMatchingList.stream()
                    .filter(n -> n.getNames().contains(name))
                    .findFirst();
        }
    }

    @Override
    public List<PublisherName> findAll() {
        final String SQL = "select * from publisher_name";

        return jdbcTemplate.query(SQL, (rs, i) ->
                new PublisherName(rs.getLong("id"),
                        rs.getString("doi_prefix"),
                        rs.getString("canonical_name"),
                        Arrays.asList(rs.getString("names").split(CUSTOM_DELIMITER))));
    }
}
