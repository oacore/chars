package uk.ac.core.common.tasks.metadataDownload;

import java.util.Date;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;

/**
 *
 * @author samuel
 */
public class MetadataDownloadParameters extends RepositoryTaskParameters {

    public MetadataDownloadParameters(int repositoryId) {
        super(repositoryId);
    }

    public MetadataDownloadParameters(int repositoryId, Date fromDate, Date toDate) {
        super(repositoryId, fromDate, toDate);
    }

}
