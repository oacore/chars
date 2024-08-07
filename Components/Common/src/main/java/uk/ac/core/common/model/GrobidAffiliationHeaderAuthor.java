/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model;

import java.util.List;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
public class GrobidAffiliationHeaderAuthor {
    Integer headerAuthorId;
    Integer documentId;
    List<GrobidAffiliationAuthor> grobidAffiliationAuthors;
    List<GrobidAffiliationInstitution> grobidAffiliationInstitutions;
    List<GrobidAffiliationDepartment> grobidAffiliationDepartments;
    List<GrobidAffiliationLaboratory> grobidAffiliationLaborators;

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public Integer getHeaderAuthorId() {
        return headerAuthorId;
    }

    public void setHeaderAuthorId(Integer headerAuthorId) {
        this.headerAuthorId = headerAuthorId;
    }

    public List<GrobidAffiliationAuthor> getGrobidAffiliationAuthors() {
        return grobidAffiliationAuthors;
    }

    public void setGrobidAffiliationAuthors(List<GrobidAffiliationAuthor> grobidAffiliationAuthors) {
        this.grobidAffiliationAuthors = grobidAffiliationAuthors;
    }

    public List<GrobidAffiliationInstitution> getGrobidAffiliationInstitutions() {
        return grobidAffiliationInstitutions;
    }

    public void setGrobidAffiliationInstitutions(List<GrobidAffiliationInstitution> grobidAffiliationInstitutions) {
        this.grobidAffiliationInstitutions = grobidAffiliationInstitutions;
    }

    public List<GrobidAffiliationDepartment> getGrobidAffiliationDepartments() {
        return grobidAffiliationDepartments;
    }

    public void setGrobidAffiliationDepartments(List<GrobidAffiliationDepartment> grobidAffiliationDepartments) {
        this.grobidAffiliationDepartments = grobidAffiliationDepartments;
    }

    public List<GrobidAffiliationLaboratory> getGrobidAffiliationLaborators() {
        return grobidAffiliationLaborators;
    }

    public void setGrobidAffiliationLaborators(List<GrobidAffiliationLaboratory> grobidAffiliationLaborators) {
        this.grobidAffiliationLaborators = grobidAffiliationLaborators;
    }
}
