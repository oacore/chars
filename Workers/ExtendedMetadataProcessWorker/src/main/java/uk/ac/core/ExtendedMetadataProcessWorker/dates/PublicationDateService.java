package uk.ac.core.ExtendedMetadataProcessWorker.dates;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.crossref.CrossrefService;
import uk.ac.core.database.mucc.MUCCDocument;
import uk.ac.core.database.mucc.MUCCDocumentDAO;
import uk.ac.core.database.service.document.DocumentDAO;

import java.io.IOException;
import java.sql.Timestamp;

@Service
public class PublicationDateService {

    @Autowired
    MUCCDocumentDAO muccDocumentDAO;

    @Autowired
    CrossrefService crossrefService;

    @Autowired
    DocumentDAO documentDAO;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(PublicationDateService.class);


    public void checkAndSavePublicationDate(Integer documentId) {
        MUCCDocument muccDocument = this.muccDocumentDAO.load(documentId);
        String doi;
        if (muccDocument == null || muccDocument.getDoi().isEmpty()) {
            doi = this.documentDAO.getArticleDoiById(documentId);
        } else {
            doi = muccDocument.getDoi();
        }
        if (doi != null) {
            Timestamp published;
            try {

                published = this.crossrefService.downloadPublicationDate(doi);
                if (muccDocument == null) {
                    muccDocument = new MUCCDocument();
                    muccDocument.setDoi(doi);
                    muccDocument.setPublished(published);
                    muccDocument.setCoreId(String.valueOf(documentId));
                }
                muccDocument.setPublished(published);
                muccDocumentDAO.save(muccDocument);
            } catch (IOException e) {
                logger.info("Article with doi: {} failed to collect published date.", doi);
            }
        }
    }
}
