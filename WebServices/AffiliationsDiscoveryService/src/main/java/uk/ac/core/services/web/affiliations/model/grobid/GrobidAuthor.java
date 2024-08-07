package uk.ac.core.services.web.affiliations.model.grobid;

import java.util.ArrayList;
import java.util.List;

public class GrobidAuthor {
    private String role;
    private String fullName;
    private String email;
    private List<GrobidAffiliation> affiliations;

    public GrobidAuthor() {
        this.affiliations = new ArrayList<>();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (!"".equals(role)) {
            this.role = role;
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<GrobidAffiliation> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<GrobidAffiliation> affiliations) {
        this.affiliations = affiliations;
    }
}
