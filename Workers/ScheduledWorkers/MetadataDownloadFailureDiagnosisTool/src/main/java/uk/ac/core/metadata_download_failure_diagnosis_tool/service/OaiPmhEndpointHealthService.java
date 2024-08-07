package uk.ac.core.metadata_download_failure_diagnosis_tool.service;

public interface OaiPmhEndpointHealthService {
    boolean isOaiPmhAlive(String oaiPmhEndpoint, String metadataFormat);
    boolean isOaiPmhEmpty(String oaiPmhEndpoint, String metadataFormat);
}
