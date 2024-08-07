/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;


import uk.ac.core.common.model.legacy.ArticleMetadata;

/**
 *
 * @author samuel
 */
public interface IDocumentHandler {

    /**
     * Get metadata of currently processed article (record).
     *
     * @return
     */
    ArticleMetadata getArticleMetadata();
    
}
