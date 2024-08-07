package uk.ac.core.common.model;

import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public class GrobidCitation {

    Integer articleId;
    Integer positionInBiblList;
    String xmlId;
    String title;
    String doi;
    String address;
    String authorsString = "";
    List<GrobidCitationAuthor> grobidAuthors;
    GrobidCitationImprint GrobidCitationImprint;
    
    public void addAuthorToAuthorsStr(String addStr, boolean isLastName) {
        if(this.authorsString.isEmpty()){
            this.authorsString += addStr;
        }else{
            if(isLastName){
                this.authorsString += " and " + addStr;
            }else{
                this.authorsString += ", " + addStr;
            }
        }
    }

    public String getAuthorsString() {
        return authorsString;
    }

    public void setAuthorsString(String authorsString) {
        this.authorsString = authorsString;
    }

    public Integer getPositionInBiblList() {
        return positionInBiblList;
    }

    public void setPositionInBiblList(Integer positionInBiblList) {
        this.positionInBiblList = positionInBiblList;
    }

    public GrobidCitationImprint getGrobidCitationImprint() {
        return GrobidCitationImprint;
    }

    public void setGrobidCitationImprint(GrobidCitationImprint GrobidCitationImprint) {
        this.GrobidCitationImprint = GrobidCitationImprint;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String Address) {
        this.address = Address;
    }

    public List<GrobidCitationAuthor> getGrobidAuthors() {
        return grobidAuthors;
    }

    public void setGrobidAuthors(List<GrobidCitationAuthor> grobidAuthors) {
        this.grobidAuthors = grobidAuthors;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public String getXmlId() {
        return xmlId;
    }

    public void setXmlId(String xmlId) {
        this.xmlId = xmlId;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }
}
