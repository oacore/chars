package uk.ac.core.elasticsearch.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.legacy.LegacyRepository;
import uk.ac.core.common.model.legacy.SimilarDocument;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author la4227 <lucas.anastasiou@open.ac.uk>
 */
@Document(indexName = "#{@indexName}", type = "article", useServerConfiguration = true, createIndex = false)
@Setting(settingPath = "/elasticsearch/mappings/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/articles.json")
public class ElasticSearchArticleMetadata {

    @Id
    private String id;

    private List<String> authors;

    private List<ElasticSearchCitation> citations;

    private List<String> contributors;

    private String datePublished;

    private String deleted;

    private String description;

    private String fullText;

    private String fullTextIdentifier;

    @Field(type = FieldType.Keyword)
    private List<String> identifiers;//doi,..,core-id,..

    private List<ElasticSearchJournal> journals;

    private ElasticSearchLanguage language;
    //it's a unique ID to identify documents with duplicates, all documents with the same duplicateId can be considered duplicate
    private String duplicateId;

    private String publisher;

    private String rawRecordXml;

    private List<String> relations;

    private List<LegacyRepository> repositories;

    private ElasticSearchRepositoryDocument repositoryDocument;

    private List<SimilarDocument> similarities;

    private List<String> subjects;

    private String title;

    private List<String> topics;

    private List<String> types;

    private List<String> urls;

    private Integer year;

    private String doi;

    private String oai;

    private String downloadUrl;

    private String pdfHashValue;

    private String documentType;

    private Double documentTypeConfidence;

    private Integer citationCount;

    private Integer estimatedCitationCount;

    private Timestamp acceptedDate;

    private Timestamp depositedDate;

    private Timestamp publishedDate;

    private String issn;

    private int attachmentCount;

    private Timestamp repositoryPublicReleaseDate;

    private ElasticSearchExtendedMetadataAttributes extendedMetadataAttributes;

    private ElasticSearchCrossrefDocument crossrefDocument;
    private ElasticSearchMAGDocument magDocument;

    private List<OrcidAuthor> orcidAuthors;

    private List<String> setSpecs;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<ElasticSearchCitation> getCitations() {
        return citations;
    }

    public void setCitations(List<ElasticSearchCitation> citations) {
        this.citations = citations;
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

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(DeletedStatus deleted) {
        this.deleted = deleted.name();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public List<ElasticSearchJournal> getJournals() {
        return journals;
    }

    public void setJournals(List<ElasticSearchJournal> journals) {
        this.journals = journals;
    }

    public ElasticSearchLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ElasticSearchLanguage language) {
        this.language = language;
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

    public List<LegacyRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<LegacyRepository> repositories) {
        this.repositories = repositories;
    }

    public ElasticSearchRepositoryDocument getRepositoryDocument() {
        return repositoryDocument;
    }

    public void setRepositoryDocument(ElasticSearchRepositoryDocument repositoryDocument) {
        this.repositoryDocument = repositoryDocument;
    }

    /**
     * gets the URL of the original fulltext PDF
     *
     * @return
     */
    public String getFullTextIdentifier() {
        return fullTextIdentifier;
    }

    /**
     * Sets the URL to the original fulltext PDF
     *
     * @param fullTextIdentifier
     */
    public void setFullTextIdentifier(String fullTextIdentifier) {
        this.fullTextIdentifier = fullTextIdentifier;
    }

    public String getDuplicateId() {
        return duplicateId;
    }

    public void setDuplicateId(String duplicateId) {
        this.duplicateId = duplicateId;
    }

    public List<SimilarDocument> getSimilarities() {
        return similarities;
    }

    public void setSimilarities(List<SimilarDocument> similarities) {
        this.similarities = similarities;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPdfHashValue() {
        return pdfHashValue;
    }

    public void setPdfHashValue(String pdfHashValue) {
        this.pdfHashValue = pdfHashValue;
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

    public Integer getCitationCount() {
        return citationCount;
    }

    public void setCitationCount(Integer citationCount) {
        this.citationCount = citationCount;
    }

    public Integer getEstimatedCitationCount() {
        return estimatedCitationCount;
    }

    public void setEstimatedCitationCount(Integer estimatedCitationCount) {
        this.estimatedCitationCount = estimatedCitationCount;
    }

    public Timestamp getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(Timestamp acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public Timestamp getDepositedDate() {
        return depositedDate;
    }

    public void setDepositedDate(Timestamp depositedDate) {
        this.depositedDate = depositedDate;
    }

    public Timestamp getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Timestamp publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public ElasticSearchCrossrefDocument getCrossrefDocument() {
        return crossrefDocument;
    }

    public void setCrossrefDocument(ElasticSearchCrossrefDocument crossrefDocument) {
        this.crossrefDocument = crossrefDocument;
    }

    public ElasticSearchMAGDocument getMagDocument() {
        return magDocument;
    }

    public void setMagDocument(ElasticSearchMAGDocument magDocument) {
        this.magDocument = magDocument;
    }

    public List<OrcidAuthor> getOrcidAuthors() {
        return orcidAuthors;
    }

    public void setOrcidAuthors(List<OrcidAuthor> orcidAuthors) {
        this.orcidAuthors = orcidAuthors;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public ElasticSearchExtendedMetadataAttributes getExtendedMetadataAttributes() {
        return extendedMetadataAttributes;
    }

    public void setExtendedMetadataAttributes(ElasticSearchExtendedMetadataAttributes extendedMetadataAttributes) {
        this.extendedMetadataAttributes = extendedMetadataAttributes;
    }

    public int getPdfStatus() {
        return repositoryDocument.getPdfStatus();
    }

    public int getTextStatus() {
        return repositoryDocument.getTextStatus();
    }

    public Timestamp getRepositoryPublicReleaseDate() {
        return repositoryPublicReleaseDate;
    }

    public void setRepositoryPublicReleaseDate(Timestamp repositoryPublicReleaseDate) {
        this.repositoryPublicReleaseDate = repositoryPublicReleaseDate;
    }

    public List<String> getSetSpecs() {
        return setSpecs;
    }

    public void setSetSpecs(List<String> setSpecs) {
        this.setSpecs = setSpecs;
    }

    @Override
    public String toString() {
        return "ElasticSearchArticleMetadata{" +
                "id='" + id + '\'' +
                ", authors=" + authors +
                ", citations=" + citations +
                ", contributors=" + contributors +
                ", datePublished='" + datePublished + '\'' +
                ", deleted='" + deleted + '\'' +
                ", description='" + description + '\'' +
                ", fullText='" + fullText + '\'' +
                ", fullTextIdentifier='" + fullTextIdentifier + '\'' +
                ", identifiers=" + identifiers +
                ", journals=" + journals +
                ", language=" + language +
                ", duplicateId='" + duplicateId + '\'' +
                ", publisher='" + publisher + '\'' +
                ", rawRecordXml='" + rawRecordXml + '\'' +
                ", relations=" + relations +
                ", repositories=" + repositories +
                ", repositoryDocument=" + repositoryDocument +
                ", similarities=" + similarities +
                ", subjects=" + subjects +
                ", title='" + title + '\'' +
                ", topics=" + topics +
                ", types=" + types +
                ", urls=" + urls +
                ", year=" + year +
                ", doi='" + doi + '\'' +
                ", oai='" + oai + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", pdfHashValue='" + pdfHashValue + '\'' +
                ", documentType='" + documentType + '\'' +
                ", documentTypeConfidence=" + documentTypeConfidence +
                ", citationCount=" + citationCount +
                ", estimatedCitationCount=" + estimatedCitationCount +
                ", acceptedDate=" + acceptedDate +
                ", depositedDate=" + depositedDate +
                ", publishedDate=" + publishedDate +
                ", issn='" + issn + '\'' +
                ", attachmentCount=" + attachmentCount +
                ", repositoryPublicReleaseDate=" + repositoryPublicReleaseDate +
                ", extendedMetadataAttributes=" + extendedMetadataAttributes +
                ", crossrefDocument=" + crossrefDocument +
                ", magDocument=" + magDocument +
                ", orcidAuthors=" + orcidAuthors +
                ", setSpecs=" + setSpecs +
                '}';
    }
}
