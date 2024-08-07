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
public class GrobidAffiliationDepartment {
    Integer deparmentId;
    String name;

    public Integer getDeparmentId() {
        return deparmentId;
    }

    public void setDeparmentId(Integer deparmentId) {
        this.deparmentId = deparmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
