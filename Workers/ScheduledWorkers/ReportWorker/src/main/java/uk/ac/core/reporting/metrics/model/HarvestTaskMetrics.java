package uk.ac.core.reporting.metrics.model;

/**
 * Harvest Task Metrics.
 */
public final class HarvestTaskMetrics<T extends HarvestStep> {
    private final T harvestStep;
    private final long completed;
    private final long inQueue;

    public HarvestTaskMetrics(T harvestStep, long completed, long inQueue) {
        this.harvestStep = harvestStep;
        this.completed = completed;
        this.inQueue = inQueue;
    }

    public T getHarvestStep() {
        return harvestStep;
    }

    public long getCompleted() {
        return completed;
    }

    public long getInQueue() {
        return inQueue;
    }
}