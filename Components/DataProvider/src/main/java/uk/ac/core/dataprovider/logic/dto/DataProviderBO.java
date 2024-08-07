package uk.ac.core.dataprovider.logic.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class DataProviderBO {

    @JsonIgnore
    @Nullable
    private Long dataProviderId;

    private String oaiPmhEndpoint;
    private String name;
    private String description;
    private Boolean journal;
    private boolean disabled;
    private String countryCode;
    private Double longitude;
    private Double latitude;
    private String software;
    private String homepage;
    private String metadataFormat;
    private String source;
    private LocalDateTime createdAt;

    @Nullable
    private Boolean enabled;

    public DataProviderBO() {

    }

    public DataProviderBO(String oaiPmhEndpoint, String name, String description, boolean journal, String countryCode) {
        this.oaiPmhEndpoint = oaiPmhEndpoint;
        this.name = name;
        this.description = description;
        this.journal = journal;
        this.countryCode = countryCode;
    }

    public Long getId() {
        return dataProviderId;
    }

    public void setId(Long dataProviderId) {
        this.dataProviderId = dataProviderId;
    }

    public String getOaiPmhEndpoint() {
        return oaiPmhEndpoint;
    }

    public void setOaiPmhEndpoint(String oaiPmhEndpoint) {
        this.oaiPmhEndpoint = oaiPmhEndpoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setJournal(boolean journal) {
        this.journal = journal;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getMetadataFormat() {
        return metadataFormat;
    }

    public void setMetadataFormat(String metadataFormat) {
        this.metadataFormat = metadataFormat;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Nullable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(@Nullable Boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // used for deduplication comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataProviderBO that = (DataProviderBO) o;

        return Objects.equals(oaiPmhEndpoint, that.oaiPmhEndpoint);
    }

    @Override
    public int hashCode() {
        return oaiPmhEndpoint != null ? oaiPmhEndpoint.hashCode() : 0;
    }
}
