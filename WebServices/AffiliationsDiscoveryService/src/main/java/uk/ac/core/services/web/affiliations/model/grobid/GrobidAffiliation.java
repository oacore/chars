package uk.ac.core.services.web.affiliations.model.grobid;

import java.util.ArrayList;
import java.util.List;

public class GrobidAffiliation {
    private String key;
    private List<GrobidOrganization> organizations;
    private GrobidAddress address;

    public GrobidAffiliation() {
        this.organizations = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (!"".equals(key)) {
            this.key = key;
        }
    }

    public List<GrobidOrganization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<GrobidOrganization> organizations) {
        this.organizations = organizations;
    }

    public GrobidAddress getAddress() {
        return address;
    }

    public void setAddress(GrobidAddress address) {
        this.address = address;
    }
}
