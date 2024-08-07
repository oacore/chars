/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.filesystemdocumentworker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.filesystemdocumentworker.DocumentFilesystemService;

import java.io.UnsupportedEncodingException;

/**
 * @author mc26486
 */
@RestController
public class StarterController {
    @Autowired
    private DocumentFilesystemService service;

    @RequestMapping("/document_filesystem_item/{documentId}")
    public String processItemController(@PathVariable(value = "documentId") final Integer documentId)
            throws UnsupportedEncodingException {
        return service.processOneDocument(documentId);
    }
}
