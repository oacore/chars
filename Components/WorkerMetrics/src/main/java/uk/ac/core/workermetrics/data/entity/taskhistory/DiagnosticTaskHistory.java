package uk.ac.core.workermetrics.data.entity.taskhistory;

public class DiagnosticTaskHistory {

    private String taskParameters;

    private int daysPassedAfterTheFirstTry;

    public DiagnosticTaskHistory(String taskParameters, int daysPassedAfterTheFirstTry) {
        this.taskParameters = taskParameters;
        this.daysPassedAfterTheFirstTry = daysPassedAfterTheFirstTry;
    }

    public DiagnosticTaskHistory() {
    }

    public String getTaskParameters() {
        return taskParameters;
    }

    public void setTaskParameters(String taskParameters) {
        this.taskParameters = taskParameters;
    }

    public int getDaysPassedAfterTheFirstTry() {
        return daysPassedAfterTheFirstTry;
    }

    public void setDaysPassedAfterTheFirstTry(int date_diff) {
        this.daysPassedAfterTheFirstTry = date_diff;
    }
}
