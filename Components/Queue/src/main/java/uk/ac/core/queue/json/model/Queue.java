package uk.ac.core.queue.json.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Queue {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("vhost")
    @Expose
    private String vhost;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Queue() {
    }

    /**
     * 
     * @param vhost
     * @param name
     */
    public Queue(String name, String vhost) {
        super();
        this.name = name;
        this.vhost = vhost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("vhost", vhost).toString();
    }

}
