package uk.ac.core.dataprovider.logic.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "core_ror_data")
public class RorData {

    @Column(name = "ror_id")
    private String rorId;
    @Id
    @Column(name = "core_id")
    private Long dataProviderId;
    @Column(name = "ror_name")
    private String institutionName;
    @Column(name = "aliases")
    private String aliases;
    @Column(name = "labels")
    private String labels;
    @Column(name = "acronyms")
    private String acronyms;
    @Column(name = "external_ids")
    private String externalIds;

    public String getRorId() {
        return rorId;
    }

    public void setRorId(String rorId) {
        this.rorId = rorId;
    }

    public Long getDataProviderId() {
        return dataProviderId;
    }

    public void setDataProviderId(Long dataProviderId) {
        this.dataProviderId = dataProviderId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getAliases() {
        return aliases;
    }

    public void setAliases(String aliases) {
        this.aliases = (aliases!=null)?aliases:"";
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = (labels!=null)?labels:"";;
    }

    public String getAcronyms() {
        return acronyms;
    }

    public void setAcronyms(String acronyms) {
        this.acronyms = (acronyms!=null)?acronyms:"";;;
    }

    public String getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(String externalIds) {
        this.externalIds = externalIds;
    }


}
