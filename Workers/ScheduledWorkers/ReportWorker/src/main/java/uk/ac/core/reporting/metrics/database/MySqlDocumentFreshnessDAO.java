/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.reporting.metrics.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.OptionalDouble;

/**
 * @author mc26486
 */
@Service
public class MySqlDocumentFreshnessDAO implements DocumentFreshnessDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlDocumentFreshnessDAO.class);
    public static final String EMPTY_RESULT_MSG = "Empty result after getting the document freshness.";

    Integer WINDOW_SIZE = 7;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final String DOCUMENT_FRESHNESS = "SELECT \n"
            + "    AVG(document_freshness)\n"
            + "FROM\n"
            + "    (SELECT \n"
            + "        DATEDIFF(document.metadata_added, document_metadata.datestamp) AS document_freshness\n"
            + "    FROM\n"
            + "        document\n"
            + "    JOIN document_metadata ON document_metadata.id_document = document.id_document\n"
            + "    WHERE\n"
            + "        document.id_document >= ?\n"
            + "            AND document_metadata.datestamp IS NOT NULL\n"
            + "            AND document_metadata.datestamp > (NOW() - INTERVAL 365 DAY)) r;";

    @Override
    public OptionalDouble getDocumentFreshness() {
        Integer newestDocumentOfTheWindow = getFirstDocumentInTheWindow();
        try {
            Double documentFreshness = this.jdbcTemplate.queryForObject(DOCUMENT_FRESHNESS, new Object[]{newestDocumentOfTheWindow}, Double.class);
            if (documentFreshness != null) {
                return OptionalDouble.of(documentFreshness);
            }
        } catch (EmptyResultDataAccessException ignored) {
            LOGGER.debug(EMPTY_RESULT_MSG);
            return OptionalDouble.empty();
        }
        return OptionalDouble.empty();
    }

    private static final String FIRST_DOC_IN_WINDOW = "SELECT \n"
            + "    MIN(id_document)\n"
            + "FROM\n"
            + "    (SELECT \n"
            + "        id_document, metadata_added\n"
            + "    FROM\n"
            + "        document\n"
            + "    WHERE\n"
            + "        metadata_added >= (NOW() - INTERVAL ? DAY)) w;";

    private Integer getFirstDocumentInTheWindow() {
        Integer firstDoc = this.jdbcTemplate.queryForObject(FIRST_DOC_IN_WINDOW, new Object[]{WINDOW_SIZE}, Integer.class);
        return firstDoc;
    }

}
