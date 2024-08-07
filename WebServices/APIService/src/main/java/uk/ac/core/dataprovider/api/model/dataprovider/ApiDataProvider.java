package uk.ac.core.dataprovider.api.model.dataprovider;

import org.hibernate.validator.constraints.URL;
import uk.ac.core.dataprovider.api.handler.validation.CountryCode;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Abstract api dataprovider class.
 */
public abstract class ApiDataProvider {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @URL
    private String oaiPmhEndpoint;

    private Boolean journal=false;

    @Valid
    private ApiDataProvider.LocationInfo location;

    @Size(max = 2000)
    private String description;

    @URL
    private String homepage;

    private String metadataFormat="oai_dc";

    private String software;

    private String source;

    public ApiDataProvider() {

    }

    public static class LocationInfo {

        @CountryCode
        private String countryCode;

        @Min(value = -90)
        @Max(value = 90)
        private Double latitude;

        @Min(value = -180)
        @Max(value = 180)
        private Double longitude;

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
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

    public String getOaiPmhEndpoint() {
        return oaiPmhEndpoint;
    }

    public void setOaiPmhEndpoint(String oaiPmhEndpoint) {
        this.oaiPmhEndpoint = oaiPmhEndpoint;
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

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocationInfo getLocation() {
        return location;
    }

    public void setLocation(LocationInfo repositoryLocation) {
        this.location = repositoryLocation;
    }

    public Boolean isJournal() {
        return journal;
    }

    public void setJournal(Boolean journal) {
        this.journal = journal;
    }
}