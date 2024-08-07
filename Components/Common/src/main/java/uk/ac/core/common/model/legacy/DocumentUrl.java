package uk.ac.core.common.model.legacy;

import uk.ac.core.common.model.article.PDFUrlSource;

/**
 *
 * @author samuel
 */
public class DocumentUrl {

    private Integer id;
    private int idDocument;
    private String url;
    private int pdfStatus;
    private PDFUrlSource pDFUrlSource = PDFUrlSource.OAIPMH;

    public DocumentUrl(Integer id, int id_document, String url, int pdf_status, PDFUrlSource source) {
        this.id = id;
        this.idDocument = id_document;
        this.url = url;
        this.pdfStatus = pdf_status;
        this.pDFUrlSource = source;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDocument() {
        return idDocument;
    }

    public void setId_document(int id_document) {
        this.idDocument = id_document;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl_tr() {
        return (this.url.length() <= 1000) ? this.url : this.url.substring(0, 1000);
    }

    public int getPdfStatus() {
        return pdfStatus;
    }

    public void setPdfStatus(int pdf_status) {
        this.pdfStatus = pdf_status;
    }

    public PDFUrlSource getpDFUrlSource() {
        return pDFUrlSource;
    }

    public void setpDFUrlSource(PDFUrlSource pDFUrlSource) {
        this.pDFUrlSource = pDFUrlSource;
    }
    

}
