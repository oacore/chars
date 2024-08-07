package uk.ac.core.extractmetadata.worker.oaipmh.GetRecords;

/**
 * Enumeration for capturing extraction statistics.
 */
public enum Statistics {

    /**
     * total number of records in the metadata file
     */
    META_TOTAL,
    /**
     * total number of documents with PDF link
     */
    META_PDF_URL,
    /**
     * documents without PDF link
     */
    META_NO_URL,
    /**
     * number of deleted documents
     */
    META_DELETED,
    /**
     * how many new records were added during this update
     */
    DB_ADDED,
    /**
     * how many documents were updated during this update
     */
    DB_UPDATED,
    /**
     * how many documents were deleted during this update
     */
    DB_DELETED,
    /**
     * how many documents were allowed again
     */
    DB_UNDELETED;
    /**
     * Value of one enumeration object.
     */
    private int value = 0;

    /**
     * Get the value of the enumeration object.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Increase the object value by one.
     */
    public void incValue() {
        this.value++;
    }

    /**
     * Reset the object value to 0.
     */
    public void resetValue() {
        this.value = 0;
    }
}
