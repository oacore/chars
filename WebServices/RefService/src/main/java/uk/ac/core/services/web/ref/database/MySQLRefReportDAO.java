package uk.ac.core.services.web.ref.database;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.core.common.util.TextToDateTime;
import uk.ac.core.services.web.ref.model.FullTextReportDTO;
import uk.ac.core.services.web.ref.model.RefReportDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.util.List;


@Service
@Transactional
public class MySQLRefReportDAO implements RefReportDAO {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RefReportDAO.class);


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<RefReportDTO> getReportData(String doi) {
        String sql = "SELECT dm.id_document, dm.doi, d.id_repository, r.name, dm.`date`, dmea.repository_metadata_public_release_date\n" +
                "FROM document_metadata dm \n" +
                "INNER JOIN document d on dm.id_document = d.id_document \n" +
                "LEFT JOIN document_metadata_extended_attributes dmea ON dm.id_document = dmea.id_document \n" +
                "INNER JOIN repository r on d.id_repository = r.id_repository\n" +
                "where dm.doi = ?";

        List<RefReportDTO> refReportDTO = null;

        try {
           refReportDTO = jdbcTemplate.query(sql, new RefReportDTOMapper(), doi);
        } catch (QueryTimeoutException e){
            logger.info("Query was cancelled because of timeout");
        }

        return refReportDTO;
    }

    @Override
    public List<RefReportDTO> getMuccReportData(String doi) {
        String sql = "SELECT mdm.coreId, mdm.doi, d.id_repository, r.name, mdm.published, dmea.repository_metadata_public_release_date\n" +
                "from mucc_document_metadata mdm\n" +
                "INNER JOIN document d on mdm.coreId = d.id_document\n" +
                "LEFT JOIN document_metadata_extended_attributes dmea ON mdm.coreId = dmea.id_document \n" +
                "INNER JOIN repository r on d.id_repository = r.id_repository\n" +
                "where mdm.doi = ?";

        List<RefReportDTO> refReportDTO = null;

        try {
          refReportDTO = jdbcTemplate.query(sql, new RefMuccReportDTOMapper(), doi);
        } catch (QueryTimeoutException e){
            logger.info("Query was cancelled because of timeout");
        }

        return refReportDTO;
    }

    @Override
    public List<FullTextReportDTO> getFullTextReportData(String doi) {
        String sql = "SELECT dm.id_document, dm.doi, d.id_repository, r.name\n" +
                "FROM document_metadata dm \n" +
                "INNER JOIN document d on dm.id_document = d.id_document \n" +
                "INNER JOIN repository r on d.id_repository = r.id_repository\n" +
                "where dm.doi = ? and d.deleted = 0";

        List<FullTextReportDTO> fullTextReportDTOS = null;

        try {
            fullTextReportDTOS = jdbcTemplate.query(sql, new FullTextReportDTOMapper(), doi);
        } catch (QueryTimeoutException e){
            logger.info("Query was cancelled because of timeout");
        }

        return fullTextReportDTOS;
    }

    private static final class RefReportDTOMapper implements RowMapper<RefReportDTO> {

        @Override
        public RefReportDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            RefReportDTO refReportDTO = new RefReportDTO();
            refReportDTO.setIdDocument(rs.getInt("id_document"));
            refReportDTO.setDoi(rs.getString("doi"));
            refReportDTO.setIdRepository(rs.getInt("id_repository"));
            refReportDTO.setRepoName(rs.getString("name"));
            String dateString = rs.getString("date");
            if (dateString != null) {
                try {
                    refReportDTO.setPublicationDate(Timestamp.valueOf(new TextToDateTime(dateString).asLocalDateTime()));
                } catch (DateTimeException e) {
                    logger.warn("Unable to parse date from {}", dateString);
                }
            }
            refReportDTO.setPublicReleaseDate(rs.getTimestamp("repository_metadata_public_release_date"));
            return refReportDTO;
        }
    }

    private static final class RefMuccReportDTOMapper implements RowMapper<RefReportDTO> {

        @Override
        public RefReportDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            RefReportDTO refReportDTO = new RefReportDTO();
            refReportDTO.setIdDocument(rs.getInt("coreId"));
            refReportDTO.setDoi(rs.getString("doi"));
            refReportDTO.setIdRepository(rs.getInt("id_repository"));
            refReportDTO.setRepoName(rs.getString("name"));
            refReportDTO.setPublicationDateCrossref(rs.getTimestamp("published"));
            refReportDTO.setPublicReleaseDate(rs.getTimestamp("repository_metadata_public_release_date"));
            return refReportDTO;
        }
    }

    private static final class FullTextReportDTOMapper implements RowMapper<FullTextReportDTO>{

        @Override
        public FullTextReportDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            FullTextReportDTO fullTextReportDTO = new FullTextReportDTO();
            fullTextReportDTO.setIdDocument(rs.getInt("id_document"));
            fullTextReportDTO.setDoi(rs.getString("doi"));
            fullTextReportDTO.setIdRepository(rs.getInt("id_repository"));
            fullTextReportDTO.setRepoName(rs.getString("name").replace(",", " "));
            return fullTextReportDTO;
        }
    }
}
