package uk.ac.core.database.service.citation;

import java.util.List;
import uk.ac.core.common.model.legacy.Citation;

/**
 *
 * @author lucasanastasiou
 */
public interface CitationDAO {

    public List<Citation> getCitations(Integer articleId);
    
}
