package uk.ac.core.dataprovider.api.controller.internal_dedup;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.dataprovider.api.model.internal_dedup.DeduplicationReport;
import uk.ac.core.dataprovider.api.service.Internal_Dedup.InternalDedupService;

@RestController
public class InternalDedupController {
    private static final Logger log = LoggerFactory.getLogger(InternalDedupController.class);

    @Autowired
    InternalDedupService internalDedupService;

    @GetMapping("/internal_dedup/{repo_id}")
    public String internalDedup(
            @PathVariable("repo_id")
            int repoId,
            @RequestParam(required = false, defaultValue = "0.75", name = "conf")
            double confidence,
            @RequestParam(required = false, defaultValue = "true", name = "internal_duplicates")
            boolean internalDuplicates,
            @RequestParam(required = false, defaultValue = "false", name = "refresh")
            boolean forceCacheRefresh) {

        DeduplicationReport report = internalDedupService.generateReport(repoId, confidence, internalDuplicates, forceCacheRefresh);

        log.info("Report generated");

        return new Gson().toJson(report);
    }
}
