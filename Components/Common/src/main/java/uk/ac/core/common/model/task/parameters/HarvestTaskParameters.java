package uk.ac.core.common.model.task.parameters;

import java.util.Date;

/**
 *
 * @author mc26486
 */
public class HarvestTaskParameters extends RepositoryTaskParameters{
    
    public HarvestTaskParameters(int repositoryId) {
        super(repositoryId);
    }

    public HarvestTaskParameters(Integer repositoryId) {
        super(repositoryId);
    }

    public HarvestTaskParameters(Integer repositoryId, Date fromDate) {
        super(repositoryId, fromDate, null);
    }

    public HarvestTaskParameters(Integer repositoryId, Date fromDate, Date toDate) {
        super(repositoryId, fromDate, toDate);
    }
}
