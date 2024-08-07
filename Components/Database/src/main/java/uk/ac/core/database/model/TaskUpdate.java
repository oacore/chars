package uk.ac.core.database.model;

import uk.ac.core.common.model.legacy.ActionType;
import java.util.Date;

/**
 *
 * @author lucasanastasiou
 */
public class TaskUpdate {

    private Long idUpdate;
    private Integer repositoryId;
    private Integer articleId;
    private Date created;
    private Date lastUpdateTime;
    private TaskUpdateStatus status;
    private ActionType operation;
    private String message;
    private boolean hasStats;

    public Long getIdUpdate() {
        return idUpdate;
    }

    public void setIdUpdate(Long idUpdate) {
        this.idUpdate = idUpdate;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public TaskUpdateStatus getStatus() {
        return status;
    }

    public void setStatus(TaskUpdateStatus status) {
        this.status = status;
    }

    public ActionType getOperation() {
        return operation;
    }

    public void setOperation(ActionType operation) {
        this.operation = operation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isHasStats() {
        return hasStats;
    }

    public void setHasStats(boolean hasStats) {
        this.hasStats = hasStats;
    }

    @Override
    public String toString() {
        return "TaskUpdate{" + "idUpdate=" + idUpdate + ", repositoryId=" + repositoryId + ", articleId=" + articleId + ", created=" + created + ", lastUpdateTime=" + lastUpdateTime + ", status=" + status + ", operation=" + operation + ", message=" + message + ", hasStats=" + hasStats + '}';
    }

}
