package uk.ac.core.database.service.document.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.document.DocumentTypeDAO;

/**
 *
 * @author aristotelischaralampous
 */
@Service
public class MySQLDocumentTypeDAO implements DocumentTypeDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    Logger logger = Logger.getLogger(MySQLDocumentDAO.class);

    @Override
    public String getDocumentType(Integer documentId) {
        String SQL = "SELECT label FROM Article_Type_Enrichments_V1_Random_Forest WHERE article_id=?";
        List<String> documentTypes = this.jdbcTemplate.query(SQL, new Object[]{documentId}, new RowMapper() {

            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }

        });

        if (documentTypes.isEmpty()) {
            return null;
        } else if (documentTypes.size() == 1) { // list contains exactly 1 element as expected
            return documentTypes.get(0);
        } else {  
            // do something different perhaps ?
            return documentTypes.get(0);
        }

    }

    @Override
    public Double getDocumentTypeConfidence(Integer documentId) {
                String SQL = "SELECT confidence FROM Article_Type_Enrichments_V1_Random_Forest WHERE article_id=?";
        List<Double> documentTypeConfidences = this.jdbcTemplate.query(SQL, new Object[]{documentId}, new RowMapper(){
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getDouble(1);
            }
        });
        
        if (documentTypeConfidences.isEmpty()){
            return null;
        }else {
            return documentTypeConfidences.get(0);
        }
    }

    @Override
    public Pair<String, Double> getDocumentTypeInfo(Integer documentId) {
        String SQL = "SELECT label, confidence FROM Article_Type_Enrichments_V1_Random_Forest WHERE article_id=?";
        List<Pair<String, Double>> documentTypes = this.jdbcTemplate.query(SQL, new Object[]{documentId},
                (rs, rowNum) -> Pair.of(rs.getString(1), rs.getDouble(2)));

        if (documentTypes.isEmpty()) {
            return null;
        } else if (documentTypes.size() == 1) { // list contains exactly 1 element as expected
            return documentTypes.get(0);
        } else {
            // do something different perhaps ?
            return documentTypes.get(0);
        }
    }
}
