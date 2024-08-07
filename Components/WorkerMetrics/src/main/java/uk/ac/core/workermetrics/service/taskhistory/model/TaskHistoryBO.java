package uk.ac.core.workermetrics.service.taskhistory.model;

public final class TaskHistoryBO {

    private int repositoryId;
    private int daysPassedAfterTheFirstTry;

    public int getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(int repositoryId) {
        this.repositoryId = repositoryId;
    }

    public int getDaysPassedAfterTheFirstTry() {
        return daysPassedAfterTheFirstTry;
    }

    public void setDaysPassedAfterTheFirstTry(int daysPassedAfterTheFirstTry) {
        this.daysPassedAfterTheFirstTry = daysPassedAfterTheFirstTry;
    }
}