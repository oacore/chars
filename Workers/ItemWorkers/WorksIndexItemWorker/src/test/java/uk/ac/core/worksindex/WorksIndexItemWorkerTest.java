package uk.ac.core.worksindex;

import org.apache.commons.collections4.CollectionUtils;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.core.common.util.TextToDateTime;
import uk.ac.core.elasticsearch.entities.*;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.elasticsearch.services.util.ElasticSearchWorkMetadataBuilder;
import uk.ac.core.worker.WorksIndexItemWorker;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorksIndexItemWorkerTest {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(WorksIndexItemWorkerTest.class);



    @Test
    private void testDuplication() {
        ElasticSearchWorkMetadataBuilder workMetadataBuilder = mock(ElasticSearchWorkMetadataBuilder.class);
        ArticleMetadataRepository articleMetadataRepository = mock(ArticleMetadataRepository.class);
        WorksIndexItemWorker worksIndexItemWorker = new WorksIndexItemWorker();
        ReflectionTestUtils.setField(worksIndexItemWorker, "elasticSearchWorkMetadataBuilder", workMetadataBuilder);
        ReflectionTestUtils.setField(worksIndexItemWorker, "articleMetadataRepository", articleMetadataRepository);

        ElasticSearchLanguage elasticSearchLanguage1 = new ElasticSearchLanguage();
        ElasticSearchLanguage elasticSearchLanguage2 = new ElasticSearchLanguage();

        ElasticSearchWorkReference elasticSearchCitation1 = new ElasticSearchWorkReference();
        ElasticSearchWorkReference elasticSearchCitation2 = new ElasticSearchWorkReference();


        ElasticSearchWorkMetadata workMetadata1 = getWorkMetadata(1, "Title1", Arrays.asList("Tom", "Sam"),
                null, new HashSet<>(Arrays.asList("1", "3")), new HashSet<>(Arrays.asList(1)),
                "fullText1", new HashSet<>(Arrays.asList("url1", "url2")), elasticSearchLanguage1,
                "downloadUrl1", 1, Arrays.asList(elasticSearchCitation1),
                "documentType1", new HashSet<>(Arrays.asList(new ElasticSearchJournal())),
                new Publisher(),
                "2005-09-01T00:00:00", "2005-09-01T00:00:00", "2005-09-01T00:00:00",
                2005,
                "2005-09-01T00:00:00", "2005-09-01T00:00:00",
                "doi1", "magId1", "arxivId1", null,  new HashSet<>(Arrays.asList("oai1", "oai2")),
                new HashSet<>(), new HashSet<>());

        ElasticSearchWorkMetadata workMetadata2 = getWorkMetadata(1, "Title2", Arrays.asList("Tom", "Den"),
                "description2", new HashSet<>(Arrays.asList("1", "2")), new HashSet<>(Arrays.asList(2, 3)),
                "",
                new HashSet<>(Arrays.asList("url1", "url3")), elasticSearchLanguage2,
                "core.ac.uk downloadUrl2",
                2, Arrays.asList(elasticSearchCitation2),
                "documentType2", new HashSet<>(Arrays.asList(new ElasticSearchJournal())),
                new Publisher(),
                "", null, "2005-09-01T00:00:00",
                2005,
                "2005-09-01T00:00:00", "2005-09-01T00:00:00",
                "doi2", "magId2", "arxivId2", "pubmedId2",  new HashSet<>(Arrays.asList("oai2", "oai3")),
                new HashSet<>(Arrays.asList("coreId1", "coreId2")), new HashSet<>());

        when(workMetadataBuilder.generateElasticSearchWorkMetadata(any(), anyInt()))
                .thenReturn(workMetadata1)
                .thenReturn(workMetadata2);

        when(articleMetadataRepository.findOneById(any())).thenReturn(new ElasticSearchArticleMetadata());


        ElasticSearchWorkMetadata result =
                worksIndexItemWorker.mergeNewDocumentsToExistingWorkMetadata(workMetadata1, new HashSet<>(Collections.singletonList(2)), 1);
        assertEquals(result.getId(), Integer.valueOf(1));
        assertEquals(result.getTitle(), "Title1");
        assertTrue(CollectionUtils.isEqualCollection(result.getAuthors(), Arrays.asList("Tom", "Sam")));
        assertEquals(result.getDescription(), "description2");
        assertTrue(CollectionUtils.isEqualCollection(result.getContributors(), new HashSet<>(Arrays.asList("1", "3"))));
        assertTrue(CollectionUtils.isEqualCollection(result.getDataProviders(), new HashSet<>(Arrays.asList(1, 2, 3))));
        assertEquals(result.getFullText(), "fullText1");
        assertTrue(CollectionUtils.isEqualCollection(result.getSourceFullTextUrls(), new HashSet<>(Arrays.asList("url1", "url2", "url3"))));
        assertEquals(result.getLanguage(), elasticSearchLanguage1);
        assertEquals(result.getDownloadUrl(), "core.ac.uk downloadUrl2");
        assertEquals(result.getCitationCount(), Integer.valueOf(1));
        assertTrue(CollectionUtils.isEqualCollection(result.getReferences(),
                Arrays.asList(elasticSearchCitation1)));
        assertEquals(result.getDocumentType(), "documentType1");
        assertEquals(result.getPublisher(), new Publisher());
        assertEquals(result.getAcceptedDate(), "2005-09-01T00:00:00");
        assertEquals(result.getDepositedDate(), "2005-09-01T00:00:00");
        assertEquals(result.getPublishedDate(), "2005-09-01T00:00:00");
        assertEquals(result.getYearPublished(), Integer.valueOf(2005));
        assertEquals(result.getCreatedDate(), "2005-09-01T00:00:00");
        assertEquals(result.getUpdatedDate(), "2005-09-01T00:00:00");
        assertEquals(result.getDoi(), "doi1");
        assertEquals(result.getMagId(), "magId1");
        assertEquals(result.getPubmedId(), "pubmedId2");
        assertTrue(CollectionUtils.isEqualCollection(result.getOaiIds(), new HashSet<>(Arrays.asList("oai1", "oai2", "oai3"))));
        assertTrue(CollectionUtils.isEqualCollection(result.getCoreIds(), new HashSet<>(Arrays.asList("coreId1", "coreId2"))));

    }


    private ElasticSearchWorkMetadata getWorkMetadata(Integer id, String title, List<String> authors,
                                                      String description, Set<String> contributors,
                                                      Set<Integer> dataProviders, String fullText,
                                                      Set<String> sourceFullTextUrls, ElasticSearchLanguage language,
                                                      String downloadUrl, Integer citationCount,
                                                      List<ElasticSearchWorkReference> references, String documentType,
                                                      Set<ElasticSearchJournal> journals, Publisher publisher,
                                                      String acceptedDate, String depositedDate,
                                                      String publishedDate, Integer yearPublished,
                                                      String createdDate, String updatedDate, String doi,
                                                      String magId, String arxivId, String pubmedId, Set<String> oai,
                                                      Set<String> coreIds, Set<Identifier> identifier
                                                      ) {
        ElasticSearchWorkMetadata workMetadata = new ElasticSearchWorkMetadata();

        workMetadata.setId(id);
        workMetadata.setTitle(title);
        workMetadata.setAuthors(authors);
        workMetadata.setDescription(description);
        workMetadata.setContributors(contributors);
        workMetadata.setDataProviders(dataProviders);
        workMetadata.setFullText(fullText);
        workMetadata.setSourceFullTextUrls(sourceFullTextUrls);
        workMetadata.setLanguage(language);
        workMetadata.setDownloadUrl(downloadUrl);
        workMetadata.setCitationCount(citationCount);
        workMetadata.setReferences(references);
        workMetadata.setDocumentType(documentType);
        workMetadata.setJournals(journals);
        workMetadata.setPublisher(publisher);
        workMetadata.setAcceptedDate(acceptedDate);
        workMetadata.setDepositedDate(depositedDate);
        workMetadata.setPublishedDate(publishedDate);
        workMetadata.setYearPublished(yearPublished);
        workMetadata.setCreatedDate(createdDate);
        workMetadata.setUpdatedDate(updatedDate);
        workMetadata.setDoi(doi);
        workMetadata.setMagId(magId);
        workMetadata.setArxivId(arxivId);
        workMetadata.setPubmedId(pubmedId);
        workMetadata.setOaiIds(oai);
        workMetadata.setCoreIds(coreIds);
        workMetadata.setIdentifiers(identifier);
        return workMetadata;
    }

    @Test
    public void testGetDate() {
        logger.info("Date1: {}", new TextToDateTime("2008")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date2: {}", new TextToDateTime("2008-10")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date3: {}", new TextToDateTime("2008-10-12")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date4: {}", new TextToDateTime("23 Mar 2016 12:43:46")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date5: {}", new TextToDateTime("31 January 2004 12:43:46")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date6: {}", new TextToDateTime("29 Aug 2009")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date7: {}", new TextToDateTime("2008/12/12")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date8: {}", new TextToDateTime("12Aug2008")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date9: {}", new TextToDateTime("01/12/2008")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date10: {}", new TextToDateTime("2010-09-30T17:26:04Z")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date11: {}", new TextToDateTime("2009-8-1")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date12: {}", new TextToDateTime("2009-8")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date13: {}", new TextToDateTime("1/5/2011")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date14: {}", new TextToDateTime("01/5/2011")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date15: {}", new TextToDateTime("01-07-2001")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("Date16: {}", new TextToDateTime("May 9, 2021 12:00:00 AM")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    public void testReformat() {
        String formatedDate = new TextToDateTime("2008-10")
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        logger.info("formated date: {}", formatedDate);

        logger.info("Second formating: {}", new TextToDateTime(formatedDate)
                .asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    }

    @Test
    public void test() {
        Date date1 = new Date();
        Date date2 = null;
        String result = "smth" + date1 + "smth" + date2 + "smth";
        System.out.println(result);
    }
}
