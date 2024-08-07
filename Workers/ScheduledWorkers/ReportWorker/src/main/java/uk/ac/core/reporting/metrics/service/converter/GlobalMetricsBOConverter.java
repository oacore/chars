package uk.ac.core.reporting.metrics.service.converter;

import uk.ac.core.reporting.metrics.data.entity.GlobalMetrics;
import uk.ac.core.reporting.metrics.model.HarvestStep;
import uk.ac.core.reporting.metrics.service.dto.CompleteGlobalMetricsBO;
import uk.ac.core.reporting.metrics.service.dto.OverallStats;
import uk.ac.core.reporting.metrics.service.dto.StatsPerDay;

public final class GlobalMetricsBOConverter {
    private final CompleteGlobalMetricsBO globalMetricsBO;

    public GlobalMetricsBOConverter(CompleteGlobalMetricsBO globalMetricsBO) {
        this.globalMetricsBO = globalMetricsBO;
    }

    public GlobalMetrics toGlobalMetricsBO() {
        GlobalMetrics globalMetrics = new GlobalMetrics();
        globalMetrics.setMetadataCount(getOverallStats().getMetadataCount());
        globalMetrics.setExtractedTextsCount(getOverallStats().getExtractedTextsCount());
        globalMetrics.setDownloadedPdfsCount(getOverallStats().getDownloadedPdfsCount());
        globalMetrics.setGeneratedThumbnailsCount(getOverallStats().getThumbnailsCount());
        globalMetrics.setHarvestedReposCount(getOverallStats().getHarvestedReposCount());
        globalMetrics.setFreshnessGB(getOverallStats().getFreshnessGB());
        globalMetrics.setFreshness(getOverallStats().getFreshness());
        globalMetrics.setDocumentFreshness(getOverallStats().getDocumentFreshness());
        globalMetrics.setDownloadedMetadataTasksCount(getPerDayStats().getCompletedHarvestTasks(HarvestStep.METADATA_DOWNLOAD));
        globalMetrics.setExtractedMetadataTasksCount(getPerDayStats().getCompletedHarvestTasks(HarvestStep.EXTRACT_METADATA));
        globalMetrics.setDownloadedDocumentsTasksCount(getPerDayStats().getCompletedHarvestTasks(HarvestStep.DOCUMENT_DOWNLOAD));
        return globalMetrics;
    }

    private OverallStats getOverallStats() {
        return globalMetricsBO.getOverallStats();
    }

    private StatsPerDay getPerDayStats() {
        return globalMetricsBO.getStatsPerDay();
    }
}