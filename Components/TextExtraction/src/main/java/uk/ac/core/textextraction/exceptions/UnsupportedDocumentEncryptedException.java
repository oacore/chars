/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.textextraction.exceptions;

/**
 *
 * @author samuel
 */
public class UnsupportedDocumentEncryptedException extends TextExtractionException {
    
    public UnsupportedDocumentEncryptedException(String message) {
        super(message, TextExtractionErrorCodes.UNKNOWN_ATTACHMENT_ENCRPYTION);
    }
}
