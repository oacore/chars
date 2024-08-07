package uk.ac.core.worker.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.WorkerProgress;

/**
 *
 * @author lucasanastasiou
 */
@RestController
public class ProgressController {

    @Autowired
    WorkerProgress workerProgress;

    @Autowired
    QueueWorker worker;

    @RequestMapping(path = "/progress", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String status() {
        return new Gson().toJson(this.workerProgress);
    }
}
