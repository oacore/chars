package uk.ac.core.common.model.legacy;

/**
 *
 * @author lucasanastasiou
 */
@Deprecated
public enum ActionType {

    // FULL repository actions
// FULL repository actions
    METADATA_DOWNLOAD("metadata_download", "Metadata Download"),
    METADATA_EXTRACT("metadata_extract", "Metadata Extraction"),
    METADATA_GENERATE("metadata_generate"),
    DOCUMENT("pdfs", "Document Download"),
    TEXT_EXTRACTION("text", "Text Extraction"),
    IMPORT_PDFS("import_pdfs", "PDF Import"),
    INDEX("index", "Indexing"),
    REINDEX("reindex", "Reindexing"),
    RECURSIVE_CRAWLING("recursive_crawling", "Recursive Crawling"),
    CALCULATE_SIMILARITIES("calculate_similarities", "Calculate Similarities"),
    IMAGE_GENERATION("image_generation", "Image preview generation"),
    // citation actions
    CITATION_PARSING("citation_parsing", "Parse Citations"),
    CITATION_EXTRACT("citation_extraction", "Extract Citations"),
    CITATION_DOIS("citation_dois", "Resolve Citation DOIs"),
    DOCUMENT_DOIS("document_dois", "Resolve Document DOIs"),
    // journal actions
    JOURNAL_METADATA_DOWNLOAD("journal_metadata_download", "Journal Metadata Download"),
    JOURNAL_METADATA_INDEX("journal_metadata_index", "Journal Metadata Index"),
    // global actions
    LANGUAGE_DETECTION("language_detection", "Language Detection"),
    SIMILARITY("similarity", "Similarity"),
    DEDUPLICATION("deduplication", "Deduplication"),
    RESOLVE_FULLTEXT_DOIS("resolve_fulltext_dois", "Resolve fulltext DOIs"),
    // incremental repository actions
    INCREMENTAL_METADATA_DOWNLOAD("incremental_metadata_update", "Incrementally Download Metadata"),
    INCREMENTAL_METADATA_EXTRACT("incremental_metadata_extract", "Incremental Metadata Extraction"),
    INCREMENTAL_METADATA_GENERATE("incremental_metadata_generate"),
    INCREMENTAL_PDFS("incremental_pdfs", "Incremental PDF Download"),
    INCREMENTAL_TEXT_EXTRACTION("incremental_text", "Incremental Text Extraction"),
    INCREMENTAL_CITATIONS("incremental_citations", "Incremental Citation Extraction"),
    INCREMENTAL_CITATION_PARSING("incremental_citation_parsing", "Incrementally Parse Citations"),
    INCREMENTAL_IMPORT_PDFS("incremental_import_pdfs", "Incremental PDF Import"),
    INCREMENTAL_INDEX("incremental_index", "Incremental Indexing"),
    INCREMENTAL_LANGUAGE_DETECTION("incremental_language_detection", "Incremental Language Detection"),
    INCREMENTAL_SIMILARITY("incremental_similarity", "Incremental Similarity"),
    INCREMENTAL_DEDUPLICATION("incremental_deduplication", "Incremental Deduplication"),
    // export actions
    EXPORT_TO_SESAME("export_to_sesame", "Export To Sesame"),
    EXPORT_AS_RDF("export_as_rdf", "Export as RDF"),
    EXPORT_AS_JSON("export_as_json", "Export as JSON"),
    // global index actions
    GLOBAL_INDEX("global_index", "Global index"),
    // resume actions
    RESUME_METADATA_DOWNLOAD("resume_metadata_download", "Resume Metadata Download"),
    //
    // single record update
    //
    SINGLE_RECORD_METADATA_DOWNLOAD("single_record_metadata_download", "Single record metadata download"),
    SINGLE_RECORD_PDF_DOWNLOAD("single_record_pdf_download", "Single record pdf download"),
    SINGLE_RECORD_TEXT_EXTRACT("single_record_text_extract", "Single record text extract"),
    SINGLE_RECORD_PREVIEW_GENERATION("single_record_preview_generation", "Single record preview generation"),
    SINGLE_RECORD_INDEX("single_record_index", "Single record index"), 
    RIOXXCOMPLIANCE("rioxx_compliance", "Rioxx compliance checker"),
    // parse metadata to discover available metadata_formats (of repositories)
    DISCOVER_AVAILABLE_METADATA_FORMATS("discover_available_metadata_formats","Discover availabale metadata formats");

    private String databaseValue;
    private String name;

    ActionType(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    ActionType(String databaseValue, String name) {
        this.databaseValue = databaseValue;
        this.name = name;
    }

    @Override
    public String toString() {
        return databaseValue;
    }

    public String getName() {
        return name;
    }

    static public ActionType fromString(String actionTypeStr) {
        for (ActionType type : ActionType.values()) {
            if (type.toString().equals(actionTypeStr)) {
                return type;
            }
        }
        return null;
    }
}