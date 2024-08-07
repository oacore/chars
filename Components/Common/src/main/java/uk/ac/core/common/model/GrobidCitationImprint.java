/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model;

import java.util.List;

/**
 *
 * @author vb4826
 */
public class GrobidCitationImprint {
    String publisher;
    String date;
    String dateType;
    String grobidBiblScopesStr = "";
    List<GrobidCitationBiblScope> grobidBiblScopes;
    
    public void addGrobidBiblScopesStr(String addStr) {
        this.grobidBiblScopesStr += (this.grobidBiblScopesStr.isEmpty()) ? addStr : " " + addStr;
    }

    public String getGrobidBiblScopesStr() {
        return grobidBiblScopesStr;
    }

    public void setGrobidBiblScopesStr(String grobidBiblScopesStr) {
        this.grobidBiblScopesStr = grobidBiblScopesStr;
    }

    public List<GrobidCitationBiblScope> getGrobidBiblScopes() {
        return grobidBiblScopes;
    }

    public void setGrobidBiblScopes(List<GrobidCitationBiblScope> grobidBiblScopes) {
        this.grobidBiblScopes = grobidBiblScopes;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        if(this.publisher == null){ 
            this.publisher = publisher;
        }else{
            this.publisher += ", "  + publisher;
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
