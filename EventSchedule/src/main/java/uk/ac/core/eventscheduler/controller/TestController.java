package uk.ac.core.eventscheduler.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.queue.QueueInfoService;
import uk.ac.core.supervisor.client.SupervisorClient;

/**
 *
 * @author lucasanastasiou
 */
@RestController
public class TestController {

    @Autowired
    QueueInfoService queueInfoService;
    
    @Autowired
    SupervisorClient supervisorClient;
    
    private static final String QUEUE = "harvest-queue";

    @RequestMapping("/test/{var}")
    public String test(@PathVariable(value = "var") String var) {
        String message = "";
        
        try {
            supervisorClient.sendHarvestRepositoryRequest(1);
        } catch (CHARSException ex) {
            Logger.getLogger(TestController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return message;
    }
}
