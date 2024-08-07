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
public class GrobidAffiliationAuthor {
    Integer authorId;
    Integer position;
    String forenameFirst;
    String forenameMiddle;
    String rolename;
    String surname;
    String contact;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getForenameFirst() {
        return forenameFirst;
    }

    public void setForenameFirst(String forenameFirst) {
        this.forenameFirst = forenameFirst;
    }

    public String getForenameMiddle() {
        return forenameMiddle;
    }

    public void setForenameMiddle(String forenameMiddle) {
        this.forenameMiddle = forenameMiddle;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
