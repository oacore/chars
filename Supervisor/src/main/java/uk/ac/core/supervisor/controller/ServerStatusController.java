package uk.ac.core.supervisor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.servlet.NodeStatus;

/**
 *
 * @author mc26486
 */
@RestController
public class ServerStatusController {

    @Autowired
    private NodeStatus nodeStatus;

    @RequestMapping("/status")
    public NodeStatus status() {
        return nodeStatus;
    }
    
    @RequestMapping("/")
    public NodeStatus index() {
        return status();
    }
}
