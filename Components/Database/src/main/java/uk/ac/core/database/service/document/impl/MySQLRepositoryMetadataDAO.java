package uk.ac.core.database.service.document.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import uk.ac.core.database.service.document.RepositoryMetadataDAO;

/**
 * @author Giorgio Basile
 * @since 05/04/2017
 */
@Service
public class MySQLRepositoryMetadataDAO implements RepositoryMetadataDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySQLRepositoryMetadataDAO.class);

    /**
     *
     *
     * @param oai
     * @param repositoryId
     * @return
     */
    @Override
    public Integer getIdDocumentByOai(final String oai, final Integer repositoryId) {
        long startTime = System.nanoTime();
        String sql = "SELECT id_document FROM document WHERE oai = ? AND id_repository = ? LIMIT 1";
        Integer i = null;
        try {
            i = this.jdbcTemplate.queryForObject(sql, new Object[]{oai, repositoryId}, Integer.class);
        } catch (DataAccessException ex) {
            // Gobble - expected 0 or 1 result
            i = null;
        }

        return i;
    }

    /**
     *
     *
     * @param url
     * @return
     */
    @Override
    public Integer getIdDocumentByUrl(final String url) {
        long startTime = System.nanoTime();
        String sql = "SELECT id_document FROM document WHERE url_tr = ? LMIIT 1";
        Integer i = null;
        try {
            String urlTemp = (url.length() > 1000) ? url.substring(0, 1000) : url;
            i = this.jdbcTemplate.queryForObject(sql, new Object[]{urlTemp}, Integer.class);
        } catch (DataAccessException ex) {
            // Gobble - expected 0 or 1 result
            i = null;
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("getIdDocumentByUrl took " + duration + " nanoseconds");
        return i;
    }

    @Override
    public void deleteDocument(Integer docId) {
        String[] removeFromTables = {
                "document",
                "document_metadata",
                "document_metadata_extended_attributes",
                "document_raw_metadata",
                "document_tdm_status",
                "document_urls",
                "file_extension"
        };

        for (String table : removeFromTables) {
            // Not strictly good practice, but we trust ourselves... right?
            String sql = "DELETE FROM " + table + " WHERE id_document = ?";
            this.jdbcTemplate.update(sql, new Object[]{docId});
        }

        String customSql = "DELETE FROM mucc_document_metadata where coreId = ?;";
        this.jdbcTemplate.update(customSql, new Object[]{docId});



    }


}
