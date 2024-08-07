/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.workers.item.index.ItemIndexService;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author mc26486
 */
@RestController
public class StarterController {
    @Autowired
    private ItemIndexService service;

    @RequestMapping("/index_item/{documentId}")
    public String indexItemController(@PathVariable(value = "documentId") final Integer documentId) throws UnsupportedEncodingException {
        return service.indexOneDocument(documentId);
    }

    @RequestMapping("/index_item/repository/{id}")
    public String indexRepositoryController(@PathVariable(value = "id") final Integer repositoryId) throws UnsupportedEncodingException {
        return service.indexRepository(repositoryId);
    }

}
