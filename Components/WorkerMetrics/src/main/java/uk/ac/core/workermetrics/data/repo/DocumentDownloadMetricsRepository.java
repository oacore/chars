package uk.ac.core.workermetrics.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.core.workermetrics.data.entity.DocumentDownloadMetrics;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface DocumentDownloadMetricsRepository extends JpaRepository<DocumentDownloadMetrics, Integer> {

    List<DocumentDownloadMetrics> findByDateAfter(LocalDateTime date);

}