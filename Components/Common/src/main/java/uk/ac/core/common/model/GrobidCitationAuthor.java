/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model;

/**
 *
 * @author vb4826
 */
public class GrobidCitationAuthor {
    
    Integer authorId;
    String forenameMiddle;
    String forenameFirst;
    String surname;

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getForenameMiddle() {
        return forenameMiddle;
    }

    public void setForenameMiddle(String forenameMiddle) {
        this.forenameMiddle = forenameMiddle;
    }

    public String getForenameFirst() {
        return forenameFirst;
    }

    public void setForenameFirst(String forenameFirst) {
        this.forenameFirst = forenameFirst;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
