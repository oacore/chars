package uk.ac.core.database.orcid;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author lucas
 */
@Service
public class MySQLOrcidDAO implements OrcidDAO {

    @Autowired 
    JdbcTemplate jdbcTemplate;
    
    @Override
    public List<String> getOrcidsOfAuthorsOfArticle(Integer articleId) {
    
        String SQL = "SELECT orcidId "
                + "FROM core_article_orcids "
                + "WHERE coreId = ?";
        
        List<String> orcidIds = jdbcTemplate.queryForList(SQL, new Object[]{articleId}, String.class);
        return orcidIds;
    }
    
}
