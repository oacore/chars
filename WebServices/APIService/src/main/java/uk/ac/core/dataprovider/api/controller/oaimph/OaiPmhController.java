package uk.ac.core.dataprovider.api.controller.oaimph;

import ORG.oclc.oai.harvester2.verb.Identify;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.http.conn.HttpHostConnectException;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.dataprovider.api.model.OaiPmhEndpointResponse;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.entity.IdentifyResponse;
import uk.ac.core.dataprovider.logic.exception.DataProviderDuplicateException;
import uk.ac.core.dataprovider.logic.exception.OaiPmhEndpointNotFoundException;
import uk.ac.core.dataprovider.logic.exception.OaiPmhInvalidException;
import uk.ac.core.dataprovider.logic.service.oaipmhdiscovery.OaiPmhEndpointService;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@Validated
@RequestMapping("/oaipmh")
public class OaiPmhController {

    private static final String MOCK_OAI_PMH = "http://for.test.only/oai2/pmh";

    private final OaiPmhEndpointService oaiPmhEndpointService;
    private final DataProviderService dataProviderService;

    public OaiPmhController(OaiPmhEndpointService oaiPmhEndpointService, DataProviderService dataProviderService) {
        this.oaiPmhEndpointService = oaiPmhEndpointService;
        this.dataProviderService = dataProviderService;
    }

    @GetMapping
    @ApiOperation("Finds OAI-PMH endpoint for the given URL")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OAI-PMH endpoint was found", response = OaiPmhEndpointResponse.class),
            @ApiResponse(code = 422, message = "Wrong type of path variable/query parameter"),
            @ApiResponse(code = 400, message = "A data provider doesn't seem to have an OAI-PMH endpoint"),
            @ApiResponse(code = 405, message = "Method not allowed")
    })
    public ResponseEntity<OaiPmhEndpointResponse> findOaiPmhEndpoint(@URL @RequestParam @NotNull @NotBlank String url) throws OaiPmhEndpointNotFoundException, DataProviderDuplicateException, OaiPmhInvalidException {
        if (url.equals(MOCK_OAI_PMH)) {
            IdentifyResponse ir = new IdentifyResponse(
                    "Mock Repo",
                    "mock.repo@mock.ac.uk",
                    MOCK_OAI_PMH);
            return ResponseEntity.ok(new OaiPmhEndpointResponse(ir));
        }
        String finalUrl;
        try {
            finalUrl = this.oaiPmhEndpointService.checkHostUrlForAccessibility(url).getUri();
            if (finalUrl != null) {
                DataProviderBO dataProviderBO = new DataProviderBO();
                dataProviderBO.setOaiPmhEndpoint(url);
                dataProviderBO.setJournal(false);
                dataProviderBO.setName("");
                List<DataProviderBO> duplicateRepositories = this.dataProviderService.findDuplicateRepositories(dataProviderBO);
                if (duplicateRepositories.size() != 0) {
                    throw new DataProviderDuplicateException(duplicateRepositories);
                }

                IdentifyResponse response = null;
                try {
                    Identify identify = new Identify(url);
                    finalUrl = url;

                    response = new IdentifyResponse(
                            identify.getDocument().getElementsByTagName("repositoryName").item(0).getTextContent(),
                            identify.getDocument().getElementsByTagName("adminEmail").item(0).getTextContent(),
                            finalUrl
                    );
                } catch (Exception e) {
                    // gobble: force endpoint detection upon failure
                    response = null;
                }

                if (response == null) {
                    response = oaiPmhEndpointService.findOaiPmhEndpoint(url).orElseThrow(OaiPmhEndpointNotFoundException::new);
                }

                return ResponseEntity.ok(new OaiPmhEndpointResponse(response));
            } else {
                throw new OaiPmhInvalidException();
            }
        } catch (HttpHostConnectException e) {
            // prettify error message
            if (e.getMessage().contains("443") && e.getMessage().contains("Connection refused")) {
                throw new OaiPmhInvalidException("Connection to " + e.getHost() + " failed. Try again replacing https:// with http://", e);
            } else {
                throw new OaiPmhInvalidException(e.getMessage(), e);
            }
        } catch (IOException e) {
            throw new OaiPmhInvalidException(e.getMessage(), e);
        }

    }
}
