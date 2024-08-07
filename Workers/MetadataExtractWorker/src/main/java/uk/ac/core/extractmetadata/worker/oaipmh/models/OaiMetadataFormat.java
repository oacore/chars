/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.extractmetadata.worker.oaipmh.models;

/**
 * A POJO representing a MetadataFormat described by OAI-PMH
 * @author samuel
 */
public class OaiMetadataFormat {
    
    private String metadataPrefix;
    private String schema;
    private String metadataNamespace;

    public OaiMetadataFormat() {
    }

    public OaiMetadataFormat(String metadataPrefix, String schema, String metadataNamespace) {
        this.metadataPrefix = metadataPrefix;
        this.schema = schema;
        this.metadataNamespace = metadataNamespace;
    }
    
    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getMetadataNamespace() {
        return metadataNamespace;
    }

    public void setMetadataNamespace(String metadataNamespace) {
        this.metadataNamespace = metadataNamespace;
    }

    @Override
    public String toString() {
        return "OaiMetadataFormat{" + "metadataPrefix=" + metadataPrefix + ", schema=" + schema + ", metadataNamespace=" + metadataNamespace + '}';
    }
}
