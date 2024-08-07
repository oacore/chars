/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.rioxxvalidation.rioxx;



/**
 *
 * @author mc26486
 */
public interface ComplianceCheckerListener {
    void updateCompliance(uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.validation.ValidationReport validationReport);

    void updateCompliance(uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation.ValidationReport validationReport);
}
