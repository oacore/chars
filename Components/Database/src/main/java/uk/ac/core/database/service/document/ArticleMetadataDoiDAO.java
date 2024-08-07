/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.document;

/**
 *
 * @author samuel
 */
public interface ArticleMetadataDoiDAO {
    
    public void updateDOI(int ID, String DOI, Source source);
    
    public String getDOI(int id);
    
    public enum Source { CROSSREF, METADATA }  
    
}
