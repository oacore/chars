package uk.ac.core.reporting.metrics.service.globalmetrics;

import org.springframework.stereotype.Service;
import uk.ac.core.reporting.metrics.data.entity.GlobalMetrics;
import uk.ac.core.reporting.metrics.data.repo.GlobalMetricsRepository;
import uk.ac.core.reporting.metrics.service.BigRepositoryReportingService;
import uk.ac.core.reporting.metrics.service.converter.GlobalMetricsBOConverter;
import uk.ac.core.reporting.metrics.service.converter.GlobalMetricsConverter;
import uk.ac.core.reporting.metrics.service.dto.BigRepositoryMetric;
import uk.ac.core.reporting.metrics.service.dto.CompleteGlobalMetricsBO;

import java.util.List;
import java.util.Optional;

@Service
public class GlobalMetricsServiceImpl implements GlobalMetricsService {

    private final GlobalMetricsRepository globalMetricsRepository;
    private final BigRepositoryReportingService bigRepositoryReportingService;

    public GlobalMetricsServiceImpl(GlobalMetricsRepository globalMetricsRepository,
                                    BigRepositoryReportingService bigRepositoryReportingService) {
        this.globalMetricsRepository = globalMetricsRepository;
        this.bigRepositoryReportingService = bigRepositoryReportingService;
    }

    @Override
    public void save(CompleteGlobalMetricsBO completeGlobalMetricsBO) {
        globalMetricsRepository.save(new GlobalMetricsBOConverter(completeGlobalMetricsBO).toGlobalMetricsBO());
    }

    @Override
    public Optional<CompleteGlobalMetricsBO> getLatestGlobalMetric() {
        Optional<GlobalMetrics> globalMetrics = globalMetricsRepository.findFirstByOrderByIdDesc();
        List<BigRepositoryMetric> bigRepositoryMetrics = bigRepositoryReportingService.getCurrentBigRepositoryMetrics();
        return globalMetrics.map(globalMetric -> new GlobalMetricsConverter(globalMetric, bigRepositoryMetrics).toGlobalMetricsBO());
    }
}
