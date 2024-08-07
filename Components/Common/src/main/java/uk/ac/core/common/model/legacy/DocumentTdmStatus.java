package uk.ac.core.common.model.legacy;

import java.util.Date;

/**
 * @author Giorgio Basile
 * @since 16/06/2017
 */
public class DocumentTdmStatus {

    private Integer idDocument;

    private Boolean tdmOnly;

    private Boolean fixed;

    private Date lastUpdateTime;

    public DocumentTdmStatus(){}

    public DocumentTdmStatus(Integer documentId, Boolean tdmOnly, Boolean fixed){
        this.idDocument = documentId;
        this.tdmOnly = tdmOnly;
        this.fixed = fixed;
    }

    public DocumentTdmStatus(Integer documentId, Boolean tdmOnly, Boolean fixed, Date lastUpdateTime){
        this.idDocument = documentId;
        this.tdmOnly = tdmOnly;
        this.fixed = fixed;
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Integer idDocument) {
        this.idDocument = idDocument;
    }

    public Boolean getTdmOnly() {
        return tdmOnly;
    }

    public void setTdmOnly(Boolean tdmOnly) {
        this.tdmOnly = tdmOnly;
    }

    public Boolean getFixed() {
        return fixed;
    }

    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
