package uk.ac.core.elasticsearch.entities;

import java.sql.Timestamp;

/**
 *
 * @author mc26486
 */
public class ElasticSearchCrossrefDocument {
    private String doi;
    private Timestamp acceptedDate;
    private Timestamp publishedDate;
    private Timestamp depositedDate;
    private String issn;

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public Timestamp getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(Timestamp acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public Timestamp getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Timestamp publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Timestamp getDepositedDate() {
        return depositedDate;
    }

    public void setDepositedDate(Timestamp depositedDate) {
        this.depositedDate = depositedDate;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }
    
    
    
}
