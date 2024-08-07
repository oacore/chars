package uk.ac.core.elasticsearch.services.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import uk.ac.core.elasticsearch.entities.ElasticSearchJournal;
import java.util.List;

public class CompleteArticleBO {

    @JsonUnwrapped
    private CompactArticleBO compactArticleBO;
    private String doi;
    private String oai;
    private String title;
    private List<String> authors;
    private List<String> contributors;
    private String datePublished;
    private String description;
    private String documentType;
    private Double documentTypeConfidence;
    private String downloadUrl;
    private String fullTextIdentifier;
    private String pdfHashValue;
    private String publisher;
    private String rawRecordXml;
    private List<String> relations;
    private List<ElasticSearchJournal> journals;
    private String language;
    private Integer year;
    private List<String> topics;
    private List<String> subjects;
    private List<Repository> repositories;
    private List<Citation> references;

    public static class Citation {

        private long coreId;
        private String title;
        private List<String> authors;
        private String date;
        private String doi;
        private String raw;
        private List<Integer> cites;

        public Citation() {
        }

        public Citation(long id, String title, List<String> authors, String date, String doi, String raw, List<Integer> cites) {
            this.coreId = id;
            this.title = title;
            this.authors = authors;
            this.date = date;
            this.doi = doi;
            this.raw = raw;
            this.cites = cites;
        }

        public long getCoreId() {
            return coreId;
        }

        public void setCoreId(long coreId) {
            this.coreId = coreId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getAuthors() {
            return authors;
        }

        public void setAuthors(List<String> authors) {
            this.authors = authors;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDoi() {
            return doi;
        }

        public void setDoi(String doi) {
            this.doi = doi;
        }

        public String getRaw() {
            return raw;
        }

        public void setRaw(String raw) {
            this.raw = raw;
        }

        public List<Integer> getCites() {
            return cites;
        }

        public void setCites(List<Integer> cites) {
            this.cites = cites;
        }
    }

    public static class Repository {
        private int id;
        private String name;
        private Integer openDoarId;

        public Repository() {
        }

        public Repository(int id, String name, Integer openDoarId) {
            this.id = id;
            this.name = name;
            this.openDoarId = openDoarId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getOpenDoarId() {
            return openDoarId;
        }

        public void setOpenDoarId(Integer openDoarId) {
            this.openDoarId = openDoarId;
        }
    }

    public CompactArticleBO getCompactArticleBO() {
        return compactArticleBO;
    }

    public void setCompactArticleBO(CompactArticleBO compactArticleBO) {
        this.compactArticleBO = compactArticleBO;
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

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<String> getContributors() {
        return contributors;
    }

    public void setContributors(List<String> contributors) {
        this.contributors = contributors;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Double getDocumentTypeConfidence() {
        return documentTypeConfidence;
    }

    public void setDocumentTypeConfidence(Double documentTypeConfidence) {
        this.documentTypeConfidence = documentTypeConfidence;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFullTextIdentifier() {
        return fullTextIdentifier;
    }

    public void setFullTextIdentifier(String fullTextIdentifier) {
        this.fullTextIdentifier = fullTextIdentifier;
    }

    public String getPdfHashValue() {
        return pdfHashValue;
    }

    public void setPdfHashValue(String pdfHashValue) {
        this.pdfHashValue = pdfHashValue;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getRawRecordXml() {
        return rawRecordXml;
    }

    public void setRawRecordXml(String rawRecordXml) {
        this.rawRecordXml = rawRecordXml;
    }

    public List<String> getRelations() {
        return relations;
    }

    public void setRelations(List<String> relations) {
        this.relations = relations;
    }

    public List<ElasticSearchJournal> getJournals() {
        return journals;
    }

    public void setJournals(List<ElasticSearchJournal> journals) {
        this.journals = journals;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }

    public List<Citation> getReferences() {
        return references;
    }

    public void setReferences(List<Citation> references) {
        this.references = references;
    }
}
