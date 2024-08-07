/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.grobid.processor.store.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.GrobidAffiliationAuthor;
import uk.ac.core.common.model.GrobidAffiliationHeaderAuthor;
import uk.ac.core.common.model.GrobidAffiliationInstitution;
import uk.ac.core.common.model.GrobidAffiliationInstitutionRelAuthor;
import uk.ac.core.common.model.GrobidCitation;
import uk.ac.core.common.model.GrobidCitationAuthor;
import uk.ac.core.common.model.GrobidCitationBiblScope;
import uk.ac.core.common.model.GrobidCitationRelAuthor;
import uk.ac.core.database.service.affiliation.GrobidAffiliationDAO;
import uk.ac.core.database.service.citation.GrobidCitationDAO;
import uk.ac.core.grobid.processor.store.StoreSeparateAddData;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
@Service
public class StoreSeparateAddDataImpl implements StoreSeparateAddData{
    
    @Autowired
    GrobidCitationDAO grobidCitationDAO;
    
    @Autowired
    GrobidAffiliationDAO affiliationDAO;
        
    @Override
    public void storeCitationSeparateAddData(GrobidCitation grobidCitation, Integer citationKey){
        for(GrobidCitationAuthor grobidCitationAuthor : grobidCitation.getGrobidAuthors()){
            List<GrobidCitationAuthor> existingAuthors = grobidCitationDAO.getAuthors(grobidCitationAuthor);
            
            GrobidCitationRelAuthor relation = new GrobidCitationRelAuthor();
            relation.setAuthorId(grobidCitationAuthor.getAuthorId());
            relation.setCitationId(citationKey);
                
            if(existingAuthors.isEmpty()){
                Integer newAuthorId = grobidCitationDAO.insertGrobidCitationAuthor(grobidCitationAuthor);
                if(newAuthorId == null) continue;
                relation.setAuthorId(newAuthorId);
                grobidCitationDAO.insertGrobidCitationRelAuthor(relation);
            }else{
                relation.setAuthorId(existingAuthors.get(existingAuthors.size()-1).getAuthorId());
                if(grobidCitationDAO.getCitationAuthorRelations(relation).isEmpty()){
                    grobidCitationDAO.insertGrobidCitationRelAuthor(relation);
                }
                
            }
        }
        for(GrobidCitationBiblScope grobidCitationBiblScope : grobidCitation.getGrobidCitationImprint().getGrobidBiblScopes()){
            grobidCitationDAO.insertGrobidCitationBiblScope(grobidCitationBiblScope, citationKey);
        }
    }
    
    @Override
    public void storeAffiliationSeparateAddData(GrobidAffiliationHeaderAuthor headerAuthor, Integer citationKey){
        for(GrobidAffiliationInstitution institution : headerAuthor.getGrobidAffiliationInstitutions()){
            Integer institutionKey = affiliationDAO.insertGrobidAffiliationInstitution(institution);
            if(institutionKey != null){
                GrobidAffiliationInstitutionRelAuthor relation = new GrobidAffiliationInstitutionRelAuthor();
                relation.setInstitutionId(institution.getInstitutionId());
                
                for(GrobidAffiliationAuthor author : headerAuthor.getGrobidAffiliationAuthors()){
                    List<GrobidAffiliationAuthor> authorExistList = affiliationDAO.getAuthors(author);
                    if(authorExistList == null || authorExistList.isEmpty()){
                        Integer authorKey = affiliationDAO.insertGrobidAffiliationAuthor(author);
                        relation.setAuthorId(authorKey);
                        affiliationDAO.insertGrobidAffiliationInstRelAuthor(relation);
                    }else{
                        GrobidAffiliationAuthor existingAuthor = authorExistList.get(authorExistList.size()-1);
                        relation.setAuthorId(existingAuthor.getAuthorId());
                        
                        List<GrobidAffiliationInstitutionRelAuthor> existingRelation = affiliationDAO.getInstitutionRelationsAuthor(relation);
                        if( existingRelation == null || existingRelation.isEmpty()){
                            affiliationDAO.insertGrobidAffiliationInstRelAuthor(relation);
                        }
                    }
                }
            }
        }
    }

}
