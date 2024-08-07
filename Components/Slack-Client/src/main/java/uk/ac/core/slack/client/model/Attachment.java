package uk.ac.core.slack.client.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

public class Attachment {

    @SerializedName("fallback")
    @Expose
    public String fallback;
    @SerializedName("author_name")
    @Expose
    public String authorName;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("color")
    @Expose
    public String color;

    @SerializedName("actions")
    @Expose
    public List<Action> actions = null;

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

}
