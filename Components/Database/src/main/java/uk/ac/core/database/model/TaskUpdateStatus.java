package uk.ac.core.database.model;

/**
 *
 * @author lucasanastasiou
 */
public enum TaskUpdateStatus {

    SUCCESSFUL("successful"),
    UNSUCCESSFUL("unsuccessful"),
    WAITING("waiting"),
    RUNNING("running");

    private final String text;

    TaskUpdateStatus(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    static public TaskUpdateStatus fromString(String statusStr) {
        for (TaskUpdateStatus status : TaskUpdateStatus.values()) {
            if (status.toString().equals(statusStr)) {
                return status;
            }
        }
        return null;
    }
}