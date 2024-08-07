package uk.ac.core.dataprovider.api.controller.duplicate;


import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.dataprovider.api.service.DuplicatesService;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/duplicate")
public class DuplicateController {

    @Autowired
    DuplicatesService duplicatesService;

    @GetMapping("/versions")
    public Set<Integer> findDuplicates(@RequestParam("id") Integer documentId) {
        return duplicatesService.getDuplicates(documentId);
    }

    @GetMapping("/precision")
    public Pair<Double, Double> calculatePrecision(){
        return duplicatesService.processDataFromFile();
    }


    @GetMapping("/generateCsv")
    public void generateDuplicatesCsv() throws IOException {
        duplicatesService.generateDuplicatesCsv();
    }

    @GetMapping("/precisionOnDataset")
    public Pair<Double, Double> calculatePrecisionOnDataset(Integer startFrom) throws IOException{
        return duplicatesService.calculateRecallForDataset();
    }

    @GetMapping("/generateDatasetCsv")
    public void generateDatasetDuplicatesCsv() throws IOException {
        duplicatesService.generateDatasetCsv();
    }
}
