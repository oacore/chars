package uk.ac.core.common.servlet;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author mc26486
 */
@Component
public class NodeStatus {

    private String name;
    private Boolean running;
    @Autowired
    private ServletCustomization servletCustomization;
    private String systemInformation;

    @PostConstruct
    public void init() {
        this.name = servletCustomization.getNodeName();
        this.systemInformation = servletCustomization.getNodeSystemInformation();
        this.running = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public String getSystemInformation() {
        return systemInformation;
    }

    public void setSystemInformation(String systemInformation) {
        this.systemInformation = systemInformation;
    }

 

}
