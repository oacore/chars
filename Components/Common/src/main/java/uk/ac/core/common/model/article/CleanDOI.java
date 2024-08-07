/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model.article;

/**
 *
 * @author samuel
 */
public class CleanDOI {
    
    private final String doi;

    public CleanDOI(String doi) {
        this.doi = doi;
    }

    @Override
    public String toString() {
        return doi.replace("http://dx.doi.org/", "");
    }
}
