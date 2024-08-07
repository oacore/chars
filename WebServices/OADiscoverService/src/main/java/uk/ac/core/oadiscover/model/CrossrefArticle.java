package uk.ac.core.oadiscover.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.elasticsearch.annotations.Document;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DOI",
    "subtitle",
    "issued",
    "prefix",
    "subject",
    "author",
    "reference-count",
    "ISSN",
    "member",
    "source",
    "score",
    "deposited",
    "indexed",
    "type",
    "URL",
    "volume",
    "link",
    "published-print",
    "publisher",
    "license",
    "created",
    "issue",
    "title",
    "alternative-id",
    "container-title",
    "page"
})
@Document(indexName = "crossref", type = "article")
public class CrossrefArticle {

    @JsonProperty("DOI")
    private String dOI;
    @JsonProperty("subtitle")
    private List<Object> subtitle = null;
    @JsonProperty("issued")
    private Issued issued;
    @JsonProperty("prefix")
    private String prefix;
    @JsonProperty("subject")
    private List<String> subject = null;
    @JsonProperty("author")
    private List<Author> author = null;
    @JsonProperty("reference-count")
    private long referenceCount;
    @JsonProperty("ISSN")
    private List<String> iSSN = null;
    @JsonProperty("member")
    private String member;
    @JsonProperty("source")
    private String source;
    @JsonProperty("score")
    private double score;
    @JsonProperty("deposited")
    private Deposited deposited;
    @JsonProperty("indexed")
    private Indexed indexed;
    @JsonProperty("type")
    private String type;
    @JsonProperty("URL")
    private String uRL;
    @JsonProperty("volume")
    private String volume;
    @JsonProperty("link")
    private List<Link> link = null;
    @JsonProperty("published-print")
    private PublishedPrint publishedPrint;
    @JsonProperty("publisher")
    private String publisher;
    @JsonProperty("license")
    private List<License> license = null;
    @JsonProperty("created")
    private Created created;
    @JsonProperty("issue")
    private String issue;
    @JsonProperty("title")
    private List<String> title = null;
    @JsonProperty("alternative-id")
    private List<String> alternativeId = null;
    @JsonProperty("container-title")
    private List<String> containerTitle = null;
    @JsonProperty("page")
    private String page;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public CrossrefArticle() {
    }

    /**
     *
     * @param indexed
     * @param subject
     * @param link
     * @param issue
     * @param score
     * @param type
     * @param publisher
     * @param author
     * @param title
     * @param page
     * @param created
     * @param containerTitle
     * @param referenceCount
     * @param license
     * @param member
     * @param alternativeId
     * @param uRL
     * @param deposited
     * @param issued
     * @param publishedPrint
     * @param iSSN
     * @param source
     * @param dOI
     * @param prefix
     * @param subtitle
     * @param volume
     */
    public CrossrefArticle(String dOI, List<Object> subtitle, Issued issued, String prefix, List<String> subject, List<Author> author, long referenceCount, List<String> iSSN, String member, String source, double score, Deposited deposited, Indexed indexed, String type, String uRL, String volume, List<Link> link, PublishedPrint publishedPrint, String publisher, List<License> license, Created created, String issue, List<String> title, List<String> alternativeId, List<String> containerTitle, String page) {
        super();
        this.dOI = dOI;
        this.subtitle = subtitle;
        this.issued = issued;
        this.prefix = prefix;
        this.subject = subject;
        this.author = author;
        this.referenceCount = referenceCount;
        this.iSSN = iSSN;
        this.member = member;
        this.source = source;
        this.score = score;
        this.deposited = deposited;
        this.indexed = indexed;
        this.type = type;
        this.uRL = uRL;
        this.volume = volume;
        this.link = link;
        this.publishedPrint = publishedPrint;
        this.publisher = publisher;
        this.license = license;
        this.created = created;
        this.issue = issue;
        this.title = title;
        this.alternativeId = alternativeId;
        this.containerTitle = containerTitle;
        this.page = page;
    }

    @JsonProperty("DOI")
    public String getDOI() {
        return dOI;
    }

    @JsonProperty("DOI")
    public void setDOI(String dOI) {
        this.dOI = dOI;
    }

    @JsonProperty("subtitle")
    public List<Object> getSubtitle() {
        return subtitle;
    }

    @JsonProperty("subtitle")
    public void setSubtitle(List<Object> subtitle) {
        this.subtitle = subtitle;
    }

    @JsonProperty("issued")
    public Issued getIssued() {
        return issued;
    }

    @JsonProperty("issued")
    public void setIssued(Issued issued) {
        this.issued = issued;
    }

    @JsonProperty("prefix")
    public String getPrefix() {
        return prefix;
    }

    @JsonProperty("prefix")
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @JsonProperty("subject")
    public List<String> getSubject() {
        return subject;
    }

    @JsonProperty("subject")
    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    @JsonProperty("author")
    public List<Author> getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    @JsonProperty("reference-count")
    public long getReferenceCount() {
        return referenceCount;
    }

    @JsonProperty("reference-count")
    public void setReferenceCount(long referenceCount) {
        this.referenceCount = referenceCount;
    }

    @JsonProperty("ISSN")
    public List<String> getISSN() {
        return iSSN;
    }

    @JsonProperty("ISSN")
    public void setISSN(List<String> iSSN) {
        this.iSSN = iSSN;
    }

    @JsonProperty("member")
    public String getMember() {
        return member;
    }

    @JsonProperty("member")
    public void setMember(String member) {
        this.member = member;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("score")
    public double getScore() {
        return score;
    }

    @JsonProperty("score")
    public void setScore(double score) {
        this.score = score;
    }

    @JsonProperty("deposited")
    public Deposited getDeposited() {
        return deposited;
    }

    @JsonProperty("deposited")
    public void setDeposited(Deposited deposited) {
        this.deposited = deposited;
    }

    @JsonProperty("indexed")
    public Indexed getIndexed() {
        return indexed;
    }

    @JsonProperty("indexed")
    public void setIndexed(Indexed indexed) {
        this.indexed = indexed;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("URL")
    public String getURL() {
        return uRL;
    }

    @JsonProperty("URL")
    public void setURL(String uRL) {
        this.uRL = uRL;
    }

    @JsonProperty("volume")
    public String getVolume() {
        return volume;
    }

    @JsonProperty("volume")
    public void setVolume(String volume) {
        this.volume = volume;
    }

    @JsonProperty("link")
    public List<Link> getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(List<Link> link) {
        this.link = link;
    }

    @JsonProperty("published-print")
    public PublishedPrint getPublishedPrint() {
        return publishedPrint;
    }

    @JsonProperty("published-print")
    public void setPublishedPrint(PublishedPrint publishedPrint) {
        this.publishedPrint = publishedPrint;
    }

    @JsonProperty("publisher")
    public String getPublisher() {
        return publisher;
    }

    @JsonProperty("publisher")
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @JsonProperty("license")
    public List<License> getLicense() {
        return license;
    }

    @JsonProperty("license")
    public void setLicense(List<License> license) {
        this.license = license;
    }

    @JsonProperty("created")
    public Created getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(Created created) {
        this.created = created;
    }

    @JsonProperty("issue")
    public String getIssue() {
        return issue;
    }

    @JsonProperty("issue")
    public void setIssue(String issue) {
        this.issue = issue;
    }

    @JsonProperty("title")
    public List<String> getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(List<String> title) {
        this.title = title;
    }

    @JsonProperty("alternative-id")
    public List<String> getAlternativeId() {
        return alternativeId;
    }

    @JsonProperty("alternative-id")
    public void setAlternativeId(List<String> alternativeId) {
        this.alternativeId = alternativeId;
    }

    @JsonProperty("container-title")
    public List<String> getContainerTitle() {
        return containerTitle;
    }

    @JsonProperty("container-title")
    public void setContainerTitle(List<String> containerTitle) {
        this.containerTitle = containerTitle;
    }

    @JsonProperty("page")
    public String getPage() {
        return page;
    }

    @JsonProperty("page")
    public void setPage(String page) {
        this.page = page;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "CrossrefArticle{" + "dOI=" + dOI + ", subtitle=" + subtitle + ", issued=" + issued + ", prefix=" + prefix + ", subject=" + subject + ", author=" + author + ", referenceCount=" + referenceCount + ", iSSN=" + iSSN + ", member=" + member + ", source=" + source + ", score=" + score + ", deposited=" + deposited + ", indexed=" + indexed + ", type=" + type + ", uRL=" + uRL + ", volume=" + volume + ", link=" + link + ", publishedPrint=" + publishedPrint + ", publisher=" + publisher + ", license=" + license + ", created=" + created + ", issue=" + issue + ", title=" + title + ", alternativeId=" + alternativeId + ", containerTitle=" + containerTitle + ", page=" + page + ", additionalProperties=" + additionalProperties + '}';
    }

}
