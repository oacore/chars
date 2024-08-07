package uk.ac.core.database.model;

import java.util.List;

public class JournalISSN {
    private long id;
    private String title;
    private String subject;
    private List<String> issnList;

    public JournalISSN() {
    }

    public JournalISSN(String title, String subject, List<String> issnList) {
        this.title = title;
        this.subject = subject;
        this.issnList = issnList;
    }

    public JournalISSN(long id, String title, String subject, List<String> issnList) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.issnList = issnList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getIssnList() {
        return issnList;
    }

    public void setIssnList(List<String> issnList) {
        this.issnList = issnList;
    }
}
