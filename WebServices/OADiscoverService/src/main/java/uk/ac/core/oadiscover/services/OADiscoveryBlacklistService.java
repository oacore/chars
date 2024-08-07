package uk.ac.core.oadiscover.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author lucas
 */
@Service
public class OADiscoveryBlacklistService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Checks if an entry exists in blacklist table for the given DOI,
     * discovered URL and eprints ID
     *
     * @param doi
     * @param url
     * @param epintsId
     * @return
     */
    public boolean isBlackListed(String doi, String url, String epintsId) {
        String SQL = ""
                + "SELECT COUNT(*) "
                + "FROM discovery_repository_blacklist "
                + "WHERE doi=? AND discovered_url=? AND eprints_id=?";

        int count = jdbcTemplate.queryForObject(SQL, new Object[]{doi, url, Integer.parseInt(epintsId)}, Integer.class);
        return count > 0;
    }

    public boolean isBlackListed(String referalUrl, String doi) {
        String SQL = ""
                + "SELECT COUNT(*) "
                + "FROM discovery_repository_blacklist "
                + "WHERE referrer_url=?";

        int count = jdbcTemplate.queryForObject(SQL, new Object[]{referalUrl}, Integer.class);
        if (count == 0) {
            SQL = ""
                    + "SELECT COUNT(*) "
                    + "FROM discovery_repository_blacklist "
                    + "WHERE doi=?";

            count = jdbcTemplate.queryForObject(SQL, new Object[]{doi}, Integer.class);

        }
        return count > 0;
    }
}
