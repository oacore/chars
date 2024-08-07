package uk.ac.core.notification.services.email;

import java.util.Map;
import org.springframework.core.io.Resource;

/**
 *
 * @author lucasanastasiou
 */
public class Mail {

    private String from;
    private String[] to;
    private String subject;
    private String content;
    private String htmlTemplateName;
    private Map<String,String> variables;
    private Map<String,Resource> inlineImages;

    public Mail() {
    }
    
    public Mail(String from, String[] to, String subject, String content, String htmlTemplateName, Map<String, String> variables, Map<String, Resource> inlineImages) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.htmlTemplateName = htmlTemplateName;
        this.variables = variables;
        this.inlineImages = inlineImages;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHtmlTemplateName() {
        return htmlTemplateName;
    }

    public void setHtmlTemplateName(String htmlTemplateName) {
        this.htmlTemplateName = htmlTemplateName;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

    public Map<String, Resource> getInlineImages() {
        return inlineImages;
    }

    public void setInlineImages(Map<String, Resource> inlineImages) {
        this.inlineImages = inlineImages;
    }

    @Override
    public String toString() {
        return "Mail{" + "from=" + from + ", to=" + to + ", subject=" + subject + ", content=" + content + ", htmlTemplateName=" + htmlTemplateName + ", variables=" + variables + ", inlineImages=" + inlineImages + '}';
    }
    
    
}