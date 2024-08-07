package uk.ac.core.services.web.affiliations.model;

public class InputMetadata {

    private Integer coreId;
    private Integer repoId;
    private String doi;
    private String oai;
    private String title;
    private Integer year;

    public InputMetadata() {
    }

    public InputMetadata(Integer coreId, Integer repoId, String doi, String oai, String title, Integer year) {
        this.coreId = coreId;
        this.repoId = repoId;
        this.doi = doi;
        this.oai = oai;
        this.title = title;
        this.year = year;
    }

    public Integer getCoreId() {
        return coreId;
    }

    public void setCoreId(Integer coreId) {
        this.coreId = coreId;
    }

    public Integer getRepoId() {
        return repoId;
    }

    public void setRepoId(Integer repoId) {
        this.repoId = repoId;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getOai() {
        return oai;
    }

    public void setOai(String oai) {
        this.oai = oai;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "InputMetadata{" +
                "coreId=" + coreId +
                ", repoId=" + repoId +
                ", doi='" + doi + '\'' +
                ", oai='" + oai + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                '}';
    }
}
