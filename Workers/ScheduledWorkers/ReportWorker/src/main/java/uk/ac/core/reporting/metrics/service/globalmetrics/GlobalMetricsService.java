package uk.ac.core.reporting.metrics.service.globalmetrics;

import uk.ac.core.reporting.metrics.service.dto.CompleteGlobalMetricsBO;
import java.util.Optional;

/**
 * Global Metrics Service.
 */
public interface GlobalMetricsService {

    void save(CompleteGlobalMetricsBO completeGlobalMetricsBO);

    Optional<CompleteGlobalMetricsBO> getLatestGlobalMetric();
}