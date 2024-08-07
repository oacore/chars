package uk.ac.core.common.model.task.parameters;

import java.util.Date;

/**
 *
 * @author mc26486
 */
public class RepositoryTaskParameters extends TaskParameters {

    private Integer repositoryId;
    private Date fromDate;
    private Date toDate;

    public RepositoryTaskParameters(Integer repositoryId) {
        this(repositoryId, null, null);
    }

    public RepositoryTaskParameters(Integer repositoryId, Date fromDate, Date toDate) {
        this.repositoryId = repositoryId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
    
    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "RepositoryTaskParameters{" + "repositoryId=" + repositoryId + '}';
    }

}
