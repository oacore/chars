package uk.ac.core.queue.json.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ChannelDetails {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("number")
    @Expose
    private long number;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("connection_name")
    @Expose
    private String connectionName;
    @SerializedName("peer_port")
    @Expose
    private long peerPort;
    @SerializedName("peer_host")
    @Expose
    private String peerHost;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ChannelDetails() {
    }

    /**
     * 
     * @param peerHost
     * @param name
     * @param number
     * @param connectionName
     * @param user
     * @param peerPort
     */
    public ChannelDetails(String name, long number, String user, String connectionName, long peerPort, String peerHost) {
        super();
        this.name = name;
        this.number = number;
        this.user = user;
        this.connectionName = connectionName;
        this.peerPort = peerPort;
        this.peerHost = peerHost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public long getPeerPort() {
        return peerPort;
    }

    public void setPeerPort(long peerPort) {
        this.peerPort = peerPort;
    }

    public String getPeerHost() {
        return peerHost;
    }

    public void setPeerHost(String peerHost) {
        this.peerHost = peerHost;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("number", number).append("user", user).append("connectionName", connectionName).append("peerPort", peerPort).append("peerHost", peerHost).toString();
    }

}
