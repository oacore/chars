package uk.ac.core.queue.json.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Consumer {

    @SerializedName("channel_details")
    @Expose
    private ChannelDetails channelDetails;
    @SerializedName("queue")
    @Expose
    private Queue queue;
    @SerializedName("consumer_tag")
    @Expose
    private String consumerTag;
    @SerializedName("exclusive")
    @Expose
    private boolean exclusive;
    @SerializedName("ack_required")
    @Expose
    private boolean ackRequired;
    @SerializedName("prefetch_count")
    @Expose
    private long prefetchCount;
    @SerializedName("arguments")
    @Expose
    private Arguments arguments;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Consumer() {
    }

    /**
     * 
     * @param queue
     * @param ackRequired
     * @param channelDetails
     * @param arguments
     * @param exclusive
     * @param prefetchCount
     * @param consumerTag
     */
    public Consumer(ChannelDetails channelDetails, Queue queue, String consumerTag, boolean exclusive, boolean ackRequired, long prefetchCount, Arguments arguments) {
        super();
        this.channelDetails = channelDetails;
        this.queue = queue;
        this.consumerTag = consumerTag;
        this.exclusive = exclusive;
        this.ackRequired = ackRequired;
        this.prefetchCount = prefetchCount;
        this.arguments = arguments;
    }

    public ChannelDetails getChannelDetails() {
        return channelDetails;
    }

    public void setChannelDetails(ChannelDetails channelDetails) {
        this.channelDetails = channelDetails;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public String getConsumerTag() {
        return consumerTag;
    }

    public void setConsumerTag(String consumerTag) {
        this.consumerTag = consumerTag;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isAckRequired() {
        return ackRequired;
    }

    public void setAckRequired(boolean ackRequired) {
        this.ackRequired = ackRequired;
    }

    public long getPrefetchCount() {
        return prefetchCount;
    }

    public void setPrefetchCount(long prefetchCount) {
        this.prefetchCount = prefetchCount;
    }

    public Arguments getArguments() {
        return arguments;
    }

    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("channelDetails", channelDetails).append("queue", queue).append("consumerTag", consumerTag).append("exclusive", exclusive).append("ackRequired", ackRequired).append("prefetchCount", prefetchCount).append("arguments", arguments).toString();
    }

}
