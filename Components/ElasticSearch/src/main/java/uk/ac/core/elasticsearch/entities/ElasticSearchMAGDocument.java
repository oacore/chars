package uk.ac.core.elasticsearch.entities;

/**
 *
 * @author mc26486
 */
public class ElasticSearchMAGDocument {

    private String magId;
    private Integer citationsCount;
    private Integer estimatedCitationsCount;

    public String getMagId() {
        return magId;
    }

    public void setMagId(String magId) {
        this.magId = magId;
    }

    public Integer getCitationsCount() {
        return citationsCount;
    }

    public void setCitationsCount(Integer citationsCount) {
        this.citationsCount = citationsCount;
    }

    public Integer getEstimatedCitationsCount() {
        return estimatedCitationsCount;
    }

    public void setEstimatedCitationsCount(Integer estimatedCitationsCount) {
        this.estimatedCitationsCount = estimatedCitationsCount;
    }

}
