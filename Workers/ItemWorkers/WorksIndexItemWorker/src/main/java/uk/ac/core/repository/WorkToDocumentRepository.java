package uk.ac.core.repository;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author MTarasyuk
 */

@Repository
public class WorkToDocumentRepository {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WorkToDocumentRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @PostConstruct
    public void init() {
        namedJdbcTemplate =
                new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
    }

    public Integer insertNewWorkToDocument(Integer documentId, String explanation, Double confidence){
        String query = "INSERT INTO work_to_document (document_id, explanation, confidence) VALUES (?,?,?);";
        jdbcTemplate.update(query, documentId, explanation,confidence);
        return findWorkIdByDocuments(new HashSet<>(Collections.singletonList(documentId))).get(0);
    }

    public void insertOrUpdateWorkToDocument(Integer documentId, Integer workId, String explanation, Double confidence){
        String query = "INSERT INTO work_to_document (document_id, work_id, explanation, confidence) VALUES (?,?,?,?)\n" +
                "ON DUPLICATE KEY UPDATE confidence = ?, explanation = ?;";
        jdbcTemplate.update(query, documentId, workId, explanation, confidence, confidence, explanation);
    }

    public void updateRootWorkId(List<Integer> disabledWorksIds, Integer rightWorkId){
        String query = "UPDATE work_to_document SET root_work_id = :rightWorkId WHERE work_id in (:ids);";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", disabledWorksIds);
        parameters.addValue("rightWorkId", rightWorkId);
        namedJdbcTemplate.update(query, parameters);
    }

    public List<Integer> findWorkIdByDocuments(Collection<Integer> documentsId){
        if (documentsId == null || documentsId.isEmpty()) {
            logger.warn("Can't search for workId based on empty documents list");
            return Collections.emptyList();
        }

        String query = "SELECT DISTINCT work_id FROM work_to_document WHERE document_id in (:ids) " +
                "and root_work_id is null ORDER  by work_id asc;";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", documentsId);

        List<Integer> workIds = namedJdbcTemplate.query(query, parameters, (rs, i) -> rs.getInt("work_id"));

        if(!workIds.isEmpty()) {
            return workIds;
        } else {
            return Collections.emptyList();
        }
    }

    public List<Integer> getSavedDuplicatesByWorkId(Integer workId){
        String query = "SELECT document_id FROM work_to_document wtd where work_id = ?;";
        return jdbcTemplate.queryForList(query, Integer.class, workId);
    }

    public void deleteDocumentFromWorkToDocument(Integer documentId){
        String query = "DELETE from work_to_document where document_id = ?;";
        jdbcTemplate.update(query, documentId);
    }
}
