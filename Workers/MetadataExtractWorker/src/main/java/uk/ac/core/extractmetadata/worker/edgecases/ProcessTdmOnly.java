package uk.ac.core.extractmetadata.worker.edgecases;

import org.slf4j.LoggerFactory;
import uk.ac.core.common.model.article.License;
import uk.ac.core.common.model.legacy.DocumentTdmStatus;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.XMLMetadataParser;

import java.util.Objects;

public class ProcessTdmOnly {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLMetadataParser.class);

    /**
     * Sets and updates TdmStatus according to the repository and license of the article
     * <p>
     * Public for testing...
     *
     * @param documentTdmStatus
     * @param license
     * @param repositoryTdmOnly
     */
    public void processTdmOnlyValues(DocumentTdmStatus documentTdmStatus, License license, Boolean repositoryTdmOnly) {
        if (!documentTdmStatus.getFixed()) {
            boolean isTdmOnly = repositoryTdmOnly;
            if (license.isOpenAccess()) {
                isTdmOnly = false;
            }

            if (Objects.equals(documentTdmStatus.getTdmOnly(), isTdmOnly)) {
                logger.info("TDM Status already set for " + documentTdmStatus.getIdDocument() + " - doing nothing");
            } else {
                logger.info("Updating tdm status to" + documentTdmStatus.getTdmOnly());
                documentTdmStatus.setTdmOnly(isTdmOnly);
                //documentTdmStatusDAO.insertOrUpdateTdmStatus(documentTdmStatus);
                logger.info("Tdm status successfully updated");
            }
        }
    }
}
