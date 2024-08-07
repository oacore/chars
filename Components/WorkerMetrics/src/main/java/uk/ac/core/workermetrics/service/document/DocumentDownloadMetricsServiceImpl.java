package uk.ac.core.workermetrics.service.document;

import org.springframework.stereotype.Service;
import uk.ac.core.workermetrics.data.repo.DocumentDownloadMetricsRepository;
import uk.ac.core.workermetrics.service.converter.DocumentDownloadMetricsConverter;
import uk.ac.core.workermetrics.service.dto.DocumentDownloadMetricsBO;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentDownloadMetricsServiceImpl implements DocumentDownloadMetricsService {

    private final DocumentDownloadMetricsRepository documentDownloadMetricsRepository;

    public DocumentDownloadMetricsServiceImpl(DocumentDownloadMetricsRepository documentDownloadMetricsRepository) {
        this.documentDownloadMetricsRepository = documentDownloadMetricsRepository;
    }

    @Override
    public List<DocumentDownloadMetricsBO> getDocumentMetricsFromYesterday() {
        return documentDownloadMetricsRepository.findByDateAfter(LocalDate.now().minusDays(1).atStartOfDay()).stream()
                .map(DocumentDownloadMetricsConverter::toDocumentDownloadMetricsBO)
                .collect(Collectors.toList());
    }
}
