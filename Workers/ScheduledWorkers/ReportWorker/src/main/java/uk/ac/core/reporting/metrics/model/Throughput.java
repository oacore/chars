package uk.ac.core.reporting.metrics.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Class, which contains throughput metrics values.
 */
public final class Throughput {

    private final Map<HarvestStep, HarvestTaskMetrics<HarvestStep>> harvestMetrics = new HashMap<>();

    public Throughput() {

    }

    public Throughput addHarvestMetrics(HarvestTaskMetrics<HarvestStep> harvestTaskMetrics) {
        this.harvestMetrics.put(harvestTaskMetrics.getHarvestStep(), harvestTaskMetrics);
        return this;
    }

    public Map<HarvestStep, HarvestTaskMetrics<HarvestStep>> getHarvestMetrics() {
        return new HashMap<>(harvestMetrics);
    }
}