package uk.ac.core.database.mucc;

import java.sql.Timestamp;

/**
 *
 * @author mc26486
 */
public class MUCCDocument {

    private String magId;
    private String doi;
    private String coreId;
    private String issn;
    private Timestamp published;
    private Timestamp accepted;
    private Timestamp deposited;
    private Integer citationCount=0;
    private Integer estimatedCitationCount=0;

    public String getMagId() {
        return magId;
    }

    public void setMagId(String magId) {
        this.magId = magId;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getCoreId() {
        return coreId;
    }

    public void setCoreId(String coreId) {
        this.coreId = coreId;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public Timestamp getPublished() {
        return published;
    }

    public void setPublished(Timestamp published) {
        this.published = published;
    }

    public Timestamp getAccepted() {
        return accepted;
    }

    public void setAccepted(Timestamp accepted) {
        this.accepted = accepted;
    }

    public Timestamp getDeposited() {
        return deposited;
    }

    public void setDeposited(Timestamp deposited) {
        this.deposited = deposited;
    }

    public Integer getCitationCount() {
        return citationCount;
    }

    public void setCitationCount(Integer citationCount) {
        this.citationCount = citationCount;
    }

    public Integer getEstimatedCitationCount() {
        return estimatedCitationCount;
    }

    public void setEstimatedCitationCount(Integer estimatedCitationCount) {
        this.estimatedCitationCount = estimatedCitationCount;
    }

    @Override
    public String toString() {
        return "MUCCDocument{" + "magId=" + magId + ", doi=" + doi + ", coreId=" + coreId + ", issn=" + issn + ", published=" + published + ", accepted=" + accepted + ", deposited=" + deposited + ", citationCount=" + citationCount + ", estimatedCitationCount=" + estimatedCitationCount + '}';
    }

}
