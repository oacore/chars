package uk.ac.core.common.model.legacy;

/**
 *
 * @author aristotelischaralampous
 */
public class DocumentType {
    
    private Integer id;
    private int idDocument;
    private String documentType;
	private Double documentTypeConfidence;

	public DocumentType(Integer id, int id_document, String documentType) {
        this.id = id;
        this.idDocument = id_document;
        this.documentType = documentType;
    }
	
	public Double getDocumentTypeConfidence() {
		return documentTypeConfidence;
	}

	public void setDocumentTypeConfidence(Double documentTypeConfidence) {
		this.documentTypeConfidence = documentTypeConfidence;
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

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}
