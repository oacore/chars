/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.affiliation;

import java.util.List;
import uk.ac.core.common.model.GrobidAffiliationAuthor;
import uk.ac.core.common.model.GrobidAffiliationInstitution;
import uk.ac.core.common.model.GrobidAffiliationInstitutionRelAuthor;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
public interface GrobidAffiliationDAO {
    
    public Integer insertGrobidAffiliationInstitution(final GrobidAffiliationInstitution institution);
    
    public Integer insertGrobidAffiliationAuthor(final GrobidAffiliationAuthor author);
    
    public Integer insertGrobidAffiliationInstRelAuthor(final GrobidAffiliationInstitutionRelAuthor relation);
    
    public List<GrobidAffiliationInstitutionRelAuthor> getInstitutionRelationsAuthor(GrobidAffiliationInstitutionRelAuthor relation);
    
    public List<GrobidAffiliationAuthor> getAuthors(GrobidAffiliationAuthor wantedAuthor);
    
}
