package uk.ac.core.reporting.metrics;

import uk.ac.core.reporting.metrics.model.AlertStatus;
import uk.ac.core.reporting.metrics.service.dto.CompleteGlobalMetricsBO;

import java.util.Optional;

public interface AlertService {
    Optional<AlertStatus> metricsToAlert(CompleteGlobalMetricsBO completeGlobalMetricsBO);
}
