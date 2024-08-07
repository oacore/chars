/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.citation;

import java.util.List;
import uk.ac.core.common.model.GrobidCitation;
import uk.ac.core.common.model.GrobidCitationAuthor;
import uk.ac.core.common.model.GrobidCitationBiblScope;
import uk.ac.core.common.model.GrobidCitationRelAuthor;
import uk.ac.core.common.model.legacy.Citation;

/**
 *
 * @author vb4826
 */
public interface GrobidCitationDAO {
    
    public List<GrobidCitationAuthor> getAuthors(GrobidCitationAuthor wantedAuthor);
    
    public List<GrobidCitationRelAuthor> getCitationAuthorRelations(GrobidCitationRelAuthor relation);
    
    public Integer insertGrobidCitation(GrobidCitation c);
    
    public Integer insertGrobidCitationRelAuthor(GrobidCitationRelAuthor relation);
    
    public Integer insertGrobidCitationAuthor(GrobidCitationAuthor cAuthor);
    
    public void insertGrobidCitationBiblScope(GrobidCitationBiblScope cBiblScope, Integer citationId);
    
    public List<Citation> getCitationsByDOI(String doi);
}
