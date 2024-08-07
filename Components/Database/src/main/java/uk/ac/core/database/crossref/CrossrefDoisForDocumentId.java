
package uk.ac.core.database.crossref;

import uk.ac.core.database.crossref.impl.CrossrefCitationForDocumentId;

/**
 *
 * @author samuel
 */
public interface CrossrefDoisForDocumentId {

    public CrossrefCitationForDocumentId getCitationResolution(String query_string);

    public CrossrefCitationForDocumentId getCitationResolution(Integer id_document);
    
    public void insert(int id_document, String queryString, String doi, String coins, Double score);
    
    public Boolean isDocumentIdResolved(int documentId);

}
