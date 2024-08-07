/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import uk.ac.core.rioxxvalidation.rioxx.ValidationReportAbstract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author MTarasiuk
 */
@Document(indexName = "compliance-report-v3", type = "report")
@Mapping(mappingPath = "/elasticsearch/mappings/report.json")
public class ValidationReport extends ValidationReportAbstract {

    public static String getReportId(Integer repositoryId, String recordIdentifier) {
        return DigestUtils.md5Hex(repositoryId.toString() + recordIdentifier);
    }

    @Id
    private String id;

    private Map<String, List<String>> missingRequiredData;
    private Map<String, List<String>> missingOptionalData;
    private boolean parseFailed = false;
    private String recordIdentifier;
    private Integer repositoryId;
    private String reportDate;

    public ValidationReport() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        this.reportDate = dateFormat.format(cal.getTime());
        this.rioxxVersion="3";
    }

    public String getId() {
        if (id != null) {
            return id;
        } else {
            return ValidationReport.getReportId(repositoryId, recordIdentifier);
        }
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

    public boolean isValidDataRequiredData() {
        return !isParseFailed() && missingRequiredData.isEmpty();
    }

    public boolean isValidDataOptionalData() {
        return !isParseFailed() && missingOptionalData.isEmpty();
    }

    public boolean isAValidRecord() {
        return !isParseFailed() && missingRequiredData.isEmpty();
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

    public Map<String, List<String>> getMissingRequiredData() {
        return missingRequiredData;
    }

    public void setMissingRequiredData(Map<String, List<String>> missingRequiredData) {
        this.missingRequiredData = missingRequiredData;
    }

    public Map<String, List<String>> getMissingOptionalData() {
        return missingOptionalData;
    }

    public void setMissingOptionalData(Map<String, List<String>> missingOptionalData) {
        this.missingOptionalData = missingOptionalData;
    }

    //todo: is it correct?
    @Override
    public String toString() {
        return "ValidationReport{" + "missingRequiredField=" + missingRequiredData + ", parseFailed=" + parseFailed + ", recordIdentifier=" + recordIdentifier + ", repositoryId=" + repositoryId + ", reportDate=" + reportDate + '}';
    }

}
