package uk.ac.core.dataprovider.api.controller.dataprovider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.dataprovider.api.annotation.documentation.DeleteDocumentation;
import uk.ac.core.dataprovider.api.annotation.documentation.GetDocumentation;
import uk.ac.core.dataprovider.api.annotation.documentation.PatchDocumentation;
import uk.ac.core.dataprovider.api.annotation.documentation.PostDocumentation;
import uk.ac.core.dataprovider.api.model.dataprovider.CompactDataProviderResponse;
import uk.ac.core.dataprovider.api.model.dataprovider.PostDataProviderRequest;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.dto.SyncResult;
import uk.ac.core.dataprovider.logic.exception.DataProviderDuplicateException;
import uk.ac.core.dataprovider.logic.exception.DataProviderNotFoundException;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.slack.client.SlackWebhookService;
import uk.ac.core.supervisor.client.SupervisorClient;

import javax.validation.Valid;
import java.util.List;

import static uk.ac.core.dataprovider.api.converter.DataProviderResourceConverter.*;

@RequestMapping("/dataproviders")
@RestController
public class DataproviderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataproviderController.class);

    private final DataProviderService dataProviderService;
    private final ObjectMapper objectMapper;

    public DataproviderController(DataProviderService dataProviderService, ObjectMapper objectMapper) {
        this.dataProviderService = dataProviderService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @PostDocumentation
    public ResponseEntity<CompactDataProviderResponse> addDataprovider(@RequestBody @Valid PostDataProviderRequest postRequest) throws DataProviderDuplicateException {
        DataProviderBO dataProviderBO = dataProviderService.save(toDataProviderBO(postRequest));
        return ResponseEntity.ok().body(toCompactDataProviderResponse(dataProviderBO));
    }

    @PatchMapping("/{id}")
    @PatchDocumentation
    public ResponseEntity<CompactDataProviderResponse> updateDataprovider(@PathVariable @ApiParam(value = "id of dataprovider to patch", required = true) Long id,
                                                                          @RequestBody @Valid JsonPatch patchRequest) throws DataProviderNotFoundException, JsonPatchException, JsonProcessingException {

        DataProviderBO dataProvider = dataProviderService.findById(id);

        return ResponseEntity.ok(
                toCompactDataProviderResponse(
                        dataProviderService.update(applyPatchToDataProvider(patchRequest, dataProvider))
                )
        );
    }

    private DataProviderBO applyPatchToDataProvider(
            JsonPatch patch, DataProviderBO dataProvider) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(dataProvider, JsonNode.class));
        return objectMapper.treeToValue(patched, DataProviderBO.class);
    }

    @GetMapping("/{id}")
    @GetDocumentation
    public ResponseEntity<CompactDataProviderResponse> getDataProvider(@PathVariable("id") long id) throws DataProviderNotFoundException {
        DataProviderBO dataProviderBO = dataProviderService.findById(id);
        return ResponseEntity.ok(toCompactDataProviderResponseWithEnabledStatus(dataProviderBO));
    }

    @PostMapping("/all")
    @ApiOperation("Gets all dataproviders")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request was successful"),
            @ApiResponse(code = 405, message = "Method not allowed")
    })
    public ResponseEntity<Page<DataProviderBO>> getAllDataProviders(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                                    @RequestParam Integer size,
                                                                    @RequestParam(required = false, defaultValue = "true") @ApiParam(value = "The status of dataproviders to fetch based on whether they are enabled") Boolean enabled,
                                                                    @RequestParam(required = false, defaultValue = "false") @ApiParam(value = "The status of dataproviders to fetch based on whether they are journals") Boolean journals) {
        Page<DataProviderBO> dataProviders = dataProviderService.findAll(PageRequest.of(page, size), journals, enabled);
        return ResponseEntity.ok(dataProviders);
    }

    @GetMapping("/findduplicates")
    @ApiOperation("Find all duplicated dataproviders")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request was successful"),
            @ApiResponse(code = 405, message = "Method not allowed")
    })
    public ResponseEntity<List<DataProviderBO>> getFindDuplicateDataProviders(@RequestParam(required = true) Long repositoryId) throws DataProviderNotFoundException {
        DataProviderBO dataProviderBO = dataProviderService.findById(repositoryId);
        List<DataProviderBO> duplicateDataProviders = dataProviderService.findDuplicateRepositories(dataProviderBO);
        return ResponseEntity.ok(duplicateDataProviders);
    }

    @GetMapping("/disableduplicates/{repositoryId}")
    @ApiOperation("Disable duplicated dataproviders")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request was successful"),
            @ApiResponse(code = 405, message = "Method not allowed")
    })
    public ResponseEntity<String> disableDuplicateDataProviders(@PathVariable Long repositoryId) throws DataProviderNotFoundException {
        int updatedRecords = dataProviderService.disableDataProviderDuplicates(repositoryId);

        return ResponseEntity.ok("Updated " + updatedRecords + " records");
    }

    @GetMapping("/disableallduplicates")
    @ApiOperation("Disable all duplicates")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request was successful"),
            @ApiResponse(code = 405, message = "Method not allowed")
    })
    public ResponseEntity<String> disableAllDuplicates() throws DataProviderNotFoundException {
        int updatedRecords = dataProviderService.disableAllDuplicates();
        return ResponseEntity.ok("Updated " + updatedRecords + " records");
    }

    @DeleteMapping("/{id}")
    @DeleteDocumentation
    public ResponseEntity<CompactDataProviderResponse> deleteDataProvider(@PathVariable Long id) throws DataProviderNotFoundException {
        dataProviderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sync")
    @ApiOperation("Syncs the Database and the index")
    public ResponseEntity<SyncResult> syncAllRepositoriesToIndex() {
        return ResponseEntity.ok(dataProviderService.syncAll());
    }

    @PostMapping("/sync/{id}")
    @ApiOperation("Syncs a single repository from the index")
    public ResponseEntity<String> syncRepositoryToIndex(@PathVariable("id") Long id) throws DataProviderNotFoundException {
        dataProviderService.syncOne(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/slack")
    public ResponseEntity<String> testSlackNotification() {
        SlackWebhookService.sendMessage("Test message from production", "data-provider-report");
        return ResponseEntity.ok().build();
    }
}
