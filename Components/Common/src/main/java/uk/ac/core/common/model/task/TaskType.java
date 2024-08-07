package uk.ac.core.common.model.task;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author lucasanastasiou
 */
public enum TaskType implements Serializable {

    @SerializedName("harvest")
    HARVEST("harvest"),
    @SerializedName("metadata_download")
    METADATA_DOWNLOAD("metadata_download"),
    @SerializedName("download-document")
    DOCUMENT_DOWNLOAD("download-document"),
    @SerializedName("extract-metadata")
    EXTRACT_METADATA("extract-metadata"),
    @SerializedName("extract-text")
    EXTRACT_TEXT("extract-text"),
    @SerializedName("index")
    INDEX("index"),
    @SerializedName("thumbnail_generation")
    THUMBNAIL_GENERATION("thumbnail-generation"),
    @SerializedName("extended-metadata-process")
    EXTENDED_METADATA_PROCESS("extended-metadata-process"),
    //
    // Item level tasks
    //
    @SerializedName("extract-text-item")
    EXTRACT_TEXT_ITEM("extract-text-item"),
    @SerializedName("index-item")
    INDEX_ITEM("index-item"),
    @SerializedName("works-index-item")
    WORKS_INDEX_ITEM("works-index-item"),
    @SerializedName("works-index-item-reindex")
    WORKS_INDEX_ITEM_REINDEX("works-index-item-reindex"),
    @SerializedName("thumbnail-generation-item")
    THUMBNAIL_GENERATION_ITEM("thumbnail-generation-item"),
    @SerializedName("grobid-extraction-item")
    GROBID_EXTRACTION_ITEM("grobid-extraction-item"),
    @SerializedName("grobid-processor-item")
    GROBID_PROCESSING_ITEM("grobid-processing-item"),
    @SerializedName("grobid-citation-parser-item")
    GROBID_CITATION_PARSER_ITEM("grobid-citation-parser-item"),
    @SerializedName("grobid-affiliation-parser-item")
    GROBID_AFFILIATION_PARSER_ITEM("grobid-affiliation-parser-item"),
    @SerializedName("reindex-item")
    REINDEX_ITEM("reindex-item"),
    @SerializedName("item-doi-resolution")
    ITEM_DOI_RESOLUTION("item-doi-resolution"),
    @SerializedName("item-language-detection")
    ITEM_LANG_DETECTION("item-language-detection"),
    @SerializedName("pdf-decorate-item")
    PDF_DECORATE_ITEM("pdf-decorate-item"),
    @SerializedName("document-filesystem-item")
    DOCUMENT_FILESYSTEM_ITEM("document-filesystem-item"),
    @SerializedName("purge-document-item")
    PURGE_DOCUMENT_ITEM("purge-document-item"),
    //
    // Scheduled tasks
    //
    @SerializedName("opendoar-import")
    OPENDOAR_IMPORT("opendoar-import"),
    @SerializedName("BASE import")
    BASE_IMPORT("BASE import"),
    SITEMAPS_GENERATION("sitemaps_generation"),
    METADATA_DOWNLOAD_FAILURE_DIAGNOSTICS("metadata_download_failure_diagnostics"),
    @SerializedName("check-for-unprocessed-pdfs")
    CHECK_FOR_UNPROCESSED_PDFS("check-for-unprocessed-pdfs"),
    @SerializedName("reporting")
    REPORTING("reporting"),
    @SerializedName("metric-collection")
    METRIC_COLLECTION("metric-collection"),
    @SerializedName("upload-to-cloud")
    UPLOAD_TO_CLOUD_ITEM("upload-to-cloud"),
    @SerializedName("rioxx-compliance")
    RIOXX_COMPLIANCE("rioxx-compliance"),
    @SerializedName("mucc-document-download")
    MUCC_DOCUMENT_DOWNLOAD("mucc-document-download"),
    @SerializedName("dit-ingestion")
    DIT_INGESTION("dit-ingestion"),
    @SerializedName("arxiv-daily-harvest")
    ARXIV_DAILY_HARVEST("arxiv-daily-harvest"),
    @SerializedName("warehouse-report-generation")
    WAREHOUSE_REPORT_GENERATION("warehouse-report-generation"),
    @SerializedName("publisher_name_update")
    PUBLISHER_NAME_UPDATE("publisher_name_update"),
    @SerializedName("journal_issn_update")
    JOURNALS_ISSN_UPDATE("journal_issn_update"),
    @SerializedName("notifications")
    NOTIFICATIONS("notifications");

    public static TaskType fromString(String typeStr) {
        for (TaskType type : TaskType.values()) {
            if (type.getName().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        return null;
    }

    private String name;

    public String getName() {
        return this.name;
    }

    TaskType(String name) {
        this.name = name;
    }
}
