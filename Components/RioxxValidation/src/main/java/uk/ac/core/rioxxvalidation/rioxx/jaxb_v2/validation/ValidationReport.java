/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.validation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.apache.commons.codec.digest.DigestUtils;
import uk.ac.core.rioxxvalidation.rioxx.ValidationReportAbstract;

/**
 *
 * @author mc26486
 */
@Document(indexName = "compliance-report", type = "report")
@Mapping(mappingPath = "/elasticsearch/mappings/report.json")
public class ValidationReport extends ValidationReportAbstract {

    public static String getReportId(Integer repositoryId, String recordIdentifier) {
        return DigestUtils.md5Hex(repositoryId.toString() + recordIdentifier);
    }

    @Id
    private String id;
    private List<String> missingRequiredFieldFull = new ArrayList<>();
    private List<String> missingRequiredFieldBasic = new ArrayList<>();
    private boolean parseFailed = false;
    private String recordIdentifier;
    private Integer repositoryId;
    private String reportDate;

    public ValidationReport() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        this.reportDate = dateFormat.format(cal.getTime());
        this.rioxxVersion="2";
    }

    public String getId() {
        if (id != null) {
            return id;
        } else {
            return ValidationReport.getReportId(repositoryId, recordIdentifier);
        }
    }

    public List<String> getMissingRequiredFieldFull() {
        return missingRequiredFieldFull;
    }

    public void setMissingRequiredFieldFull(List<String> missingRequiredFieldFull) {
        this.missingRequiredFieldFull = missingRequiredFieldFull;
    }

    public List<String> getMissingRequiredFieldBasic() {
        return missingRequiredFieldBasic;
    }

    public void setMissingRequiredFieldBasic(List<String> missingRequiredFieldBasic) {
        this.missingRequiredFieldBasic = missingRequiredFieldBasic;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public boolean isParseFailed() {
        return parseFailed;
    }

    public void setParseFailed(boolean parseFailed) {
        this.parseFailed = parseFailed;
    }

    public boolean isAValidRecordFull() {
        return !isParseFailed() && missingRequiredFieldFull.isEmpty();
    }

    public boolean isAValidRecordBasic() {
        return !isParseFailed() && missingRequiredFieldBasic.isEmpty();
    }

    public boolean isAValidRecord() {
        return isAValidRecordBasic() && isAValidRecordFull();
    }

    public String getRecordIdentifier() {
        return recordIdentifier;
    }

    public void setRecordIdentifier(String recordIdentifier) {
        this.recordIdentifier = recordIdentifier;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    @Override
    public String toString() {
        return "ValidationReport{" + "missingRequiredFieldFull=" + missingRequiredFieldFull + ", missingRequiredFieldBasic=" + missingRequiredFieldBasic + ", parseFailed=" + parseFailed + ", recordIdentifier=" + recordIdentifier + ", repositoryId=" + repositoryId + ", reportDate=" + reportDate + '}';
    }

}
