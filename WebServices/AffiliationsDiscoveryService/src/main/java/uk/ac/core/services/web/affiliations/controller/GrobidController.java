package uk.ac.core.services.web.affiliations.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.services.web.affiliations.model.grobid.GrobidReport;
import uk.ac.core.services.web.affiliations.service.GrobidExtractionService;

@RestController
public class GrobidController {
    private static final Logger logger = LoggerFactory.getLogger(GrobidController.class);

    private final GrobidExtractionService grobidService;

    @Autowired
    public GrobidController(GrobidExtractionService grobidService) {
        this.grobidService = grobidService;
    }

    @GetMapping("/grobidify/repository/{repo_id}")
    public String processRepository(
            @PathVariable("repo_id")
                    Integer repoId,
            @RequestParam(name = "overwrite", required = false, defaultValue = "false")
                    boolean overwrite
    ) {
        GrobidReport report = this.grobidService.processRepository(repoId, overwrite);
        return new Gson().toJson(report);
    }
}
