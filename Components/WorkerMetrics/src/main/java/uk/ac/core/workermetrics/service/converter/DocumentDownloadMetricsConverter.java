package uk.ac.core.workermetrics.service.converter;

import uk.ac.core.workermetrics.data.entity.DocumentDownloadMetrics;
import uk.ac.core.workermetrics.service.dto.DocumentDownloadMetricsBO;

public final class DocumentDownloadMetricsConverter {

    private DocumentDownloadMetricsConverter() {

    }

    public static DocumentDownloadMetricsBO toDocumentDownloadMetricsBO(DocumentDownloadMetrics documentDownloadMetrics) {
        DocumentDownloadMetricsBO documentDownloadMetricsBO = new DocumentDownloadMetricsBO();
        documentDownloadMetricsBO.setNumberOfDocumentsAttempted(documentDownloadMetrics.getNumberOfDocumentsAttempted());
        documentDownloadMetricsBO.setNumberOfDocumentsDownloaded(documentDownloadMetrics.getNumberOfPdfDownloaded());
        documentDownloadMetricsBO.setNumberOfRequestsPerformed(documentDownloadMetrics.getNumberOfRequestsPerformed());
        documentDownloadMetricsBO.setDate(documentDownloadMetrics.getDate());
        documentDownloadMetricsBO.setDuration(documentDownloadMetrics.getDuration());
        return documentDownloadMetricsBO;
    }
}