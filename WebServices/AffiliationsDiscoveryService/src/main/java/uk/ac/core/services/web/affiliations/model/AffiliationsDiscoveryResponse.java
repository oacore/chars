package uk.ac.core.services.web.affiliations.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AffiliationsDiscoveryResponse {

    private String source;
    private Integer coreId;
    private Integer repoId;
    private Date dateCreated;
    private Integer count;
    private Long took;
    private String message;
    private List<AffiliationsDiscoveryResponseItem> hits;

    public AffiliationsDiscoveryResponse() {
        this.hits = new ArrayList<>();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getCoreId() {
        return coreId;
    }

    public void setCoreId(Integer coreId) {
        this.coreId = coreId;
    }

    public Integer getRepoId() {
        return repoId;
    }

    public void setRepoId(Integer repoId) {
        this.repoId = repoId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getTook() {
        return took;
    }

    public void setTook(Long took) {
        this.took = took;
    }

    public List<AffiliationsDiscoveryResponseItem> getHits() {
        return hits;
    }

    public void setHits(List<AffiliationsDiscoveryResponseItem> hits) {
        this.hits = hits;
    }
}
