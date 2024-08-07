package uk.ac.core.dataprovider.logic.entity;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "repository")
public class DataProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_repository")
    private Long id;

    @Column(name = "id_opendoar")
    private Long openDoarId;

    @Column(name = "id_base")
    private Long baseId;

    @Column
    private String name;

    /**
     * Use {@link DataProvider#urlOaipmh} instead.
     */
    @Deprecated
    private String uri;

    @Column(nullable = false)
    private String urlOaipmh;
    private String urlHomepage;
    private String source;
    private String software;
    private String description;

    @Column(name="metadata_format")
    private String metadataFormat = "oai_dc";

    @Column
    private boolean journal = false;

    @Column
    private boolean disabled = false;

    @Temporal(TemporalType.DATE)
    private Date created_date;

    public DataProvider() {
    }

    public DataProvider(String name, String urlOaipmh, String urlHomepage) {
        this.name = name;
        this.urlOaipmh = urlOaipmh;
        this.urlHomepage = urlHomepage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOpenDoarId() {
        return openDoarId;
    }

    public void setOpenDoarId(Long OpenDoarId) {
        this.openDoarId = OpenDoarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Use {@link DataProvider#getUrlOaipmh()} instead.
     */
    @Deprecated
    public String getUri() {
        return uri;
    }

    /**
     * Use {@link DataProvider#setUrlOaipmh(String)} ()} instead.
     */
    @Deprecated
    public void setUri(String uri) {
        setUrlOaipmh(uri);
    }

    public String getUrlOaipmh() {
        return urlOaipmh;
    }

    public void setUrlOaipmh(String urlOaipmh) {
        this.urlOaipmh = urlOaipmh;
        // support backward compatibility with uri for existing applications
        this.uri = urlOaipmh;
    }

    public String getUrlHomepage() {
        return urlHomepage;
    }

    public void setUrlHomepage(String urlHomepage) {
        this.urlHomepage = urlHomepage;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getMetadataFormat() {
        return metadataFormat;
    }

    public void setMetadataFormat(String metadataFormat) {
        this.metadataFormat = metadataFormat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isJournal() {
        return journal;
    }

    public void setJournal(Boolean journal) {
        this.journal = journal;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }


    @Override
    public String toString() {
        return "DataProvider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", urlOaipmh='" + urlOaipmh + '\'' +
                ", source='" + source + '\'' +
                ", software='" + software + '\'' +
                ", description='" + description + '\'' +
                ", metadataFormat='" + metadataFormat + '\'' +
                ", journal=" + journal +
                ", disabled=" + disabled +
                '}';
    }

    /**
     * WARNING: The repos, which are not saved yet, DON'T have id.
     *
     * @param o
     *
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataProvider)) return false;
        DataProvider that = (DataProvider) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}