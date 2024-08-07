/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.crossref.impl;

import uk.ac.core.database.crossref.CrossrefDoisForDocumentId;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.document.ArticleMetadataDoiDAO;
import uk.ac.core.database.service.document.impl.MySQLArticleMetadataDoiDAO;

/**
 *
 * @author samuel
 */
@Service
public class MySQLCrossrefDOIForDocumentIdDAO implements CrossrefDoisForDocumentId {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySQLArticleMetadataDoiDAO.class);

    JdbcTemplate jdbcTemplate;

    @Autowired
    public MySQLCrossrefDOIForDocumentIdDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(int id_document, String queryString, String doi, String coins, Double score) {

        String sql = "INSERT INTO crossref_dois_for_documents "
                + "(id_document, query_string, doi, coins, score) "
                + "VALUES (?,?,?,?,?) ";

        try {
            this.jdbcTemplate.update(sql, id_document, queryString, doi, coins, score);
        } catch (DataAccessException ex) {
            // if the batch fails, log the info but do nothing
            logger.warn(ex.getMessage(), this.getClass());
        }
    }

    @Override
    public CrossrefCitationForDocumentId getCitationResolution(String query_string) {
        String sql = "SELECT id,id_document, query_string, doi, coins, score "
                + "FROM crossref_dois_for_documents "
                + "WHERE "
                + "query_string = ? ";
        return this.jdbcTemplate.queryForObject(sql, new CrossrefCitationRowMapper(), query_string);

    }

    @Override
    public CrossrefCitationForDocumentId getCitationResolution(Integer id_document) {
        String sql = "SELECT id,id_document, query_string, doi, coins, score "
                + "FROM crossref_dois_for_documents "
                + "WHERE "
                + "id_document = ? ";
        return this.jdbcTemplate.queryForObject(sql, new CrossrefCitationRowMapper(), id_document);
    }

    @Override
    public Boolean isDocumentIdResolved(int documentId) {
        String sql = "SELECT count(id) FROM crossref_dois_for_documents WHERE id_document = ?";
        Integer count = this.jdbcTemplate.queryForObject(sql, Integer.class, documentId);
        return count != null && count > 0;
    }

}
