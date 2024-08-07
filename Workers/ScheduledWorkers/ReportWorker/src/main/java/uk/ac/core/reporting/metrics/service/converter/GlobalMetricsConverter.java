package uk.ac.core.reporting.metrics.service.converter;

import uk.ac.core.reporting.metrics.data.entity.GlobalMetrics;
import uk.ac.core.reporting.metrics.model.HarvestStep;
import uk.ac.core.reporting.metrics.model.HarvestTaskMetrics;
import uk.ac.core.reporting.metrics.model.Throughput;
import uk.ac.core.reporting.metrics.service.dto.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Global metrics converter.
 */
public final class GlobalMetricsConverter {

    private static final int DEFAULT_NUMERIC_VALUE = 0;
    private final GlobalMetrics globalMetrics;
    private final List<BigRepositoryMetric> bigRepositoryMetric;

    public GlobalMetricsConverter(GlobalMetrics globalMetrics, List<BigRepositoryMetric> bigRepositoryMetrics) {
        this.globalMetrics = globalMetrics;
        this.bigRepositoryMetric = bigRepositoryMetrics;
    }

    public CompleteGlobalMetricsBO toGlobalMetricsBO() {
        return new CompleteGlobalMetricsBO(
                createOverallStats(),
                createStatsPerDay(),
                LocalDateTime.now(),
                bigRepositoryMetric);
    }

    private StatsPerDay createStatsPerDay() {
        return new StatsPerDay(null, null, createThroughput());
    }

    private Throughput createThroughput() {
        return new Throughput()
                .addHarvestMetrics(new HarvestTaskMetrics<>(HarvestStep.METADATA_DOWNLOAD, globalMetrics.getDownloadedMetadataTasksCount() == null ? DEFAULT_NUMERIC_VALUE : globalMetrics.getDownloadedMetadataTasksCount(), DEFAULT_NUMERIC_VALUE))
                .addHarvestMetrics(new HarvestTaskMetrics<>(HarvestStep.EXTRACT_METADATA, globalMetrics.getExtractedMetadataTasksCount() == null ? DEFAULT_NUMERIC_VALUE : globalMetrics.getExtractedMetadataTasksCount(), DEFAULT_NUMERIC_VALUE))
                .addHarvestMetrics(new HarvestTaskMetrics<>(HarvestStep.DOCUMENT_DOWNLOAD, globalMetrics.getDownloadedDocumentsTasksCount() == null ? DEFAULT_NUMERIC_VALUE : globalMetrics.getDownloadedDocumentsTasksCount(), DEFAULT_NUMERIC_VALUE));
    }

    private OverallStats createOverallStats() {
        return new OverallStatsBuilder()
                .allMetadataCount(globalMetrics.getMetadataCount())
                .downloadedPdfsCount(globalMetrics.getDownloadedPdfsCount())
                .harvestedReposCount(globalMetrics.getHarvestedReposCount())
                .extractedTextsCount(globalMetrics.getExtractedTextsCount())
                .thumbnailsCount(globalMetrics.getGeneratedThumbnailsCount())
                .freshnessGB(globalMetrics.getFreshnessGB() == null ? DEFAULT_NUMERIC_VALUE : globalMetrics.getFreshnessGB())
                .freshness(globalMetrics.getFreshness() == null ? DEFAULT_NUMERIC_VALUE : globalMetrics.getFreshness())
                .documentFreshness(globalMetrics.getDocumentFreshness() == null ? DEFAULT_NUMERIC_VALUE : globalMetrics.getDocumentFreshness())
                .build();
    }

}
