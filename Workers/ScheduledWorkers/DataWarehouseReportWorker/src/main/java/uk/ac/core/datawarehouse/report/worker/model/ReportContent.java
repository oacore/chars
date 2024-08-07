package uk.ac.core.datawarehouse.report.worker.model;

/**
 *
 * @author lucas
 */
public class ReportContent extends Report{

    Integer total;
    Integer metadata_only;
    Integer full_text_records;
    Integer records_with_Abstract;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getMetadata_only() {
        return metadata_only;
    }

    public void setMetadata_only(Integer metadata_only) {
        this.metadata_only = metadata_only;
    }

    public Integer getFull_text_records() {
        return full_text_records;
    }

    public void setFull_text_records(Integer full_text_records) {
        this.full_text_records = full_text_records;
    }

    public Integer getRecords_with_Abstract() {
        return records_with_Abstract;
    }

    public void setRecords_with_Abstract(Integer records_with_Abstract) {
        this.records_with_Abstract = records_with_Abstract;
    }
}
