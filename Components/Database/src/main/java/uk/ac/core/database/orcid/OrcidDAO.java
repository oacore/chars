package uk.ac.core.database.orcid;

import java.util.List;

/**
 *
 * @author lucas
 */
public interface OrcidDAO {

    public List<String> getOrcidsOfAuthorsOfArticle(Integer articleId);
    
}
