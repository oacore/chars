package uk.ac.core.datawarehouse.report.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author lucas
 */
@Controller
public class DataWarehouseReportController {

    @Autowired
    DataWarehouseReportWorker dataWarehouseReportWorker;

    @RequestMapping(value = "/datawarehouse", method = RequestMethod.GET)
    public ResponseEntity<Void> startPorFavor() {
        dataWarehouseReportWorker.scheduledStart();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
