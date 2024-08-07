
package uk.ac.core.oadiscover.services.epmc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("pmid")
    @Expose
    private String pmid;
    @SerializedName("pmcid")
    @Expose
    private String pmcid;
    @SerializedName("doi")
    @Expose
    private String doi;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("authorString")
    @Expose
    private String authorString;
    @SerializedName("authorList")
    @Expose
    private AuthorList authorList;
    @SerializedName("journalInfo")
    @Expose
    private JournalInfo journalInfo;
    @SerializedName("pubYear")
    @Expose
    private String pubYear;
    @SerializedName("pageInfo")
    @Expose
    private String pageInfo;
    @SerializedName("abstractText")
    @Expose
    private String abstractText;
    @SerializedName("affiliation")
    @Expose
    private String affiliation;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("pubModel")
    @Expose
    private String pubModel;
    @SerializedName("pubTypeList")
    @Expose
    private PubTypeList pubTypeList;
    @SerializedName("keywordList")
    @Expose
    private KeywordList keywordList;
    @SerializedName("fullTextUrlList")
    @Expose
    private FullTextUrlList fullTextUrlList;
    @SerializedName("isOpenAccess")
    @Expose
    private String isOpenAccess;
    @SerializedName("inEPMC")
    @Expose
    private String inEPMC;
    @SerializedName("inPMC")
    @Expose
    private String inPMC;
    @SerializedName("hasPDF")
    @Expose
    private String hasPDF;
    @SerializedName("hasBook")
    @Expose
    private String hasBook;
    @SerializedName("hasSuppl")
    @Expose
    private String hasSuppl;
    @SerializedName("citedByCount")
    @Expose
    private Long citedByCount;
    @SerializedName("hasReferences")
    @Expose
    private String hasReferences;
    @SerializedName("hasTextMinedTerms")
    @Expose
    private String hasTextMinedTerms;
    @SerializedName("hasDbCrossReferences")
    @Expose
    private String hasDbCrossReferences;
    @SerializedName("hasLabsLinks")
    @Expose
    private String hasLabsLinks;
    @SerializedName("license")
    @Expose
    private String license;
    @SerializedName("authMan")
    @Expose
    private String authMan;
    @SerializedName("epmcAuthMan")
    @Expose
    private String epmcAuthMan;
    @SerializedName("nihAuthMan")
    @Expose
    private String nihAuthMan;
    @SerializedName("hasTMAccessionNumbers")
    @Expose
    private String hasTMAccessionNumbers;
    @SerializedName("dateOfCreation")
    @Expose
    private String dateOfCreation;
    @SerializedName("dateOfRevision")
    @Expose
    private String dateOfRevision;
    @SerializedName("electronicPublicationDate")
    @Expose
    private String electronicPublicationDate;
    @SerializedName("firstPublicationDate")
    @Expose
    private String firstPublicationDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getPmcid() {
        return pmcid;
    }

    public void setPmcid(String pmcid) {
        this.pmcid = pmcid;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorString() {
        return authorString;
    }

    public void setAuthorString(String authorString) {
        this.authorString = authorString;
    }

    public AuthorList getAuthorList() {
        return authorList;
    }

    public void setAuthorList(AuthorList authorList) {
        this.authorList = authorList;
    }

    public JournalInfo getJournalInfo() {
        return journalInfo;
    }

    public void setJournalInfo(JournalInfo journalInfo) {
        this.journalInfo = journalInfo;
    }

    public String getPubYear() {
        return pubYear;
    }

    public void setPubYear(String pubYear) {
        this.pubYear = pubYear;
    }

    public String getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(String pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPubModel() {
        return pubModel;
    }

    public void setPubModel(String pubModel) {
        this.pubModel = pubModel;
    }

    public PubTypeList getPubTypeList() {
        return pubTypeList;
    }

    public void setPubTypeList(PubTypeList pubTypeList) {
        this.pubTypeList = pubTypeList;
    }

    public KeywordList getKeywordList() {
        return keywordList;
    }

    public void setKeywordList(KeywordList keywordList) {
        this.keywordList = keywordList;
    }

    public FullTextUrlList getFullTextUrlList() {
        return fullTextUrlList;
    }

    public void setFullTextUrlList(FullTextUrlList fullTextUrlList) {
        this.fullTextUrlList = fullTextUrlList;
    }

    public String getIsOpenAccess() {
        return isOpenAccess;
    }

    public void setIsOpenAccess(String isOpenAccess) {
        this.isOpenAccess = isOpenAccess;
    }

    public String getInEPMC() {
        return inEPMC;
    }

    public void setInEPMC(String inEPMC) {
        this.inEPMC = inEPMC;
    }

    public String getInPMC() {
        return inPMC;
    }

    public void setInPMC(String inPMC) {
        this.inPMC = inPMC;
    }

    public String getHasPDF() {
        return hasPDF;
    }

    public void setHasPDF(String hasPDF) {
        this.hasPDF = hasPDF;
    }

    public String getHasBook() {
        return hasBook;
    }

    public void setHasBook(String hasBook) {
        this.hasBook = hasBook;
    }

    public String getHasSuppl() {
        return hasSuppl;
    }

    public void setHasSuppl(String hasSuppl) {
        this.hasSuppl = hasSuppl;
    }

    public Long getCitedByCount() {
        return citedByCount;
    }

    public void setCitedByCount(Long citedByCount) {
        this.citedByCount = citedByCount;
    }

    public String getHasReferences() {
        return hasReferences;
    }

    public void setHasReferences(String hasReferences) {
        this.hasReferences = hasReferences;
    }

    public String getHasTextMinedTerms() {
        return hasTextMinedTerms;
    }

    public void setHasTextMinedTerms(String hasTextMinedTerms) {
        this.hasTextMinedTerms = hasTextMinedTerms;
    }

    public String getHasDbCrossReferences() {
        return hasDbCrossReferences;
    }

    public void setHasDbCrossReferences(String hasDbCrossReferences) {
        this.hasDbCrossReferences = hasDbCrossReferences;
    }

    public String getHasLabsLinks() {
        return hasLabsLinks;
    }

    public void setHasLabsLinks(String hasLabsLinks) {
        this.hasLabsLinks = hasLabsLinks;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getAuthMan() {
        return authMan;
    }

    public void setAuthMan(String authMan) {
        this.authMan = authMan;
    }

    public String getEpmcAuthMan() {
        return epmcAuthMan;
    }

    public void setEpmcAuthMan(String epmcAuthMan) {
        this.epmcAuthMan = epmcAuthMan;
    }

    public String getNihAuthMan() {
        return nihAuthMan;
    }

    public void setNihAuthMan(String nihAuthMan) {
        this.nihAuthMan = nihAuthMan;
    }

    public String getHasTMAccessionNumbers() {
        return hasTMAccessionNumbers;
    }

    public void setHasTMAccessionNumbers(String hasTMAccessionNumbers) {
        this.hasTMAccessionNumbers = hasTMAccessionNumbers;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getDateOfRevision() {
        return dateOfRevision;
    }

    public void setDateOfRevision(String dateOfRevision) {
        this.dateOfRevision = dateOfRevision;
    }

    public String getElectronicPublicationDate() {
        return electronicPublicationDate;
    }

    public void setElectronicPublicationDate(String electronicPublicationDate) {
        this.electronicPublicationDate = electronicPublicationDate;
    }

    public String getFirstPublicationDate() {
        return firstPublicationDate;
    }

    public void setFirstPublicationDate(String firstPublicationDate) {
        this.firstPublicationDate = firstPublicationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (id != null ? !id.equals(result.id) : result.id != null) return false;
        if (source != null ? !source.equals(result.source) : result.source != null) return false;
        if (pmid != null ? !pmid.equals(result.pmid) : result.pmid != null) return false;
        if (pmcid != null ? !pmcid.equals(result.pmcid) : result.pmcid != null) return false;
        if (doi != null ? !doi.equals(result.doi) : result.doi != null) return false;
        if (title != null ? !title.equals(result.title) : result.title != null) return false;
        if (authorString != null ? !authorString.equals(result.authorString) : result.authorString != null)
            return false;
        if (authorList != null ? !authorList.equals(result.authorList) : result.authorList != null) return false;
        if (journalInfo != null ? !journalInfo.equals(result.journalInfo) : result.journalInfo != null) return false;
        if (pubYear != null ? !pubYear.equals(result.pubYear) : result.pubYear != null) return false;
        if (pageInfo != null ? !pageInfo.equals(result.pageInfo) : result.pageInfo != null) return false;
        if (abstractText != null ? !abstractText.equals(result.abstractText) : result.abstractText != null)
            return false;
        if (affiliation != null ? !affiliation.equals(result.affiliation) : result.affiliation != null) return false;
        if (language != null ? !language.equals(result.language) : result.language != null) return false;
        if (pubModel != null ? !pubModel.equals(result.pubModel) : result.pubModel != null) return false;
        if (pubTypeList != null ? !pubTypeList.equals(result.pubTypeList) : result.pubTypeList != null) return false;
        if (keywordList != null ? !keywordList.equals(result.keywordList) : result.keywordList != null) return false;
        if (fullTextUrlList != null ? !fullTextUrlList.equals(result.fullTextUrlList) : result.fullTextUrlList != null)
            return false;
        if (isOpenAccess != null ? !isOpenAccess.equals(result.isOpenAccess) : result.isOpenAccess != null)
            return false;
        if (inEPMC != null ? !inEPMC.equals(result.inEPMC) : result.inEPMC != null) return false;
        if (inPMC != null ? !inPMC.equals(result.inPMC) : result.inPMC != null) return false;
        if (hasPDF != null ? !hasPDF.equals(result.hasPDF) : result.hasPDF != null) return false;
        if (hasBook != null ? !hasBook.equals(result.hasBook) : result.hasBook != null) return false;
        if (hasSuppl != null ? !hasSuppl.equals(result.hasSuppl) : result.hasSuppl != null) return false;
        if (citedByCount != null ? !citedByCount.equals(result.citedByCount) : result.citedByCount != null)
            return false;
        if (hasReferences != null ? !hasReferences.equals(result.hasReferences) : result.hasReferences != null)
            return false;
        if (hasTextMinedTerms != null ? !hasTextMinedTerms.equals(result.hasTextMinedTerms) : result.hasTextMinedTerms != null)
            return false;
        if (hasDbCrossReferences != null ? !hasDbCrossReferences.equals(result.hasDbCrossReferences) : result.hasDbCrossReferences != null)
            return false;
        if (hasLabsLinks != null ? !hasLabsLinks.equals(result.hasLabsLinks) : result.hasLabsLinks != null)
            return false;
        if (license != null ? !license.equals(result.license) : result.license != null) return false;
        if (authMan != null ? !authMan.equals(result.authMan) : result.authMan != null) return false;
        if (epmcAuthMan != null ? !epmcAuthMan.equals(result.epmcAuthMan) : result.epmcAuthMan != null) return false;
        if (nihAuthMan != null ? !nihAuthMan.equals(result.nihAuthMan) : result.nihAuthMan != null) return false;
        if (hasTMAccessionNumbers != null ? !hasTMAccessionNumbers.equals(result.hasTMAccessionNumbers) : result.hasTMAccessionNumbers != null)
            return false;
        if (dateOfCreation != null ? !dateOfCreation.equals(result.dateOfCreation) : result.dateOfCreation != null)
            return false;
        if (dateOfRevision != null ? !dateOfRevision.equals(result.dateOfRevision) : result.dateOfRevision != null)
            return false;
        if (electronicPublicationDate != null ? !electronicPublicationDate.equals(result.electronicPublicationDate) : result.electronicPublicationDate != null)
            return false;
        return firstPublicationDate != null ? firstPublicationDate.equals(result.firstPublicationDate) : result.firstPublicationDate == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (pmid != null ? pmid.hashCode() : 0);
        result = 31 * result + (pmcid != null ? pmcid.hashCode() : 0);
        result = 31 * result + (doi != null ? doi.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (authorString != null ? authorString.hashCode() : 0);
        result = 31 * result + (authorList != null ? authorList.hashCode() : 0);
        result = 31 * result + (journalInfo != null ? journalInfo.hashCode() : 0);
        result = 31 * result + (pubYear != null ? pubYear.hashCode() : 0);
        result = 31 * result + (pageInfo != null ? pageInfo.hashCode() : 0);
        result = 31 * result + (abstractText != null ? abstractText.hashCode() : 0);
        result = 31 * result + (affiliation != null ? affiliation.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (pubModel != null ? pubModel.hashCode() : 0);
        result = 31 * result + (pubTypeList != null ? pubTypeList.hashCode() : 0);
        result = 31 * result + (keywordList != null ? keywordList.hashCode() : 0);
        result = 31 * result + (fullTextUrlList != null ? fullTextUrlList.hashCode() : 0);
        result = 31 * result + (isOpenAccess != null ? isOpenAccess.hashCode() : 0);
        result = 31 * result + (inEPMC != null ? inEPMC.hashCode() : 0);
        result = 31 * result + (inPMC != null ? inPMC.hashCode() : 0);
        result = 31 * result + (hasPDF != null ? hasPDF.hashCode() : 0);
        result = 31 * result + (hasBook != null ? hasBook.hashCode() : 0);
        result = 31 * result + (hasSuppl != null ? hasSuppl.hashCode() : 0);
        result = 31 * result + (citedByCount != null ? citedByCount.hashCode() : 0);
        result = 31 * result + (hasReferences != null ? hasReferences.hashCode() : 0);
        result = 31 * result + (hasTextMinedTerms != null ? hasTextMinedTerms.hashCode() : 0);
        result = 31 * result + (hasDbCrossReferences != null ? hasDbCrossReferences.hashCode() : 0);
        result = 31 * result + (hasLabsLinks != null ? hasLabsLinks.hashCode() : 0);
        result = 31 * result + (license != null ? license.hashCode() : 0);
        result = 31 * result + (authMan != null ? authMan.hashCode() : 0);
        result = 31 * result + (epmcAuthMan != null ? epmcAuthMan.hashCode() : 0);
        result = 31 * result + (nihAuthMan != null ? nihAuthMan.hashCode() : 0);
        result = 31 * result + (hasTMAccessionNumbers != null ? hasTMAccessionNumbers.hashCode() : 0);
        result = 31 * result + (dateOfCreation != null ? dateOfCreation.hashCode() : 0);
        result = 31 * result + (dateOfRevision != null ? dateOfRevision.hashCode() : 0);
        result = 31 * result + (electronicPublicationDate != null ? electronicPublicationDate.hashCode() : 0);
        result = 31 * result + (firstPublicationDate != null ? firstPublicationDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", pmid='" + pmid + '\'' +
                ", pmcid='" + pmcid + '\'' +
                ", doi='" + doi + '\'' +
                ", title='" + title + '\'' +
                ", authorString='" + authorString + '\'' +
                ", authorList=" + authorList +
                ", journalInfo=" + journalInfo +
                ", pubYear='" + pubYear + '\'' +
                ", pageInfo='" + pageInfo + '\'' +
                ", abstractText='" + abstractText + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", language='" + language + '\'' +
                ", pubModel='" + pubModel + '\'' +
                ", pubTypeList=" + pubTypeList +
                ", keywordList=" + keywordList +
                ", fullTextUrlList=" + fullTextUrlList +
                ", isOpenAccess='" + isOpenAccess + '\'' +
                ", inEPMC='" + inEPMC + '\'' +
                ", inPMC='" + inPMC + '\'' +
                ", hasPDF='" + hasPDF + '\'' +
                ", hasBook='" + hasBook + '\'' +
                ", hasSuppl='" + hasSuppl + '\'' +
                ", citedByCount=" + citedByCount +
                ", hasReferences='" + hasReferences + '\'' +
                ", hasTextMinedTerms='" + hasTextMinedTerms + '\'' +
                ", hasDbCrossReferences='" + hasDbCrossReferences + '\'' +
                ", hasLabsLinks='" + hasLabsLinks + '\'' +
                ", license='" + license + '\'' +
                ", authMan='" + authMan + '\'' +
                ", epmcAuthMan='" + epmcAuthMan + '\'' +
                ", nihAuthMan='" + nihAuthMan + '\'' +
                ", hasTMAccessionNumbers='" + hasTMAccessionNumbers + '\'' +
                ", dateOfCreation='" + dateOfCreation + '\'' +
                ", dateOfRevision='" + dateOfRevision + '\'' +
                ", electronicPublicationDate='" + electronicPublicationDate + '\'' +
                ", firstPublicationDate='" + firstPublicationDate + '\'' +
                '}';
    }
}
