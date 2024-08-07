package uk.ac.core.common.servlet;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.SocketUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mc26486
 */
@Component
public class ServletCustomization implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>,
        ApplicationListener<ApplicationStartedEvent> {

    @Value("${node.type}")
    public String nodeType;
    @Value("${node.minPort:8000}")
    public Integer minPort;
    @Value("${node.maxPort:8100}")
    public Integer maxPort;
    @Value("${node.port:0}")
    public Integer customPort;
    @Value("${build.job_name:nojobname}")
    public String buildJobName;
    @Value("${build.git_branch:nobranch}")
    public String buildGitBranch;
    @Value("${build.git_commit:nocommit}")
    public String buildGitCommit;
    public String nodeName;
    @Value("${node.name.suffix:default}")
    public String nodeNameSuffix;
    public String nodeHost;
    public Integer nodePort = 0;
    static final Logger logger = Logger.getLogger(ServletCustomization.class.getName());
    private String nodeSystemInformation;
    
    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        int containerPort;
        logger.log(Level.INFO, "Custom port is {0}", customPort);
        if (customPort != 0) {
            containerPort = customPort;
        } else {
            containerPort = SocketUtils.findAvailableTcpPort(minPort, maxPort);
        }

        factory.setPort(containerPort);
        this.nodePort = containerPort;
        this.nodeName = this.nodeType + "_" + nodeNameSuffix + "@" + this.getNodeHost() + ":" + this.nodePort;
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);
        MDC.put("currentNodeName", this.nodeName);
        StatusPrinter.print(lc);
        logger.log(Level.INFO, "Trying to start node with name = {0}", nodeName);
        nodeSystemInformation = ManagementFactory.getRuntimeMXBean().getName();
        logger.log(Level.INFO, "System info? {0}", nodeSystemInformation);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent e) {
        String localPort = e.getApplicationContext().getEnvironment().getProperty("local.server.port");
        nodePort = Integer.parseInt(localPort);
        try {
            nodeHost = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            logger.log(Level.SEVERE, "Failed to get the host of " + nodeName, ex);
        }
        logger.log(Level.INFO, "Node started = {0}", nodeName);
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeHost() {
        if (nodeHost == null) {
            try {
                nodeHost = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                logger.log(Level.SEVERE, "Failed to get the host of " + nodeName, ex);
            }
        }
        return nodeHost;
    }

    public void setNodeHost(String nodeHost) {
        this.nodeHost = nodeHost;
    }

    public Integer getNodePort() {
        return nodePort;
    }

    public void setNodePort(Integer nodePort) {
        this.nodePort = nodePort;
    }

    public String getNodeSystemInformation() {
        return nodeSystemInformation;
    }

    public void setNodeSystemInformation(String nodeSystemInformation) {
        this.nodeSystemInformation = nodeSystemInformation;
    }

    public Integer getMinPort() {
        return minPort;
    }

    public void setMinPort(Integer minPort) {
        this.minPort = minPort;
    }

    public Integer getMaxPort() {
        return maxPort;
    }

    public void setMaxPort(Integer maxPort) {
        this.maxPort = maxPort;
    }

    public Integer getCustomPort() {
        return customPort;
    }

    public void setCustomPort(Integer customPort) {
        this.customPort = customPort;
    }

    public String getBuildJobName() {
        return buildJobName;
    }

    public void setBuildJobName(String buildJobName) {
        this.buildJobName = buildJobName;
    }

    public String getBuildGitBranch() {
        return buildGitBranch;
    }

    public void setBuildGitBranch(String buildGitBranch) {
        this.buildGitBranch = buildGitBranch;
    }

    public String getBuildGitCommit() {
        return buildGitCommit;
    }

    public void setBuildGitCommit(String buildGitCommit) {
        this.buildGitCommit = buildGitCommit;
    }

    @Override
    public String toString() {
        return "ServletCustomization{" + "nodeType=" + nodeType + ", minPort=" + minPort + ", maxPort=" + maxPort + ", customPort=" + customPort + ", buildJobName=" + buildJobName + ", buildGitBranch=" + buildGitBranch + ", buildGitCommit=" + buildGitCommit + ", nodeName=" + nodeName + ", nodeHost=" + nodeHost + ", nodePort=" + nodePort + ", nodeSystemInformation=" + nodeSystemInformation + '}';
    }

}
