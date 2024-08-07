package uk.ac.core.dataprovider.logic.entity;

public class IdentifyResponse {
    private String repositoryName;
    private String repositoryEmail;
    private String baseUrl;

    public IdentifyResponse(String repositoryName, String repositoryEmail, String baseUrl) {
        this.repositoryName = repositoryName;
        this.repositoryEmail = repositoryEmail;
        this.baseUrl = baseUrl;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryEmail() {
        return repositoryEmail;
    }

    public void setRepositoryEmail(String repositoryEmail) {
        this.repositoryEmail = repositoryEmail;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String toString() {
        return "IdentifyResponse{" +
                "repositoryName='" + repositoryName + '\'' +
                ", repositoryEmail='" + repositoryEmail + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                '}';
    }
}
