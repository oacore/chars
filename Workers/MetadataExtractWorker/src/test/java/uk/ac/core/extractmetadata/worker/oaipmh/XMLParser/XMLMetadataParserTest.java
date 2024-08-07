package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.ac.core.common.model.article.License;
import uk.ac.core.common.model.legacy.DocumentTdmStatus;
import uk.ac.core.database.service.document.DocumentTdmStatusDAO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class XMLMetadataParserTest {

    private static boolean FIXED = true, IS_TDM_ONLY = true, NOT_FIXED, NOT_TDM_ONLY = false;

    /**
     * The documentTDM status is fixed so no action should be taken
     */
    @Test
    public void testProcessTdmOnlyValuesFixedTDMStatus() {
        XMLMetadataParser parser = new XMLMetadataParser();
        DocumentTdmStatusDAO dao = Mockito.mock(DocumentTdmStatusDAO.class);

        DocumentTdmStatus documentTdmStatus = new DocumentTdmStatus(1, IS_TDM_ONLY, FIXED);

        parser.setDocumentTdmStatusDAO(dao);
        parser.processTdmOnlyValues(
                documentTdmStatus,
                new License(""),
                NOT_TDM_ONLY);

        verifyZeroInteractions(dao);
    }

    /**
     * Test when the article, repository and the database is synchronized. No license is specified
     */
    @Test
    public void testProcessTdmOnlyValuesNoChange() {
        XMLMetadataParser parser = new XMLMetadataParser();
        DocumentTdmStatusDAO dao = Mockito.mock(DocumentTdmStatusDAO.class);

        DocumentTdmStatus documentTdmStatus = new DocumentTdmStatus(1, IS_TDM_ONLY, NOT_FIXED);

        parser.setDocumentTdmStatusDAO(dao);
        parser.processTdmOnlyValues(
                documentTdmStatus,
                new License(""),
                IS_TDM_ONLY);
        verifyZeroInteractions(dao);
    }

    /***
     * Test when the article, repository and the database is synchronized. CC-BY license
     */
    @Test
    public void testProcessTdmOnlyValuesCCBY() {
        XMLMetadataParser parser = new XMLMetadataParser();
        DocumentTdmStatusDAO dao = Mockito.mock(DocumentTdmStatusDAO.class);

        DocumentTdmStatus documentTdmStatus = new DocumentTdmStatus(1, IS_TDM_ONLY, NOT_FIXED);

        parser.setDocumentTdmStatusDAO(dao);
        parser.processTdmOnlyValues(
                documentTdmStatus,
                new License("https://creativecommons.org/licenses/by/4.0/"),
                NOT_TDM_ONLY);

        verify(dao, times(1)).insertOrUpdateTdmStatus(documentTdmStatus);
    }


    /***
     * Test when the article, repository and the database is synchronized. CC-BY license
     *
     * This tests the scenario where the
     *  Repository = TDM
     *  Database = TDM
     *  Article = OA = NOT TDM
     *
     * This tests the scenario where the database was TDM but should now be set to non-tdm because of the license
     */
    @Test
    public void testProcessTdmOnlyDocumentIsTDMButLicenseIsOA() {
        XMLMetadataParser parser = new XMLMetadataParser();
        DocumentTdmStatusDAO dao = Mockito.mock(DocumentTdmStatusDAO.class);

        DocumentTdmStatus documentTdmStatus = new DocumentTdmStatus(1, IS_TDM_ONLY, NOT_FIXED);

        parser.setDocumentTdmStatusDAO(dao);
        parser.processTdmOnlyValues(
                documentTdmStatus,
                new License("https://creativecommons.org/licenses/by/4.0/"),
                IS_TDM_ONLY);
        assertEquals(false, documentTdmStatus.getTdmOnly());
        verify(dao, times(1)).insertOrUpdateTdmStatus(documentTdmStatus);
    }

    /***
     * Test when the article, repository and the database is synchronized. CC-BY license
     *
     * This tests the scenario where the
     *  Repository = TDM
     *  Database = TDM
     *  Article = OA = NOT TDM
     *
     * This tests the scenario where the database was TDM but should now be set to non-tdm because of the license
     */
    @Test
    public void testProcessTdmOnly() {
        XMLMetadataParser parser = new XMLMetadataParser();
        DocumentTdmStatusDAO dao = Mockito.mock(DocumentTdmStatusDAO.class);

        DocumentTdmStatus documentTdmStatus = new DocumentTdmStatus(1, NOT_TDM_ONLY, NOT_FIXED);

        parser.setDocumentTdmStatusDAO(dao);
        parser.processTdmOnlyValues(
                documentTdmStatus,
                new License(null),
                IS_TDM_ONLY);
        assertEquals(true, documentTdmStatus.getTdmOnly());
        verify(dao, times(1)).insertOrUpdateTdmStatus(documentTdmStatus);
    }

}