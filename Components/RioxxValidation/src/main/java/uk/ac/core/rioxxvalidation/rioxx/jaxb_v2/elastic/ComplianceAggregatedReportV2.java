package uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.elastic;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

/**
 *
 * @author mc26486
 */
/**
 *
 * @author mc26486
 */
@Document(indexName = "compliance-aggregate", type = "aggregate")
public class ComplianceAggregatedReportV2 {

    @Id
    private String repositoryId;
    private Integer totalRecords;
    private Integer compliantRecordBasic;
    private Integer compliantRecordFull;
    private Integer invalidRecords;
    private Integer brokenRecords;

    public ComplianceAggregatedReportV2() {
    }

    public ComplianceAggregatedReportV2(String repositoryId, Integer totalRecords, Integer compliantRecordBasic,
                                        Integer compliantRecordFull, Integer invalidRecords, Integer brokenRecords) {
        this.repositoryId = repositoryId;
        this.totalRecords = totalRecords;
        this.compliantRecordBasic = compliantRecordBasic;
        this.compliantRecordFull = compliantRecordFull;
        this.invalidRecords = invalidRecords;
        this.brokenRecords = brokenRecords;
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

    public Integer getCompliantRecordBasic() {
        return compliantRecordBasic;
    }

    public void setCompliantRecordBasic(Integer compliantRecordBasic) {
        this.compliantRecordBasic = compliantRecordBasic;
    }

    public Integer getCompliantRecordFull() {
        return compliantRecordFull;
    }

    public void setCompliantRecordFull(Integer compliantRecordFull) {
        this.compliantRecordFull = compliantRecordFull;
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

}
