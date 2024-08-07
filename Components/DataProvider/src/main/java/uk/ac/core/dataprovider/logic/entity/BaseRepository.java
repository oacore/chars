package uk.ac.core.dataprovider.logic.entity;

import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.*;

/**
 * BASE repository entity.
 */
@Entity
@Table(name = "repository_base")
public final class BaseRepository {

    @Id
    @Column(name = "id_base_repository")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_base")
    private String baseId;

    private String name;

    private String url;

    @Column(name = "has_oai_pmh_endpoint")
    private boolean oaiPmhEndpoint;

    @Column(name = "country")
    private String countryCode;

    @Column(name = "total_documents")
    private int totalDocuments;

    @Column(name = "total_open_access_documents")
    private int numberOfDocumentsWithOpenAccess;

    @Column(name = "base_url")
    private String baseUrl;

    private Double latitude;

    private Double longitude;

    @Column(name = "\"system\"")
    private String system;

    @Column(name = "imported_into_base")
    private LocalDate inBaseSince;

    @Column(name = "imported_into_core")
    private LocalDate inCoreSince;

    public BaseRepository() {
    }

    public BaseRepository(String name,
                          String url,
                          String countryCode,
                          int totalDocuments,
                          int numberOfDocumentsWithOpenAccess,
                          String baseUrl,
                          Double latitude,
                          Double longitude,
                          String system,
                          LocalDate inBaseSince,
                          LocalDate inCoreSince) {
        this.name = name;
        this.url = url;
        this.countryCode = countryCode;
        this.totalDocuments = totalDocuments;
        this.numberOfDocumentsWithOpenAccess = numberOfDocumentsWithOpenAccess;
        this.baseUrl = baseUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.system = system;
        this.inBaseSince = inBaseSince;
        this.inCoreSince = inCoreSince;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseId() {
        return baseId;
    }

    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean hasOaiPmhEndpoint() {
        return oaiPmhEndpoint;
    }

    public void setOaiPmhEndpointMarker(boolean hasOaiPmhEndpoint) {
        this.oaiPmhEndpoint = hasOaiPmhEndpoint;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(int totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public int getNumberOfDocumentsWithOpenAccess() {
        return numberOfDocumentsWithOpenAccess;
    }

    public void setNumberOfDocumentsWithOpenAccess(int numberOfDocumentsWithOpenAccess) {
        this.numberOfDocumentsWithOpenAccess = numberOfDocumentsWithOpenAccess;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public LocalDate getInBaseSince() {
        return inBaseSince;
    }

    public void setInBaseSince(LocalDate inBaseSince) {
        this.inBaseSince = inBaseSince;
    }

    public LocalDate getInCoreSince() {
        return inCoreSince;
    }

    public void setInCoreSince(LocalDate inCoreSince) {
        this.inCoreSince = inCoreSince;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseRepository)) return false;
        BaseRepository that = (BaseRepository) o;
        return baseId.equals(that.baseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseId);
    }

    @Override
    public String toString() {
        return "BaseRepository{" +
                "name='" + name + '\'' +
                '}';
    }

}