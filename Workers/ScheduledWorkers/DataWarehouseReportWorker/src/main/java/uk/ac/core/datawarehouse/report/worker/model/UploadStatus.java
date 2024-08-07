package uk.ac.core.datawarehouse.report.worker.model;

public class UploadStatus {
    private String cloudLink;
    private boolean success;
    private String messageOnError;

    public String getCloudLink() {
        return cloudLink;
    }

    public void setCloudLink(String cloudLink) {
        this.cloudLink = cloudLink;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessageOnError() {
        return messageOnError;
    }

    public void setMessageOnError(String messageOnError) {
        this.messageOnError = messageOnError;
    }
}
