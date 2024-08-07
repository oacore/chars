/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.repositories.impl;

import java.math.BigDecimal;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.repositories.RepositoriesFreshnessDAO;

/**
 * BAD REALLY REALLY REALLY REALLY BAS DON'T DO THIS AT HOME NEVER
 *
 * @author mc26486
 */
@Service
public class LegacyMySqlRepositoriesFreshnessDAO implements RepositoriesFreshnessDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LegacyMySqlRepositoriesFreshnessDAO.class);

    private static LegacyMySqlRepositoriesFreshnessDAO instance;

    private final static String INSERT_FRESHNESS = "INSERT INTO core.repository_freshness_metrics "
            + "(id_repository, total_documents_updated, document_freshness_sum, document_freshness_avg)"
            + "VALUES(?,?,?,?)";

    private final static String REPOFRESHNESS = "SELECT id_repository, "
            + "COUNT(id_document) as count_docs, SUM(diff) as sum_days, AVG(diff) as avg_days\n"
            + "FROM(\n"
            + "SELECT \n"
            + "    d.id_repository, d.id_document,metadata_added, metadata_updated,dm.datestamp,  DATEDIFF(metadata_updated, dm.datestamp) AS diff\n"
            + "FROM\n"
            + "    core.document d\n"
            + "    JOIN core.document_metadata dm ON\n"
            + "    d.id_document=dm.id_document\n"
            + "WHERE\n"
            + "    id_repository = ?\n"
            + "        AND id_update IN (SELECT \n"
            + "            MAX(id_update)\n"
            + "        FROM\n"
            + "            core.document\n"
            + "        WHERE\n"
            + "            id_repository = ?)\n"
            + "AND dm.datestamp > (NOW()-INTERVAL ? MONTH)\n"
            + ") k";
    private static final Integer FRESHNESSMONTHSLIMIT = 18;

    @PostConstruct
    public void injectBean() {
        /**
         * BAD REALLY REALLY REALLY REALLY BAS DON'T DO THIS AT HOME NEVER
         *
         * @author mc26486
         */
        LegacyMySqlRepositoriesFreshnessDAO.instance = this;
    }

    @Override
    public void saveForRepository(Integer repositoryId) {
        try {
            Map<String, Object> row = this.jdbcTemplate.queryForMap(REPOFRESHNESS, new Object[]{repositoryId, repositoryId, FRESHNESSMONTHSLIMIT});
            Long count = (Long) row.get("count_docs");
            BigDecimal sum = (BigDecimal) row.get("sum_days");
            BigDecimal avg = (BigDecimal) row.get("avg_days");
            this.jdbcTemplate.update(INSERT_FRESHNESS, new Object[]{repositoryId, count, sum, avg});
        } catch (Exception e) {
            logger.error("Failed to run the freshness save query for repo:" + repositoryId, e);
        }
    }

    private LegacyMySqlRepositoriesFreshnessDAO() {

    }

    public static LegacyMySqlRepositoriesFreshnessDAO getInstance() {
        /**
         * BAD REALLY REALLY REALLY REALLY BAS DON'T DO THIS AT HOME NEVER
         *
         * @author mc26486
         */
        return LegacyMySqlRepositoriesFreshnessDAO.instance;
    }

}
