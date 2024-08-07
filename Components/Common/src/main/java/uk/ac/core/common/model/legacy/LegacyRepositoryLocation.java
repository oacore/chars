package uk.ac.core.common.model.legacy;

import java.io.Serializable;

/**
 *
 * @author scp334
 */
public class LegacyRepositoryLocation implements Serializable {

    private Integer idRepository;
    private String repositoryName;
    private String countryCode;
    private String country;
    private String latitude;
    private String longitude;

    /**
     *
     * Create empty Constructor
     */
    public LegacyRepositoryLocation() {
        this.idRepository = 0;
        this.repositoryName = "";
        this.countryCode = "";
        this.country = "";
        this.latitude = "";
        this.longitude = "";
    }
 
    
    public LegacyRepositoryLocation(Integer repositoryId, String country_code, String country, String latitude, String longitude) {
        this.idRepository = repositoryId;
        this.countryCode = country_code;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     *
     * @return
     */
    public Integer getRepositoryId() {
        return idRepository;
    }

    /**
     *
     * @param id
     */
    public void setRepositoryId(Integer id) {
        this.idRepository = id;
    }

    /**
     *
     * @return
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     *
     * @param country_code
     */
    public void setCountryCode(String country_code) {
        this.countryCode = country_code;
    }

    /**
     *
     * @return
     */
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = (latitude.length() > 9) ? latitude.substring(0, 9) : latitude;
    }

    /**
     *
     * @return
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = (longitude.length() > 9) ? longitude.substring(0, 9) : longitude;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
}
