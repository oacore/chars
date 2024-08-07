package uk.ac.core.database.service.documetduplicates.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.documetduplicates.DocumentDuplicateDao;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MySqlDocumentDuplicateDao implements DocumentDuplicateDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public HashSet<Integer> findAllDuplicatesByDoi(String doi){
        //it might return non existing documents
        String query =
                "SELECT dm.id_document FROM document_metadata dm \n" +
                        "WHERE dm.doi = ?\n" +
                        "UNION\n" +
                        "SELECT mdm.coreId AS id_document FROM mucc_document_metadata mdm \n" +
                        "WHERE mdm.doi = ?";
        return new HashSet<>(jdbcTemplate.queryForList(query, Integer.class, doi, doi));
    }

    @Override
    public String getDoiByDocumentId(Integer documentId){
        String query = "SELECT doi FROM mucc_document_metadata WHERE coreId = ? and doi is not null " +
                "UNION " +
                "SELECT doi FROM document_metadata WHERE id_document = ? and doi is not null ";
        List<String> dois = jdbcTemplate.queryForList(query, String.class, documentId, documentId);
        if(dois.size() > 1) {
            return dois.get(1);
        } else if (dois.size() == 1) {
            return dois.get(0);
        } else {
            return null;
        }
    }

    @Override
    public HashSet<Integer> getIdDocumentsByDOI(String doi, Integer repositoryId) {
        String query =
                "SELECT d.id_document " +
                "FROM document_metadata dm " +
                "INNER JOIN document d ON d.id_document = dm.id_document " +
                "WHERE dm.doi = ? and d.id_repository = ? and d.deleted = 0 " +
                "UNION " +
                "SELECT mdm.coreId AS id_document " +
                "FROM mucc_document_metadata  mdm " +
                "INNER JOIN document d ON d.id_document = mdm.coreId " +
                "WHERE doi = ? and d.id_repository = ? and d.deleted = 0";

        return new HashSet<>(jdbcTemplate.queryForList(query, Integer.class, doi, repositoryId, doi, repositoryId));
    }

    @Override
    public void insertDocumentToExcludingTable(Integer excludedDocumentId, List<Integer> documentIds) {
        String insertValues = documentIds.stream().map(e -> "(" + excludedDocumentId + ", " + e + ")")
                .collect(Collectors.joining(", "));
        String query = "INSERT works_excluding(excluded_document_id, document_id) VALUES " + insertValues;
        jdbcTemplate.update(query);

    }

    @Override
    public boolean isDocumentExcluded(Integer documentId) {
        String query = "SELECT excluded_document_id from works_excluding where excluded_document_id = ?;";
        return !jdbcTemplate.queryForList(query, Integer.class, documentId).isEmpty();
    }

    @Override
    public List<Integer> getExclusionList(Integer documentId) {
        String query = "SELECT excluded_document_id as id from works_excluding we where document_id = ? " +
                "UNION " +
                "SELECT document_id as id from works_excluding we where excluded_document_id = ?;";
        return jdbcTemplate.queryForList(query, Integer.class, documentId, documentId);
    }

}
