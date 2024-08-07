package uk.ac.core.services.web.affiliations.model;

import java.util.ArrayList;
import java.util.List;

public class AffiliationsDiscoveryRequest {

    private Integer coreId;
    private Integer repoId;
    private String doi;
    private List<String> authors;

    public AffiliationsDiscoveryRequest() {
        this.authors = new ArrayList<>();
    }

    public AffiliationsDiscoveryRequest(Integer coreId, Integer repoId, String doi, List<String> authors) {
        this.coreId = coreId;
        this.repoId = repoId;
        this.doi = doi;
        this.authors = authors;
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

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
