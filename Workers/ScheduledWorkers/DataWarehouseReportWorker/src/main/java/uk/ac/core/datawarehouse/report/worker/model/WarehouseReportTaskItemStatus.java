package uk.ac.core.datawarehouse.report.worker.model;

import com.amazonaws.services.s3.transfer.Upload;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.datawarehouse.report.worker.ReportType;

public class WarehouseReportTaskItemStatus extends TaskItemStatus {

    private ReportType reportType;
    private Long duration;
    private UploadStatus uploadStatus;
    private String messageOnError;
    private Report report;

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public UploadStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getMessageOnError() {
        return messageOnError;
    }

    public void setMessageOnError(String messageOnError) {
        this.messageOnError = messageOnError;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
