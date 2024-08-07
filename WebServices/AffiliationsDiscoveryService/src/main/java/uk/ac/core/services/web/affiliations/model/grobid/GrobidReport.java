package uk.ac.core.services.web.affiliations.model.grobid;

public class GrobidReport {
    private Integer repoId;
    private Integer coreId;
    private Integer itemsTotal;
    private Integer itemsSuccess;
    private Integer itemsSkipped;
    private String message;
    private Long duration;

    public GrobidReport() {
    }

    public GrobidReport(
            Integer repoId,
            Integer coreId,
            Integer itemsTotal,
            Integer itemsSuccess,
            Integer itemsSkipped, String message,
            Long duration) {
        this.repoId = repoId;
        this.coreId = coreId;
        this.itemsTotal = itemsTotal;
        this.itemsSuccess = itemsSuccess;
        this.itemsSkipped = itemsSkipped;
        this.message = message;
        this.duration = duration;
    }

    public Integer getItemsSkipped() {
        return itemsSkipped;
    }

    public void setItemsSkipped(Integer itemsSkipped) {
        this.itemsSkipped = itemsSkipped;
    }

    public Integer getRepoId() {
        return repoId;
    }

    public void setRepoId(Integer repoId) {
        this.repoId = repoId;
    }

    public Integer getCoreId() {
        return coreId;
    }

    public void setCoreId(Integer coreId) {
        this.coreId = coreId;
    }

    public Integer getItemsTotal() {
        return itemsTotal;
    }

    public void setItemsTotal(Integer itemsTotal) {
        this.itemsTotal = itemsTotal;
    }

    public Integer getItemsSuccess() {
        return itemsSuccess;
    }

    public void setItemsSuccess(Integer itemsSuccess) {
        this.itemsSuccess = itemsSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
