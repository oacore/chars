
package uk.ac.core.common.model.legacy;

import java.util.LinkedList;
import java.util.List;

/**
 * Class holding information about repository metadata (such as number of PDF links
 * in the metadata etc.).
 *
 * @author pk3295, dh8835
 */
public class RepositoryMetadata {

    /** String name of the repository */
    private String repositoryName;
    /** URL for sending requests */
    private String baseURL;
    /** List of articles acquired from the repository */
    private List<ArticleMetadata> articleList;
    /** List of journals */
    private List<Journal> journalList;
    /** Number of valid PDF links found in the repository (valid link -- starts with "http://" and
     * ends with ".pdf", the actual parsing and checking of the links is done during PDF download)*/
    private int countPdfs;
    /** Number of deleted records */
    private int countDeletedRecords;
    /** Number of total metadata records */
    private int countMetadataRecords;
    /** Number of records without PDF URL */
    private int countNoUrlRecords;
    /** Number of journals */
    private int countJournals;
    /** ID of the repository (in CORE database) */
    private Integer repositoryId;

    /**
     * Set the variables.
     */
    public RepositoryMetadata() {
        this.countPdfs = 0;
        this.countMetadataRecords = 0;
        this.countDeletedRecords = 0;
        this.countNoUrlRecords = 0;
        this.countJournals = 0;
        this.articleList = new LinkedList<ArticleMetadata>();
        this.journalList = new LinkedList<>();
    }

    /**
     * Add metadata of one article.
     * 
     * @param am
     */
    public void addArticleMetadata(ArticleMetadata am) {
        articleList.add(am);
    }
    
    public void addJournal(Journal j) {
        this.journalList.add(j);
    }
    
    public List<Journal> getJournals() {
        return this.journalList;
    }

    /**
     * Get base URL for making request.
     * 
     * @return
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Set the repository URL.
     * 
     * @param baseURL
     */
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Get name of the repository.
     * 
     * @return
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    /**
     * Set the name of the repository.
     * 
     * @param repositoryName
     */
    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    /**
     * Return metadata of all repository articles as collection corpus.
     * 
     * @return
     */
    public Corpus<ArticleMetadata> getArticleCorpus() {
        CollectionCorpus<ArticleMetadata> c = new CollectionCorpus<ArticleMetadata>();
        c.setCollection(articleList);
        return c;
    }

    /**
     * Get number of the article metadata in the metadata list.
     * 
     * @return
     */
    public Integer getSize() {
        return articleList.size();
    }

    /**
     * Set number of metatada records which contain PDF link.
     * 
     * @param countPdfs
     */
    public void setPdfsCount(Integer countPdfs) {
        this.countPdfs = countPdfs;
    }

    /**
     * Get number of metatada records which contain PDF link.
     * 
     * @return
     */
    public Integer getPdfsCount() {
        return this.countPdfs;
    }

    /**
     * Increase number of metatada records which contain PDF link by one.
     */
    public void incPdfsCount() {
        this.countPdfs++;
    }

    /**
     * Set number of total records in the metadata file.
     * 
     * @param countMetadataRecords
     */
    public void setRecordsCount(Integer countMetadataRecords) {
        this.countMetadataRecords = countMetadataRecords;
    }

    /**
     * Get number of total records in the metadata file.
     * 
     * @return
     */
    public Integer getRecordsCount() {
        return this.countMetadataRecords;
    }

    /**
     * Increase total records count by one.
     */
    public void incRecordsCount() {
        this.countMetadataRecords++;
    }

    /**
     * Decrease total records count by one (useful in case of records marked as deleted).
     */
    public void decRecordsCount() {
        this.countMetadataRecords--;
    }
    
    /**
     * Increase total journals count by one.
     */
    public void incJournalsCount() {
        this.countJournals++;
    }

    /**
     * Set number of deleted records.
     *
     * (Records that were marked as deleted in the metadata file.)
     * 
     * @param deletedRecordsCount
     */
    public void setDeletedRecordsCount(Integer deletedRecordsCount) {
        this.countDeletedRecords = deletedRecordsCount;
    }

    /**
     * Get number of deleted records.
     *
     * (Records that were marked as deleted in the metadata file.)
     * 
     * @return
     */
    public Integer getDeletedRecordsCount() {
        return this.countDeletedRecords;
    }

    /**
     * Increase the number of deleted records by one.
     */
    public void incDeletedRecordsCount() {
        this.countDeletedRecords++;
    }

    /**
     * Set number of records without PDF URL.
     * 
     * @param noUrlRecordsCount
     */
    public void setNoUrlRecordsCount(Integer noUrlRecordsCount) {
        this.countNoUrlRecords = noUrlRecordsCount;
    }

    /**
     * Get number of records without PDF URL.
     * 
     * @return
     */
    public Integer getNoUrlRecordsCount() {
       return this.countNoUrlRecords;
    }

    /**
     * Increase the number of records without PDF URL by one.
     */
    public void incNoUrlRecordsCount() {
        this.countNoUrlRecords++;
    }

    /**
     * Set ID of the repository.
     *
     * (ID in the DORE database.)
     * 
     * @param repositoryId
     */
    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    /**
     * Get ID of the repository.
     *
     * (ID in the DORE database.)
     * 
     * @return
     */
    public Integer getRepositoryId() {
        return this.repositoryId;
    }

}
