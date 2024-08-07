package uk.ac.core.services.web.affiliations.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.ac.core.services.web.affiliations.exception.RequestPreparationException;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryRequest;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponse;
import uk.ac.core.services.web.affiliations.model.InputMetadata;
import uk.ac.core.services.web.affiliations.service.AffiliationsDiscoveryService;
import uk.ac.core.services.web.affiliations.service.InternalMetadataConverter;

import java.util.Date;

@RestController
@RequestMapping("/affiliations")
public class StarterController {
    private static final Logger log = LoggerFactory.getLogger(StarterController.class);

    private final AffiliationsDiscoveryService service;
    private final InternalMetadataConverter converter;

    @Autowired
    public StarterController(AffiliationsDiscoveryService service, InternalMetadataConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @PostMapping("/extract")
    public String extractAffiliationsWithRequestBody(@RequestBody InputMetadata metadata) {
        if (!this.isMetadataValid(metadata)) {
            return new Gson().toJson(this.noInputMetadataResponse());
        }
        AffiliationsDiscoveryRequest request;
        try {
            request = this.converter.getRequest(metadata);
        } catch (RequestPreparationException e) {
            return new Gson().toJson(this.requestPreparationFailureResponse(e));
        }
        AffiliationsDiscoveryResponse response = this.service.extract(request);
        return new Gson().toJson(response);
    }

    private AffiliationsDiscoveryResponse requestPreparationFailureResponse(Exception e) {
        AffiliationsDiscoveryResponse response = new AffiliationsDiscoveryResponse();
        response.setMessage(e.getMessage());
        response.setDateCreated(new Date());
        response.setCount(0);
        return response;
    }

    private AffiliationsDiscoveryResponse noInputMetadataResponse() {
        AffiliationsDiscoveryResponse response = new AffiliationsDiscoveryResponse();
        response.setMessage("Invalid metadata: the required fields are not provided");
        response.setDateCreated(new Date());
        response.setCount(0);
        return response;
    }

    private boolean isMetadataValid(InputMetadata metadata) {
        boolean hasCoreId = metadata.getCoreId() != null;
        boolean hasOai = metadata.getOai() != null;
        boolean hasDoi = metadata.getDoi() != null;
        boolean hasTitleAndYear = metadata.getTitle() != null && metadata.getYear() != null;
        return hasCoreId || hasDoi || hasOai || hasTitleAndYear;
    }

    @GetMapping("/extract/grobid/{repo_id}/{core_id}")
    public String extractUsingGrobid(
            @PathVariable("repo_id") Integer repoId,
            @PathVariable("core_id") Integer coreId) {
        try {
            AffiliationsDiscoveryRequest request = this.converter.getRequest(new InputMetadata(
                    coreId, repoId, null, null, null, null));
            AffiliationsDiscoveryResponse response = this.service.extractWithGrobid(request);
            return new Gson().toJson(response);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return new Gson().toJson(this.requestPreparationFailureResponse(e));
        }
    }

    @GetMapping("/extract/regex/{repo_id}/{core_id}")
    public String extractUsingRegex(
            @PathVariable("repo_id") Integer repoId,
            @PathVariable("core_id") Integer coreId) {
        try {
            AffiliationsDiscoveryRequest request = this.converter.getRequest(new InputMetadata(
                    coreId, repoId, null, null, null, null));
            AffiliationsDiscoveryResponse response = this.service.extractWithRegex(request);
            return new Gson().toJson(response);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return new Gson().toJson(this.requestPreparationFailureResponse(e));
        }
    }

    @GetMapping("/extract/orcid-api/{repo_id}/{core_id}")
    public String extractUsingOrcidApi(
            @PathVariable("repo_id") Integer repoId,
            @PathVariable("core_id") Integer coreId) {
        try {
            AffiliationsDiscoveryRequest request = this.converter.getRequest(new InputMetadata(
                    coreId, repoId, null, null, null, null));
            AffiliationsDiscoveryResponse response = this.service.extractWithOrcid(request);
            return new Gson().toJson(response);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return new Gson().toJson(this.requestPreparationFailureResponse(e));
        }
    }

    @GetMapping("/extract/crossref-api/{repo_id}/{core_id}")
    public String extractUsingCrossrefApi(
            @PathVariable("repo_id") Integer repoId,
            @PathVariable("core_id") Integer coreId) {
        try {
            AffiliationsDiscoveryRequest request = this.converter.getRequest(new InputMetadata(
                    coreId, repoId, null, null, null, null));
            AffiliationsDiscoveryResponse response = this.service.extractWithCrossref(request);
            return new Gson().toJson(response);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return new Gson().toJson(this.requestPreparationFailureResponse(e));
        }
    }

    @GetMapping("/extract/openalex-api/{repo_id}/{core_id}")
    public String extractUsingOpenAlexApi(
            @PathVariable("core_id") Integer coreId,
            @PathVariable("repo_id") Integer repoId
    ) {
        try {
            AffiliationsDiscoveryRequest request = this.converter.getRequest(new InputMetadata(
                    coreId, repoId, null, null, null, null));
            AffiliationsDiscoveryResponse response = this.service.extractWithOpenAlex(request);
            return new Gson().toJson(response);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return new Gson().toJson(this.requestPreparationFailureResponse(e));
        }
    }
}
