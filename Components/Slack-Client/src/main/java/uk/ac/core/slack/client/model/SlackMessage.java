
package uk.ac.core.slack.client.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SlackMessage {

    @SerializedName("text")
    @Expose
    public String text;
    
    @SerializedName("attachments")
    @Expose
    public List<Attachment> attachments = null;

    public SlackMessage() {}


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
    
}
