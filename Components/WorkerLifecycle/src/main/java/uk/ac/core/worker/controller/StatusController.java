package uk.ac.core.worker.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.WorkerStatus;

/**
 *
 * @author mc26486
 */
@RestController
public class StatusController {

    @Autowired
    WorkerStatus workerStatus;

    @Autowired
    QueueWorker worker;

    @Value("${logs.path}")
    private String basePath;

    @RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String index() {
        return status();
    }

    @RequestMapping(path = "/start", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String start() {
        this.worker.start();
        return new Gson().toJson(workerStatus);
    }

    @RequestMapping(path = "/stop", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String stop() {
        this.worker.stop();
        System.exit(0);
        return new Gson().toJson(workerStatus);

    }

    @RequestMapping(path = "/drop", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String drop() {
        this.worker.drop();
        return this.stop();
    }

    @RequestMapping(path = "/pause", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String pause() {
        this.worker.pause();
        return new Gson().toJson(workerStatus);

    }

    @RequestMapping(path = "/status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String status() {
        return new Gson().toJson(this.workerStatus);
    }

    @RequestMapping(path = "/log", produces = MediaType.TEXT_PLAIN_VALUE)
    public FileSystemResource log() {
        String taskLogPath = basePath + "/tasks/" + worker.getCurrentWorkingTask().getUniqueId() + ".log";
        return new FileSystemResource(taskLogPath);
    }

    @RequestMapping(path = "/tail/{size}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String tail(@PathVariable(value = "size") int size) {
        String taskLogPath = basePath + "/tasks/" + worker.getCurrentWorkingTask().getUniqueId() + ".log";
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec("tail -" + size + " " + taskLogPath);
            java.io.BufferedReader input = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line;
            //Here we first read the next line into the variable
            //line and then check for the EOF condition, which
            //is the return value of null
            while ((line = input.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (java.io.IOException e) {
        }
        return stringBuilder.toString();
    }
}
