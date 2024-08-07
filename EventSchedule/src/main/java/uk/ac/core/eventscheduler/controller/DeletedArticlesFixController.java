package uk.ac.core.eventscheduler.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.ac.core.eventscheduler.database.DeletedArticlesFixDao;
import uk.ac.core.eventscheduler.periodic.DeletedArticlesFixService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class used to be doing the next job:
 * <p>
 *     Get batch of documents that have deleted status and check if it's `truly` deleted.
 * </p>
 * <p>
 *     The reason this job launched is a bug in CORE data.
 *     By now, March 5, 2024, the bug seems to be gone.
 *     That's why the periodic task is no longer needed.
 * </p>
 */
@Deprecated
@RestController
@RequestMapping("/deleted-articles-fix/")
public class DeletedArticlesFixController {
    private static final Logger log = LoggerFactory.getLogger(DeletedArticlesFixController.class);

    private final DeletedArticlesFixService service;
    private final DeletedArticlesFixDao dao;

    @Autowired
    public DeletedArticlesFixController(DeletedArticlesFixService service, DeletedArticlesFixDao dao) {
        this.service = service;
        this.dao = dao;
    }

    @GetMapping("single-article/{docId}")
    public String fixSingleDocument(@PathVariable("docId") int docId) {
        this.service.resetNumbers();
        this.service.process(Collections.singletonList(docId));
        return this.generateResponse();
    }

    @GetMapping("batch")
    public String fixArticlesBatch(
            @RequestParam(name = "size", required = false, defaultValue = "200000")
            Integer size) {
        this.service.resetNumbers();
        this.service.process(this.dao.getBatch(size));
        return this.generateResponse();
    }

    private String generateResponse() {
        Map<String, String> map = new HashMap<>();
        map.put("total", String.valueOf(this.service.getTotalCount()));
        map.put("fixed", String.valueOf(this.service.getFixedCount()));
        map.put("deleted", String.valueOf(this.service.getDeletedCount()));
        map.put("failed", String.valueOf(this.service.getFailedCount()));
        return new Gson().toJson(map);
    }
}
