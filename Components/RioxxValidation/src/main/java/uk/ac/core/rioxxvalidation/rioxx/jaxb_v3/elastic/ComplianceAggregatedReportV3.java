package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.elastic;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "compliance-aggregate-v3", type = "aggregate")
public class ComplianceAggregatedReportV3 {
    @Id
    private String repositoryId;
    private Integer totalRecords;
    private Integer compliantRequiredData;
    private Integer compliantOptionalData;
    private Integer invalidRecords;
    private Integer brokenRecords;

    public ComplianceAggregatedReportV3() {
    }

    public ComplianceAggregatedReportV3(String repositoryId, Integer totalRecords, Integer invalidRecords,
                                        Integer brokenRecords, Integer compliantRequiredData, Integer compliantOptionalData) {
        this.repositoryId = repositoryId;
        this.totalRecords = totalRecords;
        this.invalidRecords = invalidRecords;
        this.brokenRecords = brokenRecords;
        this.compliantRequiredData = compliantRequiredData;
        this.compliantOptionalData = compliantOptionalData;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Integer getInvalidRecords() {
        return invalidRecords;
    }

    public void setInvalidRecords(Integer invalidRecords) {
        this.invalidRecords = invalidRecords;
    }

    public Integer getBrokenRecords() {
        return brokenRecords;
    }

    public void setBrokenRecords(Integer brokenRecords) {
        this.brokenRecords = brokenRecords;
    }

    public Integer getCompliantRequiredData() {
        return compliantRequiredData;
    }

    public void setCompliantRequiredData(Integer compliantRequiredData) {
        this.compliantRequiredData = compliantRequiredData;
    }

    public Integer getCompliantOptionalData() {
        return compliantOptionalData;
    }

    public void setCompliantOptionalData(Integer compliantOptionalData) {
        this.compliantOptionalData = compliantOptionalData;
    }
}
