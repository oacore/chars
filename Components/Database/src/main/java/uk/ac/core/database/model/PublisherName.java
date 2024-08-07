package uk.ac.core.database.model;

import java.util.List;

public class PublisherName {
    private long id;
    private String doiPrefix;
    private String primaryName;
    private List<String> names;

    public PublisherName() {
    }

    public PublisherName(String doiPrefix, String primaryName, List<String> names) {
        this.doiPrefix = doiPrefix;
        this.primaryName = primaryName;
        this.names = names;
    }

    public PublisherName(long id, String doiPrefix, String primaryName, List<String> names) {
        this.id = id;
        this.doiPrefix = doiPrefix;
        this.primaryName = primaryName;
        this.names = names;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDoiPrefix() {
        return doiPrefix;
    }

    public void setDoiPrefix(String doiPrefix) {
        this.doiPrefix = doiPrefix;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    @Override
    public String toString() {
        return "PublisherName{" +
                "id=" + id +
                ", doiPrefix='" + doiPrefix + '\'' +
                ", primaryName='" + primaryName + '\'' +
                ", names=" + names +
                '}';
    }
}
