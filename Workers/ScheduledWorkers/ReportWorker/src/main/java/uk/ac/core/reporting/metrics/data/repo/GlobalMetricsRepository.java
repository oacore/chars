package uk.ac.core.reporting.metrics.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.core.reporting.metrics.data.entity.GlobalMetrics;
import java.util.Optional;

/**
 * Global Metrics Repository.
 */
public interface GlobalMetricsRepository extends JpaRepository<GlobalMetrics, Integer> {
    Optional<GlobalMetrics> findFirstByOrderByIdDesc();
}