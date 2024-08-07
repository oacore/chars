package uk.ac.core.elasticsearch.entities;

/**
 *
 * @author lucas
 */
public class OrcidAuthor {

    private String orcidId;

    public OrcidAuthor() {
    }

    public OrcidAuthor(String orcidId) {
        this.orcidId = orcidId;
    }

    public String getOrcidId() {
        return orcidId;
    }

    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
    }
    
}
