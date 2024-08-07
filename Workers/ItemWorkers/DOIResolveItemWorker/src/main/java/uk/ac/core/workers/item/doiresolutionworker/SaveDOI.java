/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.workers.item.doiresolutionworker;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.database.crossref.CrossrefDoisForDocumentId;
import uk.ac.core.database.service.document.ArticleMetadataDoiDAO;
import uk.ac.core.database.service.document.ArticleMetadataDoiDAO.Source;
import uk.ac.core.workers.item.doiresolutionworker.crossref.CrossrefCitation;
import uk.ac.core.workers.item.doiresolutionworker.crossref.DOISavable;
import uk.ac.core.workers.item.doiresolutionworker.crossref.Response;

/**
 *
 * @author samuel
 */
@Service
public class SaveDOI implements DOISavable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SaveDOI.class);

    ArticleMetadataDoiDAO articleMetadataDoiDAO;

    CrossrefDoisForDocumentId crossrefDoisForDocumentId;

    @Autowired
    public SaveDOI(ArticleMetadataDoiDAO articleMetadataDoiDAO,
            CrossrefDoisForDocumentId crossrefDoisForDocumentId) {
        this.articleMetadataDoiDAO = articleMetadataDoiDAO;
        this.crossrefDoisForDocumentId = crossrefDoisForDocumentId;

    }

    @Override
    public void save(CrossrefCitation crossrefCitation, Response.Result result) {
        if (result.isMatch()) {
            if (!this.crossrefDoisForDocumentId.isDocumentIdResolved(crossrefCitation.getId())) {
                this.crossrefDoisForDocumentId.insert(
                        crossrefCitation.getId(),
                        result.getText(),
                        result.getDoi().replace("http://dx.doi.org/", ""),
                        result.getCoins(),
                        result.getScore());
            }
            if (result.getScore() > 80) {
                logger.debug("Saving DOI. Score above 50, save document ID: " + crossrefCitation.getId() + "    DOI: " + result.getDoi() + "    Score: " + result.getScore());
                this.articleMetadataDoiDAO.updateDOI(crossrefCitation.getId(), result.getDoi(), Source.CROSSREF);
            }
        } else {
            logger.debug("NOT Saving DOI. Score below 50, save document ID: " + crossrefCitation.getId() + "    DOI: " + result.getDoi() + "    Score: " + result.getScore());
        }
    }

}
