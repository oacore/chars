package uk.ac.core.enrichments;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import uk.ac.core.common.model.legacy.ArticleMetadata;

/**
 *
 * @author samuel
 */
public class CleanAuthorName {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CleanAuthorName.class);

    ArticleMetadata articleMetadata;

    public CleanAuthorName(ArticleMetadata articleMetadata) {
        this.articleMetadata = articleMetadata;
    }
    
    public List<String> cleanAuthors() {
        List<String> authors = this.articleMetadata.getAuthors();
        
        List<String> newAuthors = new ArrayList<>(authors.size());
        for (String author : authors) {
            
            String[] newAuthor = author.split(";");
            if (newAuthor[0] != null) {                
                //logger.info("Author Name modified on Index. Old Name: " + author + " cleaned name:" + newAuthor[0]);
                newAuthors.add(newAuthor[0]);     
            }
        }
        
        return newAuthors;
    }
    
}
