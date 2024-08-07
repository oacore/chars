/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.reporting.metrics;

import uk.ac.core.database.model.TaskUpdateReporting;
import java.util.List;

/**
 *
 * @author mc26486
 */
public class TaskUpdateMetric {

    private List<TaskUpdateReporting> taskUpdates;
    private List<TaskUpdateReporting> successfulRepos;
    private List<TaskUpdateReporting> failedReposPdf;
    private List<TaskUpdateReporting> failedReposMd;
    private Long successfulCount;
    private Long failedCount;

    public List<TaskUpdateReporting> getTaskUpdates() {
        return taskUpdates;
    }

    public void setTaskUpdates(List<TaskUpdateReporting> taskUpdates) {
        this.taskUpdates = taskUpdates;
    }

    public List<TaskUpdateReporting> getSuccessfulRepos() {
        return successfulRepos;
    }

    public void setSuccessfulRepos(List<TaskUpdateReporting> successfulRepos) {
        this.successfulRepos = successfulRepos;
    }

    public List<TaskUpdateReporting> getFailedReposPdf() {
        return failedReposPdf;
    }

    public void setFailedReposPdf(List<TaskUpdateReporting> failedReposPdf) {
        this.failedReposPdf = failedReposPdf;
    }

    public List<TaskUpdateReporting> getFailedReposMd() {
        return failedReposMd;
    }

    public void setFailedReposMd(List<TaskUpdateReporting> failedReposMd) {
        this.failedReposMd = failedReposMd;
    }

    public Long getSuccessfulCount() {
        return successfulCount;
    }

    public void setSuccessfulCount(Long successfulCount) {
        this.successfulCount = successfulCount;
    }

    public Long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Long failedCount) {
        this.failedCount = failedCount;
    }

    @Override
    public String toString() {
        return "TaskUpdateMetric{" + "taskUpdates=" + taskUpdates + ", successfulRepos=" + successfulRepos + ", failedReposPdf=" + failedReposPdf + ", failedReposMd=" + failedReposMd + ", countSuccessful=" + successfulCount + ", countFailures=" + failedCount + '}';
    }

}
