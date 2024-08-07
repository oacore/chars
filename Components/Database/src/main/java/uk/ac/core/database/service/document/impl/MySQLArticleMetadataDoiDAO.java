/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.document.impl;

import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.document.ArticleMetadataDoiDAO;

/**
 *
 * @author samuel
 */
@Service
public class MySQLArticleMetadataDoiDAO implements ArticleMetadataDoiDAO {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySQLArticleMetadataDoiDAO.class);
    
    JdbcTemplate jdbcTemplate;

    @Autowired
    public MySQLArticleMetadataDoiDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public void updateDOI(int ID, String DOI, Source source) {

        String sql = "UPDATE document_metadata "
                + "SET "
                + "doi=?,doi_from_crossref=?,doi_datetime_resolved=NOW()"
                + "WHERE "
                + "id_document = ?";

        try {
            String doiFromCrossref = (Source.CROSSREF.equals(source)) ? "1" : "0";
            this.jdbcTemplate.update(sql, DOI, doiFromCrossref, ID);
        } catch (DataAccessException ex) {
            // if the batch fails, log the info but do nothing
            logger.warn(ex.getMessage(), this.getClass());
        }
    }

    @Override
    public String getDOI(int id) {
        String sql = "SELECT doi FROM document_metadata WHERE id_document = ?";
        List<String> doi = this.jdbcTemplate.queryForList(sql, String.class);
        if (doi.isEmpty()) {
            return null;
        } else {
            return doi.get(0);
        }
    }

}
