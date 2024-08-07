package uk.ac.core.dataprovider.api.model;

import uk.ac.core.dataprovider.logic.entity.IdentifyResponse;

public class OaiPmhEndpointResponse {

    private String oaiPmhEndpoint;
    private String name;
    private String email;

    public OaiPmhEndpointResponse(IdentifyResponse identifyResponse) {
        this.oaiPmhEndpoint = identifyResponse.getBaseUrl();
        this.email = identifyResponse.getRepositoryEmail();
        this.name = identifyResponse.getRepositoryName();
    }

    public OaiPmhEndpointResponse(String oaiPmhEndpoint, String email, String name) {
        this.oaiPmhEndpoint = oaiPmhEndpoint;
        this.email = email;
        this.name = name;
    }
    public OaiPmhEndpointResponse() {
    }

    public String getOaiPmhEndpoint() {
        return oaiPmhEndpoint;
    }

    public void setOaiPmhEndpoint(String oaiPmhEndpoint) {
        this.oaiPmhEndpoint = oaiPmhEndpoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "OaiPmhEndpointResponse{" +
                "oaiPmhEndpoint='" + oaiPmhEndpoint + '\'' +
                ", repositoryName='" + name + '\'' +
                ", repositoryEmail='" + email + '\'' +
                '}';
    }
}