package uk.ac.core.datawarehouse.report.worker;

import uk.ac.core.common.model.task.TaskItem;

/**
 * @author lucas
 */
class WarehouseReportItem implements TaskItem {

    private String filename;
    private ReportType reportType;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
}
