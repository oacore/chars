package uk.ac.core.common.model.legacy;

import java.util.Date;

/**
 *
 * @author scp334
 */
public class DocumentRawMetadata {

    private Integer id;
    private Integer idDocument;
    private Date datetime;
    private String metadata;

    public DocumentRawMetadata(){}
    /**
     *
     * @param id
     * @param id_document the CORE id of the document
     * @param datetime Datetime where the metadata was added/updated in CORE
     * @param metadata The raw xml of the record
     */
    public DocumentRawMetadata(int id, int id_document, Date datetime, String metadata) {
        this.id = id;
        this.idDocument = id_document;
        this.datetime = datetime;
        this.metadata = metadata;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The id of the document (id_document in document table)
     *
     * @return
     */
    public Integer getIdDocument() {
        return idDocument;
    }

    /**
     * The id of the document (id_document in document table)
     *
     * @param id_document
     */
    public void setIdDocument(Integer id_document) {
        this.idDocument = id_document;
    }

    /**
     * The datetime of when the record was added to core
     *
     * @return
     */
    public Date getDatetime() {
        return datetime;
    }

    /**
     * The datetime of when the record was added to core
     *
     * @param datetime
     */
    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    /**
     *
     * @return
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     */
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String toString() {
        return "#" + this.idDocument + " : " + this.metadata;
    }

}
