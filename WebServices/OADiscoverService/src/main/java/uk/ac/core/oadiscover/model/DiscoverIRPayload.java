package uk.ac.core.oadiscover.model;

/**
 *
 * @author lucas
 */
public class DiscoverIRPayload {

    String doi;//: "10.1111/meta.12293"
    String eprints_id;//: "54889"
    String plugin_id;//: ""
    String referrer_url;//: "http://oro.open.ac.uk/54889/"

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getEprints_id() {
        return eprints_id;
    }

    public void setEprints_id(String eprints_id) {
        this.eprints_id = eprints_id;
    }

    public String getPlugin_id() {
        return plugin_id;
    }

    public void setPlugin_id(String plugin_id) {
        this.plugin_id = plugin_id;
    }

    public String getReferrer_url() {
        return referrer_url;
    }

    public void setReferrer_url(String referrer_url) {
        this.referrer_url = referrer_url;
    }
    
}
