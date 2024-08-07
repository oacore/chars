package uk.ac.core.services.web.affiliations.model.grobid;

public class GrobidOrganization {
    private String key;
    private GrobidOrganizationType type;
    private String name;

    public GrobidOrganization() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (!"".equals(key)) {
            this.key = key;
        }
    }

    public GrobidOrganizationType getType() {
        return type;
    }

    public void setType(String type) {
        this.type = GrobidOrganizationType.valueOf(type.toUpperCase());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
