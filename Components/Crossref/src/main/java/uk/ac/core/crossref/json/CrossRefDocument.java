package uk.ac.core.crossref.json;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "indexed",
        "reference-count",
        "publisher",
        "issue",
        "license",
        "content-domain",
        "short-container-title",
        "published",
        "DOI",
        "type",
        "created",
        "source",
        "title",
        "prefix",
        "volume",
        "author",
        "member",
        "container-title",
        "original-title",
        "link",
        "deposited",
        "score",
        "subtitle",
        "short-title",
        "issued",
        "alternative-id",
        "URL",
        "ISSN",
        "subject",
        "accepted",
        "abstract",
        "language"
})
public class CrossRefDocument {

    @JsonProperty("indexed")
    private Indexed indexed;
    @JsonProperty("reference-count")
    private Integer referenceCount;
    @JsonProperty("publisher")
    private String publisher;
    @JsonProperty("issue")
    private String issue;
    @JsonProperty("license")
    private List<License> license = null;
    @JsonProperty("content-domain")
    private ContentDomain contentDomain;
    @JsonProperty("short-container-title")
    private List<String> shortContainerTitle = null;
    @JsonProperty("published")
    private PublishedPrint publishedPrint;
    @JsonProperty("DOI")
    private String dOI;
    @JsonProperty("type")
    private String type;
    @JsonProperty("created")
    private Created created;
    @JsonProperty("source")
    private String source;
    @JsonProperty("title")
    private List<String> title = null;
    @JsonProperty("prefix")
    private String prefix;
    @JsonProperty("volume")
    private String volume;
    @JsonProperty("author")
    private List<Author> author = null;
    @JsonProperty("member")
    private String member;
    @JsonProperty("container-title")
    private List<String> containerTitle = null;
    @JsonProperty("original-title")
    private List<Object> originalTitle = null;
    @JsonProperty("link")
    private List<Link> link = null;
    @JsonProperty("deposited")
    private Deposited deposited;
    @JsonProperty("score")
    private Double score;
    @JsonProperty("subtitle")
    private List<Object> subtitle = null;
    @JsonProperty("short-title")
    private List<Object> shortTitle = null;
    @JsonProperty("issued")
    private Issued issued;
    @JsonProperty("alternative-id")
    private List<String> alternativeId = null;
    @JsonProperty("URL")
    private String uRL;
    @JsonProperty("ISSN")
    private List<String> iSSN = null;
    @JsonProperty("subject")
    private List<String> subject = null;
    @JsonProperty("accepted")
    private Issued accepted = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("abstract")
    private String description;
    @JsonProperty("language")
    private String language;

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("abstract")
    public String getDescription() {
        return description;
    }

    @JsonProperty("abstract")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("indexed")
    public Indexed getIndexed() {
        return indexed;
    }

    @JsonProperty("indexed")
    public void setIndexed(Indexed indexed) {
        this.indexed = indexed;
    }

    public CrossRefDocument withIndexed(Indexed indexed) {
        this.indexed = indexed;
        return this;
    }



    public static CrossRefDocument fromFile(Path filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(filePath.toFile(), CrossRefDocument.class);
    }

    public static CrossRefDocument fromString(String content) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(content, CrossRefDocument.class);
    }

    public static Timestamp datePartsToTimestamp(List<Integer> dateParts) {
        int year = 0, month = 1, day = 1;
        if (dateParts != null && dateParts.size() > 0 && dateParts.get(0) != null) {
            year = dateParts.get(0);
            if (dateParts.size() > 1) {
                month = dateParts.get(1);
            }
            if (dateParts.size() > 2) {
                day = dateParts.get(2);
            }
        }
        LocalDateTime localDateTime = LocalDateTime.of(year, Month.of(month), day, 0, 0);

        return Timestamp.valueOf(localDateTime);
    }

    @JsonProperty("reference-count")
    public Integer getReferenceCount() {
        return referenceCount;
    }

    @JsonProperty("reference-count")
    public void setReferenceCount(Integer referenceCount) {
        this.referenceCount = referenceCount;
    }

    public CrossRefDocument withReferenceCount(Integer referenceCount) {
        this.referenceCount = referenceCount;
        return this;
    }

    @JsonProperty("publisher")
    public String getPublisher() {
        return publisher;
    }

    @JsonProperty("publisher")
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public CrossRefDocument withPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    @JsonProperty("issue")
    public String getIssue() {
        return issue;
    }

    @JsonProperty("issue")
    public void setIssue(String issue) {
        this.issue = issue;
    }

    public CrossRefDocument withIssue(String issue) {
        this.issue = issue;
        return this;
    }

    @JsonProperty("license")
    public List<License> getLicense() {
        return license;
    }

    @JsonProperty("license")
    public void setLicense(List<License> license) {
        this.license = license;
    }

    public CrossRefDocument withLicense(List<License> license) {
        this.license = license;
        return this;
    }

    @JsonProperty("content-domain")
    public ContentDomain getContentDomain() {
        return contentDomain;
    }

    @JsonProperty("content-domain")
    public void setContentDomain(ContentDomain contentDomain) {
        this.contentDomain = contentDomain;
    }

    public CrossRefDocument withContentDomain(ContentDomain contentDomain) {
        this.contentDomain = contentDomain;
        return this;
    }

    @JsonProperty("short-container-title")
    public List<String> getShortContainerTitle() {
        return shortContainerTitle;
    }

    @JsonProperty("short-container-title")
    public void setShortContainerTitle(List<String> shortContainerTitle) {
        this.shortContainerTitle = shortContainerTitle;
    }

    public CrossRefDocument withShortContainerTitle(List<String> shortContainerTitle) {
        this.shortContainerTitle = shortContainerTitle;
        return this;
    }

    @JsonProperty("published")
    public PublishedPrint getPublishedPrint() {
        return publishedPrint;
    }

    @JsonProperty("published")
    public void setPublishedPrint(PublishedPrint publishedPrint) {
        this.publishedPrint = publishedPrint;
    }

    public CrossRefDocument withPublishedPrint(PublishedPrint publishedPrint) {
        this.publishedPrint = publishedPrint;
        return this;
    }

    @JsonProperty("DOI")
    public String getDOI() {
        return dOI;
    }

    @JsonProperty("DOI")
    public void setDOI(String dOI) {
        this.dOI = dOI;
    }

    public CrossRefDocument withDOI(String dOI) {
        this.dOI = dOI;
        return this;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    public CrossRefDocument withType(String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("created")
    public Created getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(Created created) {
        this.created = created;
    }

    public CrossRefDocument withCreated(Created created) {
        this.created = created;
        return this;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    public CrossRefDocument withSource(String source) {
        this.source = source;
        return this;
    }

    @JsonProperty("title")
    public List<String> getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(List<String> title) {
        this.title = title;
    }

    public CrossRefDocument withTitle(List<String> title) {
        this.title = title;
        return this;
    }

    @JsonProperty("prefix")
    public String getPrefix() {
        return prefix;
    }

    @JsonProperty("prefix")
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public CrossRefDocument withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @JsonProperty("volume")
    public String getVolume() {
        return volume;
    }

    @JsonProperty("volume")
    public void setVolume(String volume) {
        this.volume = volume;
    }

    public CrossRefDocument withVolume(String volume) {
        this.volume = volume;
        return this;
    }

    @JsonProperty("author")
    public List<Author> getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    public CrossRefDocument withAuthor(List<Author> author) {
        this.author = author;
        return this;
    }

    @JsonProperty("member")
    public String getMember() {
        return member;
    }

    @JsonProperty("member")
    public void setMember(String member) {
        this.member = member;
    }

    public CrossRefDocument withMember(String member) {
        this.member = member;
        return this;
    }

    @JsonProperty("container-title")
    public List<String> getContainerTitle() {
        return containerTitle;
    }

    @JsonProperty("container-title")
    public void setContainerTitle(List<String> containerTitle) {
        this.containerTitle = containerTitle;
    }

    public CrossRefDocument withContainerTitle(List<String> containerTitle) {
        this.containerTitle = containerTitle;
        return this;
    }

    @JsonProperty("original-title")
    public List<Object> getOriginalTitle() {
        return originalTitle;
    }

    @JsonProperty("original-title")
    public void setOriginalTitle(List<Object> originalTitle) {
        this.originalTitle = originalTitle;
    }

    public CrossRefDocument withOriginalTitle(List<Object> originalTitle) {
        this.originalTitle = originalTitle;
        return this;
    }

    @JsonProperty("link")
    public List<Link> getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(List<Link> link) {
        this.link = link;
    }

    public CrossRefDocument withLink(List<Link> link) {
        this.link = link;
        return this;
    }

    @JsonProperty("deposited")
    public Deposited getDeposited() {
        return deposited;
    }

    @JsonProperty("deposited")
    public void setDeposited(Deposited deposited) {
        this.deposited = deposited;
    }

    public CrossRefDocument withDeposited(Deposited deposited) {
        this.deposited = deposited;
        return this;
    }

    @JsonProperty("score")
    public Double getScore() {
        return score;
    }

    @JsonProperty("score")
    public void setScore(Double score) {
        this.score = score;
    }

    public CrossRefDocument withScore(Double score) {
        this.score = score;
        return this;
    }

    @JsonProperty("subtitle")
    public List<Object> getSubtitle() {
        return subtitle;
    }

    @JsonProperty("subtitle")
    public void setSubtitle(List<Object> subtitle) {
        this.subtitle = subtitle;
    }

    public CrossRefDocument withSubtitle(List<Object> subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    @JsonProperty("short-title")
    public List<Object> getShortTitle() {
        return shortTitle;
    }

    @JsonProperty("short-title")
    public void setShortTitle(List<Object> shortTitle) {
        this.shortTitle = shortTitle;
    }

    public CrossRefDocument withShortTitle(List<Object> shortTitle) {
        this.shortTitle = shortTitle;
        return this;
    }

    @JsonProperty("issued")
    public Issued getIssued() {
        return issued;
    }

    @JsonProperty("accepted")
    public Issued getAccepted() {
        return accepted;
    }

    @JsonProperty("accepted")
    public void setAccepted(Issued accepted) {
        this.accepted = accepted;
    }

    @JsonProperty("issued")
    public void setIssued(Issued issued) {
        this.issued = issued;
    }

    public CrossRefDocument withIssued(Issued issued) {
        this.issued = issued;
        return this;
    }

    @JsonProperty("alternative-id")
    public List<String> getAlternativeId() {
        return alternativeId;
    }

    @JsonProperty("alternative-id")
    public void setAlternativeId(List<String> alternativeId) {
        this.alternativeId = alternativeId;
    }

    public CrossRefDocument withAlternativeId(List<String> alternativeId) {
        this.alternativeId = alternativeId;
        return this;
    }

    @JsonProperty("URL")
    public String getURL() {
        return uRL;
    }

    @JsonProperty("URL")
    public void setURL(String uRL) {
        this.uRL = uRL;
    }

    public CrossRefDocument withURL(String uRL) {
        this.uRL = uRL;
        return this;
    }

    @JsonProperty("ISSN")
    public List<String> getISSN() {
        return iSSN;
    }

    @JsonProperty("ISSN")
    public void setISSN(List<String> iSSN) {
        this.iSSN = iSSN;
    }

    public CrossRefDocument withISSN(List<String> iSSN) {
        this.iSSN = iSSN;
        return this;
    }

    @JsonProperty("subject")
    public List<String> getSubject() {
        return subject;
    }

    @JsonProperty("subject")
    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    public CrossRefDocument withSubject(List<String> subject) {
        this.subject = subject;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public CrossRefDocument withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
