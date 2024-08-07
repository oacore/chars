package uk.ac.core.elasticsearch.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import java.util.List;
import java.util.Set;
import static org.springframework.data.elasticsearch.annotations.FieldType.Date;
import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Long;
import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

/**
 *
 * @author mTarasyuk
 */
@Document(indexName = "#{@worksIndexName}", type = "works")
public class ElasticSearchWorkMetadata {

    @Id
    @Field(type = Long)
    private Integer id;

    @MultiField(
            mainField = @Field(type = Text, analyzer = "standard"),
            otherFields = {
            @InnerField(suffix = "raw", type = Keyword)
    })
    private String title;

    @MultiField(
            mainField = @Field(type = Text),
            otherFields = {
                    @InnerField(suffix = "raw", type = Keyword)
            })
    private List<String> authors;
    
    @Field(type = Text, analyzer = "standard")
    private String description;

    @Field(type = Keyword)
    private Set<String> contributors;

    @Field(type = Long)
    private Set<Integer> dataProviders;

    @Field(type = Text)
    private String fullText;

    @Field(type = Text)
    private Set<String> sourceFullTextUrls;

    @Field(type = Nested)
    private ElasticSearchLanguage language;

    @Field(type = Keyword)
    private String downloadUrl;

    @Field(type = Long)
    private Integer citationCount;

    @Field(type = Nested)
    private List<ElasticSearchWorkReference> references;

    @Field(type = Keyword)
    private String documentType;

    @Field(type = Nested)
    private Set<ElasticSearchJournal> journals;

    private Publisher publisher;

    @Field(type = Date)
    private String acceptedDate;

    @Field(type = Date)
    private String depositedDate;

    @Field(type = Date)
    private String publishedDate;

    @Field(type = Long)
    private Integer yearPublished;

    @Field(type = Date )
    private String createdDate;

    @Field(type = Date)
    private String updatedDate;

    @Field(type = Date)
    private String indexedDate;

    @MultiField(
            mainField = @Field(type = Keyword),
            otherFields = {
                    @InnerField(suffix = "raw", type = Text)
            })
    private String doi;

    @MultiField(
            mainField = @Field(type = Keyword),
            otherFields = {
                    @InnerField(suffix = "raw", type = Text)
            })
    private String magId;

    @MultiField(
            mainField = @Field(type = Keyword),
            otherFields = {
                    @InnerField(suffix = "raw", type = Text)
            })
    private String arxivId;

    @MultiField(
            mainField = @Field(type = Keyword),
            otherFields = {
                    @InnerField(suffix = "raw", type = Text)
            })
    private String pubmedId;

    @MultiField(
            mainField = @Field(type = Keyword),
            otherFields = {
                    @InnerField(suffix = "raw", type = Text)
            })
    private Set<String> oaiIds;

    @Field(type = Keyword)
    private Set<String> coreIds;

    @Field(type = Nested)
    private Set<Identifier> identifiers;

    @Field(type = Keyword)
    private String fieldsOfStudy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getContributors() {
        return contributors;
    }

    public void setContributors(Set<String> contributors) {
        this.contributors = contributors;
    }

    public Set<Integer> getDataProviders() {
        return dataProviders;
    }

    public void setDataProviders(Set<Integer> dataProviders) {
        this.dataProviders = dataProviders;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public Set<String> getSourceFullTextUrls() {
        return sourceFullTextUrls;
    }

    public void setSourceFullTextUrls(Set<String> sourceFullTextUrls) {
        this.sourceFullTextUrls = sourceFullTextUrls;
    }

    public ElasticSearchLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ElasticSearchLanguage language) {
        this.language = language;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Integer getCitationCount() {
        return citationCount;
    }

    public void setCitationCount(Integer citationCount) {
        this.citationCount = citationCount;
    }

    public List<ElasticSearchWorkReference> getReferences() {
        return references;
    }

    public void setReferences(List<ElasticSearchWorkReference> references) {
        this.references = references;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Set<ElasticSearchJournal> getJournals() {
        return journals;
    }

    public void setJournals(Set<ElasticSearchJournal> journals) {
        this.journals = journals;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public String getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(String acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public String getDepositedDate() {
        return depositedDate;
    }

    public void setDepositedDate(String depositedDate) {
        this.depositedDate = depositedDate;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Integer getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(Integer yearPublished) {
        this.yearPublished = yearPublished;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getMagId() {
        return magId;
    }

    public void setMagId(String magId) {
        this.magId = magId;
    }

    public String getArxivId() {
        return arxivId;
    }

    public void setArxivId(String arxivId) {
        this.arxivId = arxivId;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public Set<String> getOaiIds() {
        return oaiIds;
    }

    public void setOaiIds(Set<String> oaiIds) {
        this.oaiIds = oaiIds;
    }

    public Set<String> getCoreIds() {
        return coreIds;
    }

    public void setCoreIds(Set<String> coreIds) {
        this.coreIds = coreIds;
    }

    public Set<Identifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Set<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public String getIndexedDate() { return indexedDate; }

    public void setIndexedDate(String indexedDate) { this.indexedDate = indexedDate; }

    public String getFieldsOfStudy() {
        return fieldsOfStudy;
    }

    public void setFieldsOfStudy(String fieldsOfStudy) {
        this.fieldsOfStudy = fieldsOfStudy;
    }
}
