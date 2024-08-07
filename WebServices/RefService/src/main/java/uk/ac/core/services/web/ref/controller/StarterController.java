package uk.ac.core.services.web.ref.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.ac.core.services.web.ref.services.RefReportService;

import java.io.IOException;


/**
 *
 * @author mc26486
 */
@RestController
public class StarterController {


    @Autowired
    RefReportService refReportService;

    @RequestMapping("/generateRefReport")
    public void generateRefReport() throws IOException {
        refReportService.run();
    }

    @RequestMapping("/refReport")
    public String generateRefReport( @RequestParam(value = "doi", defaultValue = "") String doi) throws IOException {
        return refReportService.getReportInfo(doi).toString();
    }

    @RequestMapping("/generateFullTextRefReport")
    public void generateFullTextRefReport() throws IOException {
        refReportService.generateFullTextReport();
    }

}
