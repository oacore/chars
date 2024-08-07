package uk.ac.core.reporting.metrics.service.dto;

import java.util.Date;
import java.util.Map;

public class BigRepositoryMetric {

    private Integer repositoryId;
    private Map<String, Date> taskLastSuccessDate;

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Map<String, Date> getTaskLastSuccessDate() {
        return taskLastSuccessDate;
    }

    public void setTaskLastSuccessDate(Map<String, Date> taskLastSuccessDate) {
        this.taskLastSuccessDate = taskLastSuccessDate;
    }
}
