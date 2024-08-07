package uk.ac.core.common.model.legacy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.article.PDFUrlSource;

/**
 *
 * @author lucasanastasiou
 */
public class ArticleMetadata extends ArticleMetadataBase {

    // article specific metadata
    private String publisher;
    private List<String> types;
    private String OAIIdentifier;
    private List<String> identifiers;
    private List<String> journalIdentifiers;
    private List<String> journalIssns;
    private Map<String, PDFUrlSource> pdfUrls;
    private String fullText;
    private Integer year = null;
    private List<String> topics;
    private Long size;
    private String formattedSize;
    // repository specific metadata
    //private RepositoryMetadata rm;
    //private Float score;
    private Float similarity = null;
    private List<String> relations;
    // digital object identifier
    private Boolean doiFromCrossRef;
    private String doiMetadataTag;
    private Date doiDatetimeResolved;
    // default deleted status value: 0 - not deleted
    private DeletedStatus deleted = DeletedStatus.ALLOWED;
    private boolean textExtracted = false;
    private List<Citation> citations;
    private Map<String, Integer> citationsWithDocIds;
    private Map<String, String> citationsDoiMap;
    // if this instance is an envelope for duplicate articles - ids of those articles
    private List<Integer> subIds;
    private Integer parentId;

    private List<SimilarDocument> similarities;
    private RepositoryDocument repositoryDocument;

    private Set<String> setNames;

    // URL for sending requests
    //private String baseUrl;
    /**
     * Set of article (e.g. "Health sciences" etc.).
     */
    private String docClass = null;
    /**
     * Subjects of article.
     */
    private List<String> subjects;

    private String snippet = null;
    private Language language = null;

    private String rawLanguage = null;

    /**
     * The raw metadata contained between the <record> tag in the original xml
     */
    private String rawRecordXml = null;
    private String documentType = null;
    private Double documentTypeConfidence = null;

    /**
     * The URL in the document.url table. This is the url were the downloaded
     * pdf was found
     */
    private String pdfDownloadedFrom = null;


    private String license = null;

    /*
     * METHODS
     * ***********************************************************************************
     */
    /**
     * Construction of the object without provided repository context.
     */
    public ArticleMetadata() {
        super();
        topics = new LinkedList();
        types = new LinkedList();
        relations = new LinkedList();
        citations = new LinkedList();
        citationsWithDocIds = new HashMap<String, Integer>();
        subjects = new LinkedList();

        subIds = new ArrayList();
        identifiers = new ArrayList();
        pdfUrls = new HashMap<String, PDFUrlSource>();
        contributors = new LinkedList();
        journalIdentifiers = new LinkedList<>();
        this.journalIssns = new LinkedList<>();
    }

    // parent of duplicates
    public ArticleMetadata(Integer id, List<ArticleMetadata> amDuplicates) {
        this();

        this.setId(id);

        for (ArticleMetadata amDuplicate : amDuplicates) {
            addDuplicateMetadata(amDuplicate);
        }
    }

    /*
     * AUTHORS
     * ***********************************************************************************
     */
    /**
     * Return list of authors with names saved as {first_name surname} instead
     * of {surname, first_name} which is the default.
     *
     * @return
     */
    public List<String> getAuthorsNamesReverted() {
        List<String> output = new ArrayList<String>();
        for (String author : this.authors) {
            output.add(this.formatName(author));
        }
        return output;
    }

    /*
     * YEAR
     * **************************************************************************************
     */
    public Integer getYear() {
        if (year == null) {
            year = getYearFromDate();
        }
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getRawRecordXml() {
        return rawRecordXml;
    }

    public void setRawRecordXml(String rawRecordXml) {
        this.rawRecordXml = rawRecordXml;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
        this.year = getYearFromDate();
    }

    private Integer getYearFromDate() {
        if (date != null) {
            if (date.contains("-")) {
                try {
                    return Integer.parseInt(date.substring(0, date.indexOf("-")));
                } catch (NumberFormatException ex) {
//                    Debugger.debug("Cannot extract the year of the article from " + date + " as numerical, will try with regex.", this.getClass());
                }
            }
            Pattern pattern = Pattern.compile("([12][0-9]{3})");
            Matcher matcher = pattern.matcher(date);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            }
        }
        return null;
    }

    /*
     * TOPIC
     * *************************************************************************************
     */
    public List<String> getTopics() {
        return this.topics;
    }

    public void addTopic(String topic) {
        topics.add(topic);
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    /*
     * SIZE
     * **************************************************************************************
     */
    public Long getSize() {
        return this.size;
    }

    public void setSize(Long size) {
        this.size = size;
        this.formattedSize = this.getFormattedSize(size);
    }

    private String getFormattedSize(Long size) {
        NumberFormat format = new DecimalFormat(".0");
        StringBuilder sb = new StringBuilder();
        if (size == 0 || size == null) {
            return null;
        } else if (size < 1048576) {
            sb.append(format.format(size / 1024));
            sb.append(" kB");
        } else {
            sb.append(format.format(size / 1048576));
            sb.append(" MB");
        }
        return sb.toString();
    }

    public String getFormattedSize() {
        return formattedSize;
    }

    public void setFormattedSize(String formattedSize) {
        this.formattedSize = formattedSize;
    }

    /*
     * PUBLISHER
     * *********************************************************************************
     */
    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /*
     * OAI
     * ***************************************************************************************
     */
    public String getOAIIdentifier() {
        return this.OAIIdentifier;
    }

    /*
     * OAI
     * ***************************************************************************************
     */
    public void setOAIIdentifier(String OaiIdentifier) {
        this.OAIIdentifier = OaiIdentifier;
    }


    /*
     * get Identifiers (what is an Identifier?)
     * @deprecated Be explicit and use getOAIIdentifier instead
     * ***************************************************************************************
     */
    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public void setIdentifier(String identifier) {
        if (identifier == null) {
            identifiers.clear();
        } else {
            if (identifiers.isEmpty()) {
                identifiers.add(identifier);
            } else {
                identifiers.add(0, identifier);
            }
        }
    }

    public void addIdentifier(String identifier) {
        identifiers.add(identifier);
    }

    public List<String> getJournalIdentifiers() {
        return journalIdentifiers;
    }

    public void setJournalIdentifiers(List<String> journalIdentifiers) {
        this.journalIdentifiers = journalIdentifiers;
    }

    public void addJournalIdentifier(String journalIdentifier) {
        if (!this.journalIdentifiers.contains(journalIdentifier)) {
            this.journalIdentifiers.add(journalIdentifier);
        }
    }

    public List<String> getJournalIssns() {
        return journalIssns;
    }

    public void setJournalIssns(List<String> journalIssns) {
        this.journalIssns = journalIssns;
    }

    public void addJournalIssn(String journalIssn) {
        if (!this.journalIssns.contains(journalIssn)) {
            this.journalIssns.add(journalIssn);
        }
    }

    /**
     * Gets the url where the PDF was downloaded.
     *
     * May return null
     *
     * @return the url as a string or null
     */
    public String getPdfUrl() {
        if (this.pdfDownloadedFrom != null) {
            return pdfDownloadedFrom;
        }
        if (pdfUrls.isEmpty()) {
            return null;
        } else {
            // in the list may exist both core download links
            // and external dl links (in the case that the article is
            // from a citation crawling repository)
            // preferably return the external one
            for (String p : pdfUrls.keySet()) {
                if (p != null && !p.contains("core.kmi.open.ac.uk/download/pdf") && !p.contains("creativecommons.org")) {
                    return p;
                }
            }

            return (String) pdfUrls.keySet().toArray()[0];
        }
    }

    public Map<String, PDFUrlSource> getPdfUrls() {
        return pdfUrls;
    }

    public void setPdfUrls(List<String> urlList) {
        urlList.forEach(url -> this.pdfUrls.put(url, PDFUrlSource.OAIPMH));
    }

    @Deprecated
    public void setPdfUrl(String pdfUrl) {
        if (pdfUrl == null) {
            pdfUrls.clear();
        } else {
            pdfUrls.put(pdfUrl, PDFUrlSource.OAIPMH);
        }
    }

    public void addPdfUrl(String pdfUrl) {
        if (pdfUrl != null) {
            this.addPdfUrl(pdfUrl, PDFUrlSource.OAIPMH);
        } else {
//            Debugger.debug("Null PDF: " + this.getId(), this.getClass());
        }
    }

    public void addPdfUrl(String pdfUrl, PDFUrlSource source) {
        if (pdfUrl != null) {
            pdfUrls.put(pdfUrl, source);
        } else {
//            Debugger.debug("Null PDF: " + this.getId(), this.getClass());
        }
    }

    /*
     * TYPES
     * *************************************************************************************
     */
    public List<String> getTypes() {
        return this.types;
    }

    public void addType(String type) {
        this.types.add(type);
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    /*
     * FULL TEXT
     * *********************************************************************************
     */
    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    /*
     * RELATION
     * **********************************************************************************
     */
    public List<String> getRelations() {
        return this.relations;
    }

    public void addRelation(String relation) {
        this.relations.add(relation);
    }

    public void setRelations(List<String> relations) {
        this.relations = relations;
    }

    public Boolean isDoiFromCrossRef() {
        return doiFromCrossRef;
    }

    public void setDoiFromCrossRef(Boolean doiFromCrossRef) {
        this.doiFromCrossRef = doiFromCrossRef;
    }

    public String getDoiMetadataTag() {
        return doiMetadataTag;
    }

    public void setDoiMetadataTag(String doiMetadataTag) {
        this.doiMetadataTag = doiMetadataTag;
    }

    public Date getDoiDatetimeResolved() {
        return doiDatetimeResolved;
    }

    public void setDoiDatetimeResolved(Date doiDatetimeResolved) {
        this.doiDatetimeResolved = doiDatetimeResolved;
    }

    /*
     * SCORE
     * *************************************************************************************
     */
 /*
     * public void setScore(float score) { this.score = score; }
     *
     * public Float getScore() { return this.score; }
     */

 /*
     * SIMILARITY
     * ********************************************************************************
     */
    public Float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Float similarity) {
        this.similarity = similarity;
    }

    /*
     * SNIPPET
     * ***********************************************************************************
     */
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    /*
     * DELETED
     * ***********************************************************************************
     */
    public void setDeleted(DeletedStatus deleted) {
        this.deleted = deleted;
    }

    public DeletedStatus getDeleted() {
        return this.deleted;
    }

    /*
     * EXTRACTED
     * *********************************************************************************
     */
    public boolean isTextExtracted() {
        return this.textExtracted;
    }

    public void setTextExtracted(boolean extracted) {
        this.textExtracted = extracted;
    }

    /*
     * CITATIONS
     * *********************************************************************************
     */
    public void setCitations(List<Citation> citations) {
        this.citations = citations;
    }

    public void addCitation(Citation citation) {
        this.citations.add(citation);
    }

    public List<Citation> getCitations() {
        return this.citations;
    }

    public void setCitationsWithDocIds(Map<String, Integer> citations) {
        this.citationsWithDocIds = citations;
    }

    public Map<String, Integer> getCitationsWithDocIds() {
        return this.citationsWithDocIds;
    }

    public Map<String, String> getCitationsDoiMap() {
        return citationsDoiMap;
    }

    public void setCitationsDoiMap(Map<String, String> citationsDoiMap) {
        this.citationsDoiMap = citationsDoiMap;
    }

    /*
     * LANGUAGE
     * **********************************************************************************
     */
    public void setLanguage(Language lang) {
        this.language = lang;
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

    public Language getLanguage() {
        return this.language;
    }

    /*
     * DOC CLASS
     * **************************************************************************************
     */
    public void setDocClass(String docClass) {
        this.docClass = docClass;
    }

    public String getDocClass() {
        return this.docClass;
    }

    /*
     * SUBJECTS
     * **********************************************************************************
     */
    public void addSubject(String subject) {
        this.subjects.add(subject);
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<String> getSubjects() {
        return this.subjects;
    }

    public List<Integer> getSubIds() {
        return subIds;
    }

    public void setSubIds(List<Integer> subIds) {
        this.subIds = subIds;
    }

    public void addSubId(Integer subId) {
        subIds.add(subId);
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<SimilarDocument> getSimilarities() {
        return similarities;
    }

    public void setSimilarities(List<SimilarDocument> similarities) {
        this.similarities = similarities;
    }

    public RepositoryDocument getRepositoryDocument() {
        return repositoryDocument;
    }

    public String getLicense() { return license; }

    public void setLicense(String license) { this.license = license; }

    /**
     * get the unparsed language from the metadata
     * @return
     */
    public String getRawLanguage() {
        return rawLanguage;
    }

    /**
     * set the unparsed language from the metadata
     * @param rawLanguage
     */
    public void setRawLanguage(String rawLanguage) {
        this.rawLanguage = rawLanguage;
    }

    public void setRepositoryDocument(RepositoryDocument repositoryDocument) {
        this.repositoryDocument = repositoryDocument;
    }

    /**
     * Builds text for calculating similarities from metadata.
     *
     * @return Text containing title and description
     */
    private String getMetadataText() {

        StringBuilder sb = new StringBuilder();
        if (getTitle() != null && !getTitle().isEmpty()) {
            sb.append(getTitle());
        }
        sb.append("\n");
        if (getDescription() != null && !getDescription().isEmpty()) {
            sb.append(getDescription());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ArticleMetadata_" + this.getTitle();
    }

    public String toFullString() {
        Gson gson;
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .setExclusionStrategies(new Citation.CitationExclStrat(), new RawMetadataExclStrat())
                .create();
        String json = gson.toJson(this, ArticleMetadata.class);
        return json;
    }

    /**
     * Parse document IDs from citation strings.
     *
     * @param citations
     * @return
     */
    private void parseCitations(List<String> citations) {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]$");
        Pattern doiPattern = Pattern.compile("\\##doi:(.*?)\\##");
        for (String citation : citations) {
            Matcher matcher = pattern.matcher(citation);
            Matcher doiMatcher = doiPattern.matcher(citation);

            //if both doi and docId exist
            if (matcher.find() && doiMatcher.find()) {
                try {
                    Integer docId = Integer.parseInt(matcher.group(1));
                    String localDoi = doiMatcher.group(1);
                    this.citationsWithDocIds.put(citation.substring(0, citation.indexOf("##doi:")), docId);
                    this.citationsDoiMap.put(citation.substring(0, citation.indexOf("##doi:")), localDoi);
                } catch (NumberFormatException ex) {
//                    Debugger.error("Parse citations error: ", ex, this.getClass());
                    citationsWithDocIds.put(citation, null);
                    citationsDoiMap.put(citation, null);
                }
            }
            matcher.reset();
            doiMatcher.reset();
            //only docId
            if (matcher.find() && !doiMatcher.find()) {
                try {
                    Integer docId = Integer.parseInt(matcher.group(1));
                    this.citationsWithDocIds.put(citation.substring(0, citation.indexOf("[" + docId + "]")), docId);
                    this.citationsDoiMap.put(citation.substring(0, citation.indexOf("[" + docId + "]")), null);
                } catch (NumberFormatException ex) {
//                    Debugger.error("Parse citations error: ", ex, this.getClass());
                    this.citationsWithDocIds.put(citation, null);
                }
            }

            matcher.reset();
            doiMatcher.reset();
            //only doi
            if (doiMatcher.find() && !matcher.find()) {
                String localDoi = doiMatcher.group(1);
                this.citationsWithDocIds.put(citation.substring(0, citation.indexOf("##doi:")), null);
                this.citationsDoiMap.put(citation.substring(0, citation.indexOf("##doi:")), localDoi);
            }

            matcher.reset();
            doiMatcher.reset();
            //nothing
            if (!doiMatcher.find() && !matcher.find()) {
                citationsWithDocIds.put(citation, null);
                citationsDoiMap.put(citation, null);
            }
        }
//        return citationsWithDocIds;
    }

    public final void addDuplicateMetadata(ArticleMetadata amDuplicate) {
        assert (amDuplicate.getId() != null);

        // all
        this.addSubId(amDuplicate.getId());
        this.addRepositoryId(amDuplicate.getRepositoryId());
        this.addRepository(amDuplicate.getRepository());
        this.addIdentifier(amDuplicate.getOAIIdentifier());
        this.addPdfUrl(amDuplicate.getPdfUrl());

        // one of
        if (this.getTitle() == null) {
            this.setTitle(amDuplicate.getTitle());
        }
        if (this.getDescription() == null) {
            this.setDescription(amDuplicate.getDescription());
        }

        if (this.getFullText() == null) {
            this.setFullText(amDuplicate.getFullText());
        }

        if (!this.isTextExtracted() && amDuplicate.isTextExtracted()) {
            this.setTextExtracted(true);
        }
        if (this.getAuthors().isEmpty()) {
            this.setAuthors(amDuplicate.getAuthors());
        }

        if (this.getTopics().isEmpty()) {
            this.setTopics(amDuplicate.getTopics());
        }

        if (this.getTypes().isEmpty()) {
            this.setTypes(amDuplicate.getTypes());
        }
        if (this.getPublisher() == null) {
            this.setPublisher(amDuplicate.getPublisher());
        }

        // scp334: This line causes the whole index task to fail so catch the error.
        // Not sure exactly why this goes wrong
        try {
            if (this.getYear() == null) {
                this.setYear(amDuplicate.getYear());
            }
        } catch (Exception ex) {
//            Debugger.warn("Something wrong with year: " + ex.getMessage(), ex, this.getClass());
        }

        if (this.getSize() == null) {
            if (amDuplicate.getSize() != null) {
                this.setSize(amDuplicate.getSize());
            }
            if (amDuplicate.getFormattedSize() != null && !amDuplicate.getFormattedSize().isEmpty()) {
                this.setFormattedSize(amDuplicate.getFormattedSize());
            }
        }

        // TODO
        // date
        // size x
        // relations
        // doi
        // deleted?
        // citations
        // citationswithdocids
        // docclass
        // subjects
        // language
    }

    public Set<String> getSetNames() {
        if(this.setNames == null) {
            return new HashSet<>();
        } else {
            return setNames;
        }
    }

    public void setSetNames(Set<String> setNames) {
        this.setNames = setNames;
    }

    public void addSetName(String setName) {
        if(this.setNames == null) {
            this.setNames = new HashSet<>();
        }
        this.setNames.add(setName);
    }


    /**
     * Exclude rawRecordXml for gson representation. RawMetadata should not be
     * included in the serialized form of ArticleMetadata that is stored in the
     * elasticsearch index
     */
    public static class RawMetadataExclStrat implements ExclusionStrategy {

        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes fa) {
            return (fa.getName().equals("rawRecordXml"));
        }
    }

    public String getPdfDownloadedFrom() {
        return pdfDownloadedFrom;
    }

    public void setPdfDownloadedFrom(String pdfDownloadedFrom) {
        this.pdfDownloadedFrom = pdfDownloadedFrom;
    }

}
