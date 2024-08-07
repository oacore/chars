package uk.ac.core.oadiscover.model;

/**
 *
 * @author lucas
 */
public class MuccDocumentUrl {

    private Integer id_document;
    private Integer pdf_status;
    private String url;

    public Integer getId_document() {
        return id_document;
    }

    public void setId_document(Integer id_document) {
        this.id_document = id_document;
    }

    public Integer getPdf_status() {
        return pdf_status;
    }

    public void setPdf_status(Integer pdf_status) {
        this.pdf_status = pdf_status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
