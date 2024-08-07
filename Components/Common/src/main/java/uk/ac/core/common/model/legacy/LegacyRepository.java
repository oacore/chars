package uk.ac.core.common.model.legacy;

import java.util.Date;
import java.util.Objects;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Mapping;
//import org.springframework.data.elasticsearch.annotations.Setting;

/**
 *
 * @author pk3295, gp3237, dh8835, mk6353, la4227
 * @deprecated See DataProvider.entity.Repository
 */
@Deprecated
//@Document(indexName = "repositories", type = "repository")
//@Setting(settingPath = "/elasticsearch/mappings/settings.json")
//@Mapping(mappingPath = "/elasticsearch/mappings/repository.json")
public class LegacyRepository {

    private String id;
    private int openDoarId;
    private String name; //e.g. ORO...
    private String uri; // must be same as urlOaipmh
    private String urlHomepage;
    private String urlOaipmh;
    private String uriJournals;
    private String physicalName = "noname";//oro.open.ac.uk
    private String source;
    private String software;
    private String metadataFormat;
    private String description;
    private Boolean journal;
    private int roarId;
    private int baseId;

    //the latest update is first in the list
    //private List<Task> rUpdates = new ArrayList<TaskUpdate>();
    private String pdfStatus; //true/false
    private int nrUpdates;//number of repository checkings
    private boolean disabled;

    private Date lastUpdateTime;

    private LegacyRepositoryLocation repositoryLocation;

    public LegacyRepository() {
    }

    /**
     * Variable for save time used by loading pdf dir name from property file
     */
    //private String pdfDir;

    /* METHODS *********************************************************************************** */
    public LegacyRepository(String id, String name, String uri, String uriJournals, String source,
            String physicalName, Boolean disabled,
            String software, String metadataFormat) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.uriJournals = uriJournals;
        this.disabled = disabled;
        this.source = source;
        this.software = software;
        this.metadataFormat = metadataFormat;

        if (source.equals("oai")) {
            this.physicalName = "" + this.id;
        } else {
            this.physicalName = physicalName;
        }
    }

    public LegacyRepository(String repositoryId, int OpenDOARID, String name,
            String uri, String uriJournals, String source, String physicalName,
            boolean disabled, String software, String metadata_format) {
        this(repositoryId, name, uri, uriJournals, source, physicalName, disabled, software, metadata_format);
        this.openDoarId = OpenDOARID;
    }

    public LegacyRepository(String repositoryId, int OpenDOARID, String name,
            String uri, String uriJournals, String source, String physicalName,
            boolean disabled, String software, String metadata_format, Boolean journal) {
        this(repositoryId, name, uri, uriJournals, source, physicalName, disabled, software, metadata_format);
        this.openDoarId = OpenDOARID;
        this.journal = journal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOpenDoarId() {
        return openDoarId;
    }

    public void setOpenDoarId(int openDoarId) {
        this.openDoarId = openDoarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlHomepage() {
        return urlHomepage;
    }

    public void setUrlHomepage(String urlHomepage) {
        this.urlHomepage = urlHomepage;
    }

    public String getUrlOaipmh() {
        return urlOaipmh;
    }

    public void setUrlOaipmh(String urlOaipmh) {
        this.urlOaipmh = urlOaipmh;
        // For backward compatibility
        this.uri = urlOaipmh;
    }

    public String getUriJournals() {
        return uriJournals;
    }

    public void setUriJournals(String uriJournals) {
        this.uriJournals = uriJournals;
    }

    public String getPhysicalName() {
        return physicalName;
    }

    public void setPhysicalName(String physicalName) {
        this.physicalName = physicalName;
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

    public Boolean getJournal() {
        return journal;
    }

    public void setJournal(Boolean journal) {
        this.journal = journal;
    }

    public String getPdfStatus() {
        return pdfStatus;
    }

    public void setPdfStatus(String pdfStatus) {
        this.pdfStatus = pdfStatus;
    }

    public int getNrUpdates() {
        return nrUpdates;
    }

    public void setNrUpdates(int nrUpdates) {
        this.nrUpdates = nrUpdates;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public LegacyRepositoryLocation getRepositoryLocation() {
        return repositoryLocation;
    }

    public void setRepositoryLocation(LegacyRepositoryLocation repositoryLocation) {
        this.repositoryLocation = repositoryLocation;
    }

    public Integer getRoarId() {
        return roarId;
    }

    public void setRoarId(int roarId) {
        this.roarId = roarId;
    }

    public int getBaseId() {
        return baseId;
    }

    public void setBaseId(int baseId) {
        this.baseId = baseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegacyRepository that = (LegacyRepository) o;
        return openDoarId == that.openDoarId &&
                roarId == that.roarId &&
                baseId == that.baseId &&
                nrUpdates == that.nrUpdates &&
                disabled == that.disabled &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(uri, that.uri) &&
                Objects.equals(urlHomepage, that.urlHomepage) &&
                Objects.equals(urlOaipmh, that.urlOaipmh) &&
                Objects.equals(uriJournals, that.uriJournals) &&
                Objects.equals(physicalName, that.physicalName) &&
                Objects.equals(source, that.source) &&
                Objects.equals(software, that.software) &&
                Objects.equals(metadataFormat, that.metadataFormat) &&
                Objects.equals(description, that.description) &&
                Objects.equals(journal, that.journal) &&
                Objects.equals(pdfStatus, that.pdfStatus) &&
                Objects.equals(lastUpdateTime, that.lastUpdateTime) &&
                Objects.equals(repositoryLocation, that.repositoryLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, openDoarId, name, uri, urlHomepage, urlOaipmh, uriJournals, physicalName, source, software, metadataFormat, description, journal, roarId, baseId, pdfStatus, nrUpdates, disabled, lastUpdateTime, repositoryLocation);
    }
}
