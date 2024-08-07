/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
public class GrobidAffiliationInstitution {
    Integer institutionId;
    Integer position;
    Integer documentId;
    String name;
    String address;
    String country;
    String grobidAffiliationDepartmentsStr;
    String grobidAffiliationLabsStr;

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getGrobidAffiliationDepartmentsStr() {
        return grobidAffiliationDepartmentsStr;
    }

    public void setGrobidAffiliationDepartmentsStr(String grobidAffiliationDepartmentsStr) {
        this.grobidAffiliationDepartmentsStr = grobidAffiliationDepartmentsStr;
    }

    public String getGrobidAffiliationLabsStr() {
        return grobidAffiliationLabsStr;
    }

    public void setGrobidAffiliationLabsStr(String grobidAffiliationLabsStr) {
        this.grobidAffiliationLabsStr = grobidAffiliationLabsStr;
    }
    
    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
