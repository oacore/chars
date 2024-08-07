package uk.ac.core.supervisor.controller.workers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.supervisor.controller.workers.service.WorkerCreationService;
import uk.ac.core.supervisor.model.WorkerCreationStatus;

/**
 *
 * @author lucasanastasiou
 */
@RestController
@RequestMapping("/workers")
public class WorkerCreationController {

    @Autowired
    WorkerCreationService workerCreationService;
    
    @RequestMapping("/start/{workerType}")
    public WorkerCreationStatus createNew(@PathVariable String workerType){
        
        
        
        
        int result = workerCreationService.createNew(TaskType.fromString(workerType));
        
        WorkerCreationStatus wcs = new WorkerCreationStatus();
        wcs.setSuccess(result>=0);
        wcs.setPid(1);
        wcs.setDuration(1);
        wcs.setMessage("Message");
        
        return wcs;
    }
}
