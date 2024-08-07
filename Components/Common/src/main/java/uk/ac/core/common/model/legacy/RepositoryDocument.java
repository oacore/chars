package uk.ac.core.common.model.legacy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author lucasanastasiou
 */
public class RepositoryDocument extends RepositoryDocumentBase {

    /**
     * ID of last update related to this document.
     */
    private Integer idUpdate;
    /**
     * Class of this document (e.g. Chemistry, Health Sciences, etc.).
     */
    private String docClass;
    /**
     * ID of repository document belongs to.
     */
    private Integer idRepository;
    /**
     * Date and time when metadata was added in the database.
     */
    private Date metadataAdded;
    /**
     * Date and time when metadata was updated.
     */
    private Date metadataUpdated;
    /**
     * Status of document PDF -- is set to 1 if PDF was successfully downloaded.
     */
    private Integer pdfStatus;
    /**
     * First attempt to download PDF.
     */
    private Date pdfFirstAttempt;
    /**
     * First successful attempt to download PDF.
     */
    private Date pdfFirstAttemptSuccessful;
    /**
     * Last attempt to download PDF.
     */
    private Date pdfLastAttempt;
    /**
     * Last successful attempt to download PDF.
     */
    private Date pdfLastAttemptSuccessul;
    
    /**
     * Status of whether preview (thumbnail of first page) was succesfully created
     */
    private Integer previewStatus;
            
    /**
     * Status of document text -- is set to 1 if text was successfully extracted
     * from PDF.
     */
    private Integer textStatus;
    /**
     * First attempt to extract text.
     */
    private Date textFirstAttempt;
    /**
     * First successful attempt to extract text.
     */
    private Date textFirstAttemptSuccessful;
    /**
     * Last attempt to extract text.
     */
    private Date textLastAttempt;
    /**
     * Last successful attempt to extract text.
     */
    private Date textLastAttemptSuccessful;
    /**
     * Was document indexed? -- is set to 1 if document was indexed.
     */
    private Integer indexed;
    /**
     * First attempt to index text.
     */
    private Date indexFirstAttempt;
    /**
     * First successful attempt to index text.
     */
    private Date indexFirstAttemptSuccessful;
    /**
     * Last attempt to index text.
     */
    private Date indexLastAttempt;
    /**
     * Last successful attempt to index text.
     */
    private Date indexLastAttemptSuccessful;
    /**
     * Flag marking whether document should be re-indexed (the metadata/full-text changed).
     */
    private Integer reindex;
    /**
     * Last attempt to calculate similarity.
     */
    private Date dateTimeSimilarityCalculated;
    /**
     * Date when the document was added to upstream repository
     */
    private Date dateTimeStamp;
    /**
     * Status of document; if is available or disabled/deleted
     */
    private int deletedStatus;
    /**
     * The URL of the original PDF
     */
    private String PdfUrl;
    /**
     * The source of the full text used, whether coming from OAI-PMH or MUCC or DIT or ...
     */
    private String fullTextSource;
    /* METHODS ************************************************************************************/
    /**
     * Constructor for the class. Sets date format.
     */
    public RepositoryDocument() {
        super();
    }

    /**
     * Parse MySql string date (expected date format is yyyy-MM-dd HH:mm:ss);
     *
     * Returns null if date could not be parsed.
     *
     * @param date
     * @return
     */
    private Date mySqlStringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (date != null && !date.isEmpty()) {
                Date parsedDate = (Date) dateFormat.parse(date);
                return parsedDate;
            } else {
                return null;
            }
        } catch (ParseException ex) {
            return null;
        }
    }

    /**
     * Creates string formatted for storing in MySql database from given Date
     * object.
     *
     * @param date
     * @return
     */
    private String dateToMySqlString(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        } else {
            return null;
        }
    }

    /* ID_UPDATE **********************************************************************************/
    /**
     *
     * @return
     */
    public Integer getIdUpdate() {
        return idUpdate;
    }

    /**
     *
     * @param idUdpate
     */
    public void setIdUpdate(Integer idUpdate) {
        this.idUpdate = idUpdate;
    }

    /* OAI ****************************************************************************************/
    /**
     *
     * @return
     */
    public String getDocClass() {
        return this.docClass;
    }

    /**
     *
     * @param docClass
     */
    public void setDocClass(String docClass) {
        this.docClass = docClass;
    }

    public String getPdfUrl() {
        if (PdfUrl != null && !PdfUrl.startsWith("file://")) {
            return PdfUrl;
        } else {
            return null;
        }
    }

    public void setPdfUrl(String PdfUrl) {
        this.PdfUrl = PdfUrl;
    }
    
    /* ID_REPOSITORY ******************************************************************************/
    /**
     *
     * @return
     */
    public Integer getIdRepository() {
        return this.idRepository;
    }

    /**
     *
     * @param idRepository
     */
    public void setIdRepository(Integer idRepository) {
        this.idRepository = idRepository;
    }

    /* METADATA_ADDED *****************************************************************************/
    /**
     *
     * @return
     */
    public Date getMetadataAdded() {
        return this.metadataAdded;
    }

    /**
     *
     * @return
     */
    public String getMetadataAddedString() {
        return this.dateToMySqlString(this.metadataAdded);
    }

    /**
     *
     * @param date
     */
    public void setMetadataAdded(String date) {
        this.metadataAdded = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setMetadataAdded(Date date) {
        this.metadataAdded = date;
    }

    /* METADATA_UPDATED ***************************************************************************/
    /**
     *
     * @return
     */
    public Date getMetadataUpdated() {
        return this.metadataUpdated;
    }

    /**
     *
     * @return
     */
    public String getMetadataUpdatedString() {
        return this.dateToMySqlString(this.metadataUpdated);
    }

    /**
     *
     * @param date
     */
    public void setMetadataUpdated(String date) {
        this.metadataUpdated = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setMetadataUpdated(Date date) {
        this.metadataUpdated = date;
    }

    /* PDF_STATUS *********************************************************************************/
    /**
     *
     * @return
     */
    public Integer getPdfStatus() {
        return pdfStatus;
    }

    /**
     *
     * @param pdfStatus
     */
    public void setPdfStatus(String pdfStatus) {
        this.pdfStatus = Integer.parseInt(pdfStatus);
    }

    /**
     *
     * @param pdfStatus
     */
    public void setPdfStatus(Integer pdfStatus) {
        this.pdfStatus = pdfStatus;
    }

    /* PDF_FIRST_ATTEMPT **************************************************************************/
    /**
     *
     * @return
     */
    public Date getPdfFirstAttempt() {
        return this.pdfFirstAttempt;
    }

    /**
     *
     * @return
     */
    public String getPdfFirstAttemptString() {
        return this.dateToMySqlString(this.pdfFirstAttempt);
    }

    /**
     *
     * @param date
     */
    public void setPdfFirstAttempt(String date) {
        this.pdfFirstAttempt = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setPdfFirstAttempt(Date date) {
        this.pdfFirstAttempt = date;
    }

    /* PDF_FIRST_ATTEMPT_SUCCESSFUL ***************************************************************/
    /**
     *
     * @return
     */
    public Date getPdfFirstAttemptSuccessful() {
        return this.pdfFirstAttemptSuccessful;
    }

    /**
     *
     * @return
     */
    public String getPdfFirstAttemptSuccessfulString() {
        return this.dateToMySqlString(this.pdfFirstAttemptSuccessful);
    }

    /**
     *
     * @param date
     */
    public void setPdfFirstAttemptSuccessful(String date) {
        this.pdfFirstAttemptSuccessful = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setPdfFirstAttemptSuccessful(Date date) {
        this.pdfFirstAttemptSuccessful = date;
    }

    /* PDF_LAST_ATTEMPT ***************************************************************************/
    /**
     *
     * @return
     */
    public Date getPdfLastAttempt() {
        return this.pdfLastAttempt;
    }

    /**
     *
     * @return
     */
    public String getPdfLastAttemptString() {
        return this.dateToMySqlString(this.pdfLastAttempt);
    }

    /**
     *
     * @param date
     */
    public void setPdfLastAttempt(String date) {
        this.pdfLastAttempt = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setPdfLastAttempt(Date date) {
        this.pdfLastAttempt = date;
    }

    /* PDF_LAST_ATTEMPT_SUCCESSFUL ****************************************************************/
    /**
     *
     * @return
     */
    public Date getPdfLastAttemptSuccessul() {
        return this.pdfLastAttemptSuccessul;
    }

    /**
     *
     * @return
     */
    public String getPdfLastAttemptSuccessfulString() {
        return this.dateToMySqlString(this.pdfLastAttemptSuccessul);
    }

    /**
     *
     * @param date
     */
    public void setPdfLastAttemptSuccessful(String date) {
        this.pdfLastAttemptSuccessul = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setPdfLastAttemptSuccessful(Date date) {
        this.pdfLastAttemptSuccessul = date;
    }

    public Integer getPreviewStatus() {
        return previewStatus;
    }

    public void setPreviewStatus(Integer previewStatus) {
        this.previewStatus = previewStatus;
    }   
    
    
    /* TEXT_STATUS ********************************************************************************/
    /**
     *
     * @return
     */
    public Integer getTextStatus() {
        return textStatus;
    }

    /**
     *
     * @param textStatus
     */
    public void setTextStatus(String textStatus) {
        this.textStatus = Integer.parseInt(textStatus);
    }

    /**
     *
     * @param textStatus
     */
    public void setTextStatus(Integer textStatus) {
        this.textStatus = textStatus;
    }

    /* TEXT_FIRST_ATTEMPT *************************************************************************/
    /**
     *
     * @return
     */
    public Date getTextFirstAttempt() {
        return this.textFirstAttempt;
    }

    /**
     *
     * @return
     */
    public String getTextFirstAttemptString() {
        return this.dateToMySqlString(this.textFirstAttempt);
    }

    /**
     *
     * @param date
     */
    public void setTextFirstAttempt(String date) {
        this.textFirstAttempt = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setTextFirstAttempt(Date date) {
        this.textFirstAttempt = date;
    }

    /* TEXT_FIRST_ATTEMPT_SUCCESSFUL **************************************************************/
    /**
     *
     * @return
     */
    public Date getTextFirstAttemptSuccessful() {
        return this.textFirstAttemptSuccessful;
    }

    /**
     *
     * @return
     */
    public String getTextFirstAttemptSuccessfulString() {
        return this.dateToMySqlString(this.textFirstAttemptSuccessful);
    }

    /**
     *
     * @param date
     */
    public void setTextFirstAttemptSuccessful(String date) {
        this.textFirstAttemptSuccessful = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setTextFirstAttemptSuccessful(Date date) {
        this.textFirstAttemptSuccessful = date;
    }

    /* TEXT_LAST_ATTEMPT **************************************************************************/
    /**
     *
     * @return
     */
    public Date getTextLastAttempt() {
        return this.textLastAttempt;
    }

    /**
     *
     * @return
     */
    public String getTextLastAttemptString() {
        return this.dateToMySqlString(this.textLastAttempt);
    }

    /**
     *
     * @param date
     */
    public void setTextLastAttempt(String date) {
        this.textLastAttempt = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setTextLastAttempt(Date date) {
        this.textLastAttempt = date;
    }

    /* TEXT_LAST_ATTEMPT_SUCCESSFUL ***************************************************************/
    /**
     *
     * @return
     */
    public Date getTextLastAttemptSuccessul() {
        return this.textLastAttemptSuccessful;
    }

    /**
     *
     * @return
     */
    public String getTextLastAttemptSuccessfulString() {
        return this.dateToMySqlString(this.textLastAttemptSuccessful);
    }

    /**
     *
     * @param date
     */
    public void setTextLastAttemptSuccessful(String date) {
        this.textLastAttemptSuccessful = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setTextLastAttemptSuccessful(Date date) {
        this.textLastAttemptSuccessful = date;
    }

    /* INDEXED ************************************************************************************/
    /**
     *
     * @return
     */
    public Integer getIndexed() {
        return this.indexed;
    }

    /**
     *
     * @param indexed
     */
    public void setIndexed(String indexed) {
        this.indexed = Integer.parseInt(indexed);
    }

    /**
     *
     * @param indexed
     */
    public void setIndexed(Integer indexed) {
        this.indexed = indexed;
    }

    /* INDEX_FIRST_ATTEMPT ************************************************************************/
    /**
     *
     * @return
     */
    public Date getIndexFirstAttempt() {
        return this.indexFirstAttempt;
    }

    /**
     *
     * @return
     */
    public String getIndexFirstAttemptString() {
        return this.dateToMySqlString(this.indexFirstAttempt);
    }

    /**
     *
     * @param date
     */
    public void setIndexFirstAttempt(String date) {
        this.indexFirstAttempt = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setIndexFirstAttempt(Date date) {
        this.indexFirstAttempt = date;
    }

    /* INDEX_FIRST_ATTEMPT_SUCCESSFUL *************************************************************/
    /**
     *
     * @return
     */
    public Date getIndexFirstAttemptSuccessful() {
        return this.indexFirstAttemptSuccessful;
    }

    /**
     *
     * @return
     */
    public String getIndexFirstAttemptSuccessfulString() {
        return this.dateToMySqlString(this.indexFirstAttemptSuccessful);
    }

    /**
     *
     * @param date
     */
    public void setIndexFirstAttemptSuccessful(String date) {
        this.indexFirstAttemptSuccessful = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setIndexFirstAttemptSuccessful(Date date) {
        this.indexFirstAttemptSuccessful = date;
    }

    /* INDEX_LAST_ATTEMPT *************************************************************************/
    /**
     *
     * @return
     */
    public Date getIndexLastAttempt() {
        return this.indexLastAttempt;
    }

    /**
     *
     * @return
     */
    public String getIndexLastAttemptString() {
        return this.dateToMySqlString(this.indexLastAttempt);
    }

    /**
     *
     * @param date
     */
    public void setIndexLastAttempt(String date) {
        this.indexLastAttempt = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setIndexLastAttempt(Date date) {
        this.indexLastAttempt = date;
    }

    /* INDEX_LAST_ATTEMPT_SUCCESSFUL **************************************************************/
    /**
     *
     * @return
     */
    public Date getIndexLastAttemptSuccessul() {
        return this.indexLastAttemptSuccessful;
    }

    /**
     *
     * @return
     */
    public String getIndexLastAttemptSuccessfulString() {
        return this.dateToMySqlString(this.indexLastAttemptSuccessful);
    }

    /**
     *
     * @param date
     */
    public void setIndexLastAttemptSuccessful(String date) {
        this.indexLastAttemptSuccessful = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setIndexLastAttemptSuccessful(Date date) {
        this.indexLastAttemptSuccessful = date;
    }
    
    /* REINDEX FLAG ***************************************************************************** */

    public Integer getReindex() {
        return reindex;
    }

    public void setReindex(Integer reindex) {
        this.reindex = reindex;
    }

    /* DATE_TIME_SIMILARITY_CALCULATED ****************************************/
    /**
     *
     * @return
     */
    public Date getDateTimeSimilarityCalculated() {
        return this.dateTimeSimilarityCalculated;
    }

    /**
     *
     * @return
     */
    public String getDateTimeSimilarityCalculatedString() {
        return this.dateToMySqlString(this.dateTimeSimilarityCalculated);
    }

    /**
     *
     * @param date
     */
    public void setDateTimeSimilarityCalculated(String date) {
        this.dateTimeSimilarityCalculated = this.mySqlStringToDate(date);
    }

    /**
     *
     * @param date
     */
    public void setDateTimeSimilarityCalculated(Date date) {
        this.dateTimeSimilarityCalculated = date;
    }

	/**
	 * @return the dateTimeStamp
	 */
	public Date getDateTimeStamp() {
		return dateTimeStamp;
	}

    public int getDeletedStatus() {
        return deletedStatus;
    }

	/**
	 * @param dateTimeStamp the dateTimeStamp to set
	 */
	public void setDateTimeStamp(String dateTimeStamp) {
		this.dateTimeStamp = this.mySqlStringToDate(dateTimeStamp);
	}

    public void setDeletedStatus(int deletedStatus) {
        this.deletedStatus = deletedStatus;
    }

    public String getFullTextSource() {
        return fullTextSource;
    }

    public void setFullTextSource(String fullTextSource) {
        this.fullTextSource = fullTextSource;
    }
    
}

