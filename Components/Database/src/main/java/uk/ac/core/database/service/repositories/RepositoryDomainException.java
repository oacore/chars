package uk.ac.core.database.service.repositories;

/**
 * Simple POJO about repository external domains that are allowed to download
 * @author lucas
 */
public class RepositoryDomainException {

    private Integer id;
    private Integer repositoryId;
    private String domainUrl;

    public RepositoryDomainException(Integer id, Integer repositoryId, String domainUrl) {
        this.id = id;
        this.repositoryId = repositoryId;
        this.domainUrl = domainUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    @Override
    public String toString() {
        return "RepositoryDomainException{" + "id=" + id + ", repositoryId=" + repositoryId + ", domainUrl=" + domainUrl + '}';
    }   
    
}
