package uk.ac.core.reporting.metrics.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import uk.ac.core.reporting.metrics.model.DocumentDownloadStats;
import uk.ac.core.reporting.metrics.model.HarvestStep;
import uk.ac.core.reporting.metrics.model.HarvestTaskMetrics;
import uk.ac.core.reporting.metrics.model.Throughput;
import java.util.Map;
import java.util.Objects;

/**
 * Statistics per day, specifically from yesterday till today.
 */
public final class StatsPerDay {

    @JsonProperty("indexed_documents")
    private Long indexedDocumentsCount;

    @JsonProperty("downloaded_documents_stats")
    private final DocumentDownloadStats documentDownloadStats;

    @JsonIgnore
    private final Throughput throughput;

    public StatsPerDay(@Nullable Long indexedDocumentsCount, @Nullable DocumentDownloadStats documentDownloadStats, Throughput throughput) {
        Objects.requireNonNull(throughput);
        this.indexedDocumentsCount = indexedDocumentsCount;
        this.documentDownloadStats = documentDownloadStats;
        this.throughput = throughput;
    }

    public long getCompletedHarvestTasks(HarvestStep harvestStep) {
        return getHarvestTaskMetrics(harvestStep).getCompleted();
    }

    public long getHarvestTasksInQueue(HarvestStep harvestStep) {
        return getHarvestTaskMetrics(harvestStep).getInQueue();
    }

    private HarvestTaskMetrics<? extends HarvestStep> getHarvestTaskMetrics(HarvestStep harvestStep) {
        Throughput throughput = getThroughput();
        Map<HarvestStep, HarvestTaskMetrics<HarvestStep>> harvestMetrics = throughput.getHarvestMetrics();
        return harvestMetrics.get(harvestStep);
    }

    public Long getIndexedDocumentsCount() {
        return indexedDocumentsCount;
    }

    public DocumentDownloadStats getDocumentDownloadStats() {
        return documentDownloadStats;
    }

    public Throughput getThroughput() {
        return this.throughput;
    }

}
