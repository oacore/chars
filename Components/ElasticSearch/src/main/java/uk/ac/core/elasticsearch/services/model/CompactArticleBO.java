package uk.ac.core.elasticsearch.services.model;

public class CompactArticleBO {

    private int documentId;

    private boolean fullText;

    public CompactArticleBO() {
    }

    public CompactArticleBO(int documentId, boolean fullText) {
        this.documentId = documentId;
        this.fullText = fullText;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public boolean isFullText() {
        return fullText;
    }

    public void setFullText(boolean fullText) {
        this.fullText = fullText;
    }
}