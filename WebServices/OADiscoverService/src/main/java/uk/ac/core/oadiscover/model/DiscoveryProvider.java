package uk.ac.core.oadiscover.model;

/**
 *
 * @author lucas
 */
public enum DiscoveryProvider {
    CORE,
    UNPAYWALL,
    KOPERNIO,
    OABUTTON,
    EPMC;
    
    public String verbose(){
        return "From "+this.name();
    }
}
