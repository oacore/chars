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
public class GrobidCitationRelAuthor {
    Integer citationId;
    Integer authorId;

    public Integer getCitationId() {
        return citationId;
    }

    public void setCitationId(Integer citationId) {
        this.citationId = citationId;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
}
