package uk.ac.core.eventscheduler.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.eventscheduler.service.CrossrefReharvestingMDService;
import uk.ac.core.eventscheduler.service.CrossrefReharvestingMEService;

import java.text.ParseException;

@RestController
@RequestMapping("/reharvesting")
public class ReharvestingController {

    private final CrossrefReharvestingMDService mdService;
    private final CrossrefReharvestingMEService meService;

    public ReharvestingController(CrossrefReharvestingMDService mdService,
                                  CrossrefReharvestingMEService meService) {
        this.mdService = mdService;
        this.meService = meService;
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/metadata_download/{fromDate}/{toDate}"})
    public String metadataDownload(@PathVariable(value = "fromDate") final String fromDateStr,
                       @PathVariable(value = "toDate") final String toDateStr) throws ParseException {
        mdService.rerun(fromDateStr, toDateStr);

        return "Scheduled";
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/metadata_extract/{fromDate}/{toDate}"})
    public String metadataExtract(@PathVariable(value = "fromDate") final String fromDateStr,
                                   @PathVariable(value = "toDate") final String toDateStr) throws ParseException {
        meService.rerun(fromDateStr, toDateStr);

        return "Scheduled";
    }
}
