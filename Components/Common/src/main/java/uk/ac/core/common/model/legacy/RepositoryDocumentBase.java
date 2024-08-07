package uk.ac.core.common.model.legacy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import uk.ac.core.common.model.article.PDFUrlSource;

/**
 *
 * @author lucasanastasiou
 */
public class RepositoryDocumentBase {

    /**
     * Internal ID of document (is generated automatically when document is
     * inserted into database.
     */
    private Integer idDocument;
    /**
     * OAI identifier of document.
     */
    private String oai;
    /**
     * URL of document PDF.
     */
    private HashMap<String, PDFUrlSource> urls;

    /**
     * Last time a PDF was last attempted
     */
    private Date pdfLastAttempt;

    private Date metadataUpdated;

    /**
     * PDF Status
     */
    private boolean pdfStatus;

    public RepositoryDocumentBase() {
        this.urls = new HashMap<>();
    }

    /* ID_DOCUMENT ********************************************************************************/
    /**
     *
     * @return
     */
    public Integer getIdDocument() {
        return idDocument;
    }

    /**
     *
     * @param id
     */
    public void setIdDocument(Integer idDocument) {
        this.idDocument = idDocument;
    }

    /* OAI ****************************************************************************************/
    /**
     *
     * @return
     */
    public String getOai() {
        return this.oai;
    }

    /**
     *
     * @param identifier
     */
    public void setOai(String oai) {
        this.oai = oai;
    }

    
    public HashMap<String, PDFUrlSource> getUrls() {
        return urls;
    }

    public Date getPdfLastAttempt() {
        return pdfLastAttempt;
    }

    public void setPdfLastAttempt(Date pdfLastAttempt) {
        this.pdfLastAttempt = pdfLastAttempt;
    }

    /**
     *
     * @param url
     */
    public void addUrl(String u, PDFUrlSource source) {
        if (this.urls == null) {
            this.urls = new HashMap<String, PDFUrlSource>();
        }
        this.urls.put(u, source);
    }

    public void setUrls(HashMap<String, PDFUrlSource> urls) {
        this.urls = urls;
    }

    public boolean isPdfStatus() {
        return pdfStatus;
    }

    public void setPdfStatus(boolean pdfStatus) {
        this.pdfStatus = pdfStatus;
    }


    public Date getMetadataUpdated() { return metadataUpdated; }

    public void setMetadataUpdated(Date metadataUpdated) { this.metadataUpdated = metadataUpdated; }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("#")
                .append(idDocument)
                .append(" | ")
                .append(oai)
                .append(" | ");
        if (urls != null) {
            for (String u : urls.keySet()) {
                s.append(u).append(", ");
            }
        }
        return s.toString();
    }
}
