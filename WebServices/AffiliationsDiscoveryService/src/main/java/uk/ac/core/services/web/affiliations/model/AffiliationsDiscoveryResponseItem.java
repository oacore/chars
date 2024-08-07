package uk.ac.core.services.web.affiliations.model;

import java.util.HashMap;
import java.util.Map;

public class AffiliationsDiscoveryResponseItem {

    private String email;
    private String author;
    private String institution;
    private Map<String, String> identifiers;
    private Double confidence;

    public AffiliationsDiscoveryResponseItem() {
        this.identifiers = new HashMap<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public Map<String, String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Map<String, String> identifiers) {
        this.identifiers = identifiers;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
