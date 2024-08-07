package uk.ac.core.oadiscover.model;

import org.springframework.data.elasticsearch.annotations.Document;

/**
 *
 * @author lucas
 */
@Document(indexName = "caching-discovery", type = "_doc")
public class CachedDiscoveryDocument {

    private String doi;
    private String link;
    private String source;
    private Boolean valid;

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
    
    public DiscoverySource convertToDiscoverySource(){
        DiscoverySource ds = new DiscoverySource();
        ds.setLink(link);
        ds.setSource(source);
        ds.setValid(valid);
        return ds;
                
    }
    
    public static CachedDiscoveryDocument buildFromDiscoverySource(String doi,DiscoverySource dSource){
        CachedDiscoveryDocument cachedDiscoveryDocument = new CachedDiscoveryDocument();
        cachedDiscoveryDocument.setDoi(doi);
        cachedDiscoveryDocument.setLink(dSource.getLink());
        cachedDiscoveryDocument.setSource(dSource.getSource());
        cachedDiscoveryDocument.setValid(dSource.getValid());
        return cachedDiscoveryDocument;
    }

}
