package uk.ac.core.reporting.metrics.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.core.reporting.metrics.TaskUpdateMetric;
import uk.ac.core.reporting.metrics.model.DocumentDownloadStats;
import uk.ac.core.reporting.metrics.model.HarvestStep;
import uk.ac.core.reporting.metrics.model.HarvestTaskMetrics;
import uk.ac.core.reporting.metrics.model.Throughput;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Global metrics BO.
 */
public final class CompleteGlobalMetricsBO {
    private final OverallStats overallStats;
    private final StatsPerDay statsPerDay;

    @JsonIgnore
    private final LocalDateTime createdAt;

    @JsonIgnore
    private TaskUpdateMetric taskUpdateMetric;

    @JsonIgnore
    private List<BigRepositoryMetric> bigRepositoryMetrics;

    public CompleteGlobalMetricsBO(OverallStats overallStats, StatsPerDay statsPerDay, LocalDateTime createdAt) {
        this(overallStats, statsPerDay, createdAt, Collections.emptyList());
    }

    public CompleteGlobalMetricsBO(OverallStats overallStats, StatsPerDay statsPerDay,
                                   LocalDateTime createdAt, List<BigRepositoryMetric> bigRepositoryMetrics) {
        this.overallStats = overallStats;
        this.statsPerDay = statsPerDay;
        this.createdAt = createdAt;
        this.bigRepositoryMetrics = bigRepositoryMetrics;
    }

    public long getAllMetadataCount() {
        return overallStats.getMetadataCount();
    }

    public long getExtractedTextsCount() {
        return overallStats.getExtractedTextsCount();
    }

    public long getDownloadedPdfsCount() {
        return overallStats.getDownloadedPdfsCount();
    }

    public long getGeneratedThumbnailsCount() {
        return overallStats.getThumbnailsCount();
    }

    public long getHarvestedReposCount() {
        return overallStats.getHarvestedReposCount();
    }

    public double getFreshnessGB() {
        return overallStats.getFreshnessGB();
    }

    public double getFreshness() {
        return overallStats.getFreshness();
    }

    public double getDocumentFreshness() {
        return overallStats.getDocumentFreshness();
    }

    public long getAllowedMetadataCount() {
        return overallStats.getAllowedMetadataCount();
    }

    public OverallStats getOverallStats() {
        return overallStats;
    }

    public StatsPerDay getStatsPerDay() {
        return statsPerDay;
    }

    public long getIndexedDocumentsCount() {
        return statsPerDay.getIndexedDocumentsCount();
    }

    public DocumentDownloadStats getDocumentDownloadStats() {
        return statsPerDay.getDocumentDownloadStats();
    }

    public TaskUpdateMetric getTaskUpdateMetric() {
        return taskUpdateMetric;
    }

    public List<BigRepositoryMetric> getBigRepositoryMetrics() {
        return bigRepositoryMetrics;
    }

    public void setTaskUpdateMetric(TaskUpdateMetric taskUpdateMetric) {
        this.taskUpdateMetric = taskUpdateMetric;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
    
    private Throughput getThroughput() {
        return statsPerDay.getThroughput();
    }

    @Override
    public String toString() {
        try {
            return "Generated metrics:\n" + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
