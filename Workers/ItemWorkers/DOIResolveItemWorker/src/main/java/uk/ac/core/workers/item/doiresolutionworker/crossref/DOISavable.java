/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.workers.item.doiresolutionworker.crossref;

import uk.ac.core.workers.item.doiresolutionworker.crossref.Response.Result;

/**
 *
 * @author samuel
 */
public interface DOISavable {
    
    void save(CrossrefCitation crossrefCitation, Result result);
}
