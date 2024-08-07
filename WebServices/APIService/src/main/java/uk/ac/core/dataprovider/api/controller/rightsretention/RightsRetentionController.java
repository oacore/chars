package uk.ac.core.dataprovider.api.controller.rightsretention;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.core.dataprovider.api.model.rightsretention.HighlightedArticleMetadata;
import uk.ac.core.dataprovider.api.model.rightsretention.ReportedArticleMetadata;
import uk.ac.core.dataprovider.api.service.rightsretention.RightsRetentionService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rights-retention")
public class RightsRetentionController {
    private static final Logger log = LoggerFactory.getLogger(RightsRetentionController.class);

    private final RightsRetentionService service;

    @Autowired
    public RightsRetentionController(RightsRetentionService service) {
        this.service = service;
    }

    @ApiOperation("Finds RRS articles from specified repository")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Found RRS articles"),
            @ApiResponse(code = 204, message = "Found no RRS articles")
    })
    @GetMapping("/find/{repoId}")
    public ResponseEntity<List<ReportedArticleMetadata>> findAndValidate(
            @PathVariable("repoId") Integer repositoryId,
            @RequestParam(name = "set", required = false) String harvestingSet
    ) {
        log.info("Searching for Rights Retention articles in repository {}", repositoryId);

        List<HighlightedArticleMetadata> potentialCandidates =
                this.service.findPotentialArticles(repositoryId, harvestingSet);

        List<ReportedArticleMetadata> validatedArticles =
                this.service.validatePotentialArticles(potentialCandidates);

        if (validatedArticles.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }
        log.info("Validated articles count: {}", validatedArticles.size());
        return ResponseEntity.ok(validatedArticles);
    }

    @ApiOperation("Checks whether the external file supports RRS or not")
    @ApiResponses({
            @ApiResponse(code = 200, message = "File accepted, saved and processed successfully"),
            @ApiResponse(code = 500, message = "I/O error occurred during operations with the file")
    })
    @RequestMapping(
            value = "/upload-file",
            method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity uploadAndValidateExternalFile(
            @RequestParam("file") MultipartFile multipartFile) {
        Map<String, String> map = new HashMap<>();
        try {
            String tmpPdfPath = "/tmp/".concat(
                    multipartFile.getOriginalFilename() == null
                            ? "rrs-file-" + System.currentTimeMillis()
                            : multipartFile.getOriginalFilename()
            );
            File pdf = new File(tmpPdfPath);

            FileOutputStream fos = new FileOutputStream(pdf);
            fos.write(multipartFile.getBytes());
            fos.close();

            log.info("External file successfully saved - {}", pdf.getPath());

            ReportedArticleMetadata ram = this.service.validateExternalFile(pdf);

            log.info("Done validating");

            return ResponseEntity.ok(ram);
        } catch (IOException e) {
            log.error("IOException caught while fetching the file", e);
            map.put("message", "IOException caught while fetching the file");
            map.put("exceptionMsg", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(map);
        }
    }
}
