package uk.ac.core.extractmetadata.worker.oaipmh.metadataformats;

import org.slf4j.LoggerFactory;
import uk.ac.core.database.service.repositories.RepositoryMetadataFormatsDAO;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.Persist;
import uk.ac.core.extractmetadata.worker.oaipmh.models.OaiMetadataFormat;

/**
 *
 * @author samuel
 */
public class MetadataFormatPersist implements Persist<OaiMetadataFormat> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MetadataFormatPersist.class);

    Integer repositoryId;
    RepositoryMetadataFormatsDAO metadataFormatsDAO;

    public MetadataFormatPersist(Integer repositoryId, RepositoryMetadataFormatsDAO metadataFormatsDAO) {
        this.repositoryId = repositoryId;
        this.metadataFormatsDAO = metadataFormatsDAO;
    }

    @Override
    public void persist(OaiMetadataFormat format) {
        // Save the object somewhere....
        logger.debug("Object to save: " + format.toString());

        this.metadataFormatsDAO.insertMetadataFormat(format.getMetadataPrefix(), format.getSchema(), format.getMetadataNamespace());

        this.metadataFormatsDAO.insertOrUpdateRepositoryMetadataFormat(this.repositoryId, format.getMetadataPrefix());
    }
}
