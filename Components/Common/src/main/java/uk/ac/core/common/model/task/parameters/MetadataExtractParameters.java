package uk.ac.core.common.model.task.parameters;

import java.util.Date;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;

/**
 *
 * @author samuel
 */
public class MetadataExtractParameters extends RepositoryTaskParameters {

    public MetadataExtractParameters(int repositoryId) {
        super(repositoryId);
    }

    public MetadataExtractParameters(Integer repositoryId) {
        super(repositoryId);
    }

    public MetadataExtractParameters(Integer repositoryId, Date fromDate, Date toDate) {
        super(repositoryId, fromDate, toDate);
    }

    
}
