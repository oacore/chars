/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.grobid.processor.store;

import uk.ac.core.common.model.GrobidAffiliationHeaderAuthor;
import uk.ac.core.common.model.GrobidCitation;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
public interface StoreSeparateAddData {
    
    public void storeCitationSeparateAddData(GrobidCitation grobidCitation, Integer citationKey);
    
    public void storeAffiliationSeparateAddData(GrobidAffiliationHeaderAuthor headerAuthor, Integer citationKey);
}
