/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;

/**
 *
 * @author dk5588
 */
public enum RepositoryType {

    PUBMED(150),
    ARXIV(144),
    SPRINGER(692),
    DOAJ(645);

    private final int repositoryId;

    private RepositoryType(int repositoryId) {
        this.repositoryId = repositoryId;
    }

    public int getRepositoryId() {
        return repositoryId;
    }
}
