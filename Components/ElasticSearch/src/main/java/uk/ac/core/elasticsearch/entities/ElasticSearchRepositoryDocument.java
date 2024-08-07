package uk.ac.core.elasticsearch.entities;

import java.util.Date;
import uk.ac.core.common.model.article.DeletedStatus;

/**
 *
 * @author la4227 <lucas.anastasiou@open.ac.uk>
 */
public class ElasticSearchRepositoryDocument {

    private Integer pdfStatus;
    private Integer textStatus;
    
    /**
     * the date the item was deposited in CORE
     */
    private Date metadataAdded;
    private Date metadataUpdated;
    
    /**
     * The raw header - datetime value in oai-pmh
     */
    private Date timestamp;
    
    /**
     * date deposited in the data provider 
     * @See metadataAdded for the date deposited in CORE
     */
    private Date depositedDate;
    private Integer indexed;
    private String deletedStatus;
    private Integer previewStatus;
    private Long pdfSize;
    private Boolean tdmOnly;
    private String pdfOrigin;

    public Integer getPdfStatus() {
        return pdfStatus;
    }

    public void setPdfStatus(Integer pdfStatus) {
        this.pdfStatus = pdfStatus;
    }

    public Integer getTextStatus() {
        return textStatus;
    }

    public void setTextStatus(Integer textStatus) {
        this.textStatus = textStatus;
    }

    public Date getMetadataAdded() {
        return metadataAdded;
    }

    public void setMetadataAdded(Date metadataAdded) {
        this.metadataAdded = metadataAdded;
    }

    public Date getMetadataUpdated() {
        return metadataUpdated;
    }

    public void setMetadataUpdated(Date metadataUpdated) {
        this.metadataUpdated = metadataUpdated;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * The date the item was deposited in the Data Provider (repository/journal)
     * @return 
     */
    public Date getDepositedDate() {
        return depositedDate;
    }

    public void setDepositedDate(Date depositedDate) {
        this.depositedDate = depositedDate;
    }
    
    public Integer getIndexed() {
        return indexed;
    }

    public void setIndexed(Integer indexed) {
        this.indexed = indexed;
    }

    public String getDeletedStatus() {
        return deletedStatus;
    }

    public void setDeletedStatus(DeletedStatus deletedStatus) {
        this.deletedStatus = Integer.toString(deletedStatus.getValue());
    }

    public Long getPdfSize() {
        return pdfSize;
    }

    public void setPdfSize(Long pdfSize) {
        this.pdfSize = pdfSize;
    }

    public Boolean isTdmOnly() {
        return tdmOnly;
    }

    public void setTdmOnly(Boolean tdmOnly) {
        this.tdmOnly = tdmOnly;
    }

    public String getPdfOrigin() {
        return pdfOrigin;
    }

    public void setPdfOrigin(String pdfOrigin) {
        this.pdfOrigin = pdfOrigin;
    }    
}
