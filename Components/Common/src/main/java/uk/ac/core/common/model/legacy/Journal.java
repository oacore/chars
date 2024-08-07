package uk.ac.core.common.model.legacy;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Drahomira Herrmannova <d.herrmannova@gmail.com>
 */
public class Journal {

    private String title;

    private Integer repositoryId;

    private List<String> identifiers;

    private List<String> subjects;

    private String language;

    private String rights;

    private String publisher;

    /**
     * convenience field that indicates number of articles we posses of this
     * journal
     */
    private Integer numberOfPublications;

    public Journal() {
        this.identifiers = new LinkedList<>();
        this.subjects = new LinkedList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRights() {
        return rights;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public String getOai() {

        String prefix = "oai";

        for (String i : this.identifiers) {
            if (i == null || i.isEmpty() || prefix.length() > i.length()) {
                continue;
            }
            if (i.regionMatches(true, 0, prefix, 0, prefix.length())) {
                return i;
            }
        }
        return null;
    }

    public void addIdentifier(String identifier) {
        this.identifiers.add(identifier);
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void addSubject(String subject) {
        this.subjects.add(subject);
    }

    public Integer getNumberOfPublications() {
        return numberOfPublications;
    }

    public void setNumberOfPublications(Integer numberOfPublications) {
        this.numberOfPublications = numberOfPublications;
    }

    @Override
    public String toString() {
        return "Journal{" + "title=" + title + ", repositoryId=" + repositoryId + ", identifiers=" + identifiers + ", subjects=" + subjects + ", language=" + language + ", rights=" + rights + ", publisher=" + publisher + ", numberOfPublications=" + numberOfPublications + '}';
    }

}
