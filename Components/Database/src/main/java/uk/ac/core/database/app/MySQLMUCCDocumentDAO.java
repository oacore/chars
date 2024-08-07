package uk.ac.core.database.app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.mucc.MUCCDocumentDAO;
import uk.ac.core.database.mucc.MUCCDocument;
import java.sql.Timestamp;

/**
 *
 * @author mc26486
 */
@Service
public class MySQLMUCCDocumentDAO implements MUCCDocumentDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySQLMUCCDocumentDAO.class);

    private final List<Object[]> documentBatch = new LinkedList<Object[]>();
    /**
     * Max size of a batch for writing data to database.
     */
    protected static final int BATCH_SIZE = 100;
    private static final String INSERT_MUCC_DOCUMENT = "INSERT INTO mucc_document_metadata "
            + "(coreId,"
            + "doi,"
            + "mag_id,"
            + "issn,"
            + "citationCount,"
            + "estimatedCitationCount,"
            + "deposited,"
            + "published,"
            + "accepted,"
            + "last_update) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?) "
            + "ON DUPLICATE KEY UPDATE "
            + "doi=?,mag_id=?,issn=?,"
            + "citationCount=?,estimatedCitationCount=?,deposited=?,"
            + "published=?,accepted=?,last_update=?";

    private static final String EXIST_DOI = "select coreId as coreId " +
            "FROM mucc_document_metadata " +
            "where doi = ? " +
            "UNION " +
            "SELECT id_document as coreId " +
            "FROM document_metadata  " +
            "where doi = ? " +
            "order by coreId";

    @Override
    public void save(MUCCDocument mUCCDocument) {
        Object[] toParameterArray = this.toParameterArray(mUCCDocument);
        try {
            this.jdbcTemplate.update(INSERT_MUCC_DOCUMENT, toParameterArray);
        } catch (UncategorizedSQLException e) {
            logger.error(Arrays.toString(toParameterArray));
            logger.error("Error in inserting document", e);
            throw e;
        }
    }

    private String toDatabaseString(String item) {
        if (item == null || item.isEmpty()) {
            return null;
        }

        return item.trim();

    }

    private Long toDatabaseTimestamp(Timestamp time) {
        if (time == null) {
            return null;
        } else {
            return time.getTime();
        }
    }

    /**
     * Convert article metadata into a 1D Object array. This method is used for
     * passing article metadata to the QueryRunner.insert method.
     *
     * @param am
     * @param the created date of the object to insert
     * @return
     */
    private Object[] toParameterArray(MUCCDocument document) {
        List<Object> params = new LinkedList<>();

        params.add(Integer.parseInt(document.getCoreId()));
        params.add(this.toDatabaseString(document.getDoi()));
        params.add(this.toDatabaseString(document.getMagId()));
        params.add(this.toDatabaseString(document.getIssn()));
        params.add(document.getCitationCount());
        params.add(document.getEstimatedCitationCount());
        params.add(document.getPublished());
        params.add(document.getAccepted());
        params.add(document.getDeposited());
        params.add(new Timestamp(new Date().getTime()));

        // the insert list needs all parameters duplicated, because the insert query uses
        // ON DUPLICATE KEY UPDATE
        List<Object> all = new LinkedList<>();
        all.addAll(params);  // parameters for INSERT
        all.addAll(params.subList(1, params.size()));  // parameters for ON DUPLICATE KEY UPDATE

        return all.toArray();
    }

    @Override
    public Optional<Long> getCoreId(String doi) {
        Long result = null;
        try {
            result = this.jdbcTemplate.query(EXIST_DOI, new Object[]{doi, doi}, (ResultSet rs) -> {
                if (rs.next()) {
                    return rs.getLong("coreId");
                } else {
                    return null;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return  Optional.ofNullable(result);
    }

    @Override
    public MUCCDocument load(Integer coreId) {
        String sql = "SELECT * FROM mucc_document_metadata WHERE coreId=?;";
        MUCCDocument result;
        try {
            result = this.jdbcTemplate.query(sql, new Object[]{coreId}, (ResultSet rs) -> {
                if (rs.next()) {
                    MUCCDocument mUCCDocument = new MUCCDocument();
                    mUCCDocument.setAccepted(rs.getTimestamp("accepted"));
                    mUCCDocument.setCitationCount(rs.getInt("citationCount"));
                    mUCCDocument.setCoreId(rs.getString("coreId"));
                    mUCCDocument.setDeposited(rs.getTimestamp("deposited"));
                    mUCCDocument.setDoi(rs.getString("doi"));
                    mUCCDocument.setEstimatedCitationCount(rs.getInt("estimatedCitationCount"));
                    mUCCDocument.setIssn(rs.getString("issn"));
                    mUCCDocument.setMagId(rs.getString("mag_id"));
                    mUCCDocument.setPublished(rs.getTimestamp("published"));
                    return mUCCDocument;
                } else {
                    return null;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return result;
    }

}
