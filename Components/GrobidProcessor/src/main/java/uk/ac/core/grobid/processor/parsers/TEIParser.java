/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.grobid.processor.parsers;

import java.util.List;
import uk.ac.core.common.model.GrobidAffiliationHeaderAuthor;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.ListBibl;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.TEI;
import uk.ac.core.common.model.GrobidCitation;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Author;
/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
public interface TEIParser {
    
    public ListBibl getListBibl(TEI teiObject);
    
    public List<Author> getListHeaderAuthors(TEI teiObject);
    
    public List<GrobidCitation> getListOfGrobidCitationsFromListBibl(Integer articleId, ListBibl listBibl)  throws IllegalAccessException;
    
    public List<GrobidAffiliationHeaderAuthor> processingGrobidHeaderAuthors(Integer articleId, List<Author> headerAuthorList);
    
}
