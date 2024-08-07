package uk.ac.core.workermetrics.service.document;

import uk.ac.core.workermetrics.service.dto.DocumentDownloadMetricsBO;
import java.util.List;

/**
 * Document Download Metrics Service.
 */
public interface DocumentDownloadMetricsService {

    List<DocumentDownloadMetricsBO> getDocumentMetricsFromYesterday();

}