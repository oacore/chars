package uk.ac.core.elasticsearch.services.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.*;
import uk.ac.core.common.util.TextToDateTime;
import uk.ac.core.database.entity.FileExtension;
import uk.ac.core.database.entity.FileExtensionType;
import uk.ac.core.database.model.DocumentMetadataExtendedAttributes;
import uk.ac.core.database.model.JournalISSN;
import uk.ac.core.database.model.PublisherName;
import uk.ac.core.database.mucc.MUCCDocument;
import uk.ac.core.database.mucc.MUCCDocumentDAO;
import uk.ac.core.database.orcid.OrcidDAO;
import uk.ac.core.database.repository.FileExtensionRepository;
import uk.ac.core.database.service.document.DocumentTdmStatusDAO;
import uk.ac.core.database.service.document.DocumentUrlDAO;
import uk.ac.core.database.service.document.ExtendedAttributesDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.database.service.journals.JournalsDAO;
import uk.ac.core.database.service.publishername.PublisherNameDAO;
import uk.ac.core.database.service.repositories.RepositoriesDAO;
import uk.ac.core.elasticsearch.entities.*;
import uk.ac.core.filesystem.services.FilesystemDAO;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lucasanastasiou
 */
@Service
public class ElasticSearchArticleMetadataBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchArticleMetadataBuilder.class);

    @Autowired
    private RepositoriesDAO repositoriesDAO;

    @Autowired
    private JournalsDAO journalsDAO;

    @Autowired
    private RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    private DocumentUrlDAO documentUrlDAO;

    @Autowired
    private DocumentTdmStatusDAO documentTdmStatusDAO;

    @Autowired
    private FilesystemDAO filesystemDAO;

    @Autowired
    private MUCCDocumentDAO mUCCDocumentDAO;

    @Autowired
    private OrcidDAO orcidDAO;

    @Autowired
    private ExtendedAttributesDAO extendedAttributesDAO;

    @Autowired
    private PublisherNameDAO publisherNameDAO;

    @Autowired
    private FileExtensionRepository fileExtensionRepository;


    public ElasticSearchArticleMetadata convertArticleMetadataToElasticDocument(ArticleMetadata am) {
        if (am == null) {
            return null;
        }
        ElasticSearchArticleMetadata esam = new ElasticSearchArticleMetadata();

        esam.setId("" + am.getId());
        //----
        List<String> amAuthors = am.getAuthors();
        esam.setAuthors(amAuthors);
        //----
        List<Citation> amCitations = am.getCitations();
        if (amCitations != null) {
            List<ElasticSearchCitation> esamCitations = ElasticSearchCitation.convertFromCitation(amCitations);
            esam.setCitations(esamCitations);
        }
        //----

        //----
        List<String> amContr = am.getContributors();
        esam.setContributors(amContr);
        //----
        try {
            if (am.getDate() != null) {
                String datePublishedString = new TextToDateTime(am.getDate()).asIsoString();
                esam.setDatePublished(datePublishedString);
            }
        } catch (DateTimeException e) {
            logger.error("Can't parse the date : '{}'", am.getDate());
        }

        //----
        esam.setDeleted(am.getDeleted());
        //----
        esam.setDescription(am.getDescription());
        //---
        esam.setFullText(am.getFullText());
        //---        
        String doi = am.getDoi();
        List<String> amIdentifiers = new ArrayList<>(am.getIdentifiers());
        amIdentifiers.add(doi);
        esam.setIdentifiers(amIdentifiers);
        //--- TODO for journals ask Dasha
        List<String> journalsIds = am.getJournalIdentifiers();
        List<String> journalIssns = am.getJournalIssns();
        List<ElasticSearchJournal> esJournals = null;
        if ((journalsIds != null && !journalsIds.isEmpty()) || (journalIssns != null && !journalIssns.isEmpty())) {
            List<String> unifiedIds = this.union(journalsIds, journalIssns);
            esJournals = new ArrayList<>();
            ElasticSearchJournal esj = new ElasticSearchJournal();
            for (String jid : unifiedIds) {

                esj.addIdentifier(jid);
                long jt0 = System.currentTimeMillis();
                String title;
                JournalISSN issn = journalsDAO.findByIdentifier(jid);
                if (issn != null) {
                    title = issn.getTitle();
                } else {
                    title = journalsDAO.getJournalTitleByIdentifier(jid);
                }
                long jt1 = System.currentTimeMillis();
                logger.info("Journals tite took " + (jt1 - jt0));
                if (title != null && !title.isEmpty()) {
                    esj.setTitle(title);
                }
            }
            esJournals.add(esj);
        }

        esam.setJournals(esJournals);
        //---
        Language amLang = am.getLanguage();
        ElasticSearchLanguage esl = new ElasticSearchLanguage();
        if (amLang != null) {
            esl.setCode(amLang.getCode());
            esl.setId(amLang.getLanguageId());
            esl.setName(amLang.getName());
            esam.setLanguage(esl);
        }

        //---
        //---
        Optional<PublisherName> publisherNameOpt;
        if (am.getDoi() != null) {
            int slashIndex = am.getDoi().indexOf("/");
            if (slashIndex != -1) {
                publisherNameOpt = publisherNameDAO.findByPrefix(am.getDoi().substring(0, slashIndex + 1));
            } else {
                publisherNameOpt = publisherNameDAO.findByName(am.getPublisher());
            }
        } else {
            publisherNameOpt = publisherNameDAO.findByName(am.getPublisher());
        }

        if (publisherNameOpt.isPresent()) {
            esam.setPublisher(publisherNameOpt.get().getPrimaryName());
        } else {
            esam.setPublisher(am.getPublisher());
        }

        //---
        int maxFieldSize = 32766;
        int fieldSizeInBytes = am.getRawRecordXml().getBytes(StandardCharsets.UTF_8).length;
        if (am.getRawRecordXml() != null && fieldSizeInBytes > maxFieldSize) {
            esam.setRawRecordXml("");
            logger.warn("Document ID {} exceeds elasticsearch key length of {} " +
                    "(String size {})", am.getId(), maxFieldSize, fieldSizeInBytes);
        } else {
            esam.setRawRecordXml(am.getRawRecordXml());
        }
        //---
        esam.setRelations(am.getRelations());
        //---
        List<Integer> repoIds = am.getRepositoryIds();
        List<String> repoNames = am.getRepositories();

        List<LegacyRepository> repos = new ArrayList<>();
        for (Integer rid : repoIds) {
            String repoName = "";
            try {
                long rn0 = System.currentTimeMillis();
                repoName = repositoriesDAO.getRepositoryName(rid);
                long rn1 = System.currentTimeMillis();
                logger.info("Repo name took " + (rn1 - rn0));
            } catch (Exception e) {
                System.out.println("tadaaaaa");
            }
            LegacyRepository esr = new LegacyRepository();
            esr.setId("" + rid);
            esr.setName(repoName);
            repos.add(esr);
        }
        esam.setRepositories(repos);
        //---
        long rd0 = System.currentTimeMillis();
        RepositoryDocument rd = repositoryDocumentDAO.getRepositoryDocumentById(am.getId());
        long rd1 = System.currentTimeMillis();
        logger.info("Repodocument took" + (rd1 - rd0));
        ElasticSearchRepositoryDocument esrd = new ElasticSearchRepositoryDocument();
        esrd.setDeletedStatus(am.getDeleted());
        esrd.setIndexed(rd.getIndexed());
        esrd.setMetadataAdded(am.getRepositoryDocument().getMetadataAdded());
        esrd.setMetadataUpdated(am.getRepositoryDocument().getMetadataUpdated());
        esrd.setPdfSize(am.getSize());
        esrd.setPdfStatus(rd.getPdfStatus());
        esrd.setTextStatus(rd.getTextStatus());
        esrd.setTimestamp(rd.getDateTimeStamp());
        esrd.setDepositedDate(am.getDepositedDateStamp());
        if (rd.getPdfUrl() != null && !rd.getPdfUrl().startsWith("file://")) {
            esrd.setPdfOrigin(rd.getPdfUrl());
        }
        long tdm0 = System.currentTimeMillis();
        DocumentTdmStatus tdmStatus = documentTdmStatusDAO.getDocumentTdmStatus(am.getId());
        long tdm1 = System.currentTimeMillis();
        logger.info("Tdm status took " + (tdm1 - tdm0));
        if (tdmStatus == null) {
            tdmStatus = new DocumentTdmStatus(am.getId(), false, false);
        }
        esrd.setTdmOnly(tdmStatus.getTdmOnly());
        esam.setRepositoryDocument(esrd);

        //---
        esam.setFullTextIdentifier(rd.getPdfUrl());
        //--- get Urls and convert to list of strings
        long url0 = System.currentTimeMillis();
        List<DocumentUrl> documentsUrl = documentUrlDAO.load(am.getId());
        long url1 = System.currentTimeMillis();
        logger.info("Url took" + (url1 - url0));
        List<String> urls = new ArrayList<>();
        for (DocumentUrl url : documentsUrl) {
            if (!url.getUrl().startsWith("file://")) {
                urls.add(url.getUrl());
            }
        }
        esam.setUrls(urls);
        //---
        esam.setSimilarities(am.getSimilarities());
        //---
        esam.setSubjects(am.getSubjects());
        //---
        esam.setTitle(am.getTitle());
        //---
        esam.setTopics(am.getTopics());
        //---
        esam.setTypes(am.getTypes());
        //---
        esam.setYear(am.getYear());
        //---
        esam.setRelations(am.getRelations());
        //---
        long mucc0 = System.currentTimeMillis();
        MUCCDocument mUCCDocument = mUCCDocumentDAO.load(am.getId());
        long mucc1 = System.currentTimeMillis();
        logger.info("Mucc took " + (mucc1 - mucc0));
        if (mUCCDocument != null) {

            esam.setCitationCount(mUCCDocument.getCitationCount());
            esam.setEstimatedCitationCount(mUCCDocument.getEstimatedCitationCount());
            esam.setAcceptedDate(mUCCDocument.getAccepted());

            esam.setPublishedDate(mUCCDocument.getPublished());
            esam.setIssn(mUCCDocument.getIssn());

            ElasticSearchCrossrefDocument elasticSearchCrossrefDocument = new ElasticSearchCrossrefDocument();
            elasticSearchCrossrefDocument.setAcceptedDate(mUCCDocument.getAccepted());
            elasticSearchCrossrefDocument.setDepositedDate(mUCCDocument.getDeposited());
            elasticSearchCrossrefDocument.setPublishedDate(mUCCDocument.getPublished());
            elasticSearchCrossrefDocument.setIssn(mUCCDocument.getIssn());
            elasticSearchCrossrefDocument.setDoi(mUCCDocument.getDoi());

            esam.setCrossrefDocument(elasticSearchCrossrefDocument);

            ElasticSearchMAGDocument elasticSearchMAGDocument = new ElasticSearchMAGDocument();
            elasticSearchMAGDocument.setCitationsCount(mUCCDocument.getCitationCount());
            elasticSearchMAGDocument.setEstimatedCitationsCount(mUCCDocument.getEstimatedCitationCount());
            elasticSearchMAGDocument.setMagId(mUCCDocument.getMagId());
            esam.setMagDocument(elasticSearchMAGDocument);

        }
        long orcid0 = System.currentTimeMillis();
        List<String> orcidAuthorStrings = orcidDAO.getOrcidsOfAuthorsOfArticle(am.getId());
        long orcid1 = System.currentTimeMillis();
        logger.info("ORCID took " + (orcid1 - orcid0));
        List<OrcidAuthor> orcidAuthors = this.convertToOrcidAuthors(orcidAuthorStrings);
        esam.setOrcidAuthors(orcidAuthors);

        if (esam.getPublishedDate() == null && esam.getDatePublished() != null) {
            Timestamp datePublished = null;
            datePublished = new Timestamp(new TextToDateTime(esam.getDatePublished()).asLocalDateTime().toEpochSecond(ZoneOffset.UTC) * 1000);
            esam.setPublishedDate(datePublished);
        }

        if (esam.getRepositoryDocument().getDepositedDate() != null) {
            esam.setDepositedDate(new Timestamp(esam.getRepositoryDocument().getDepositedDate().getTime()));
        } else if (mUCCDocument != null) {
            esam.setDepositedDate(mUCCDocument.getDeposited());
        }

        if (am.getDoi() == null && mUCCDocument != null) {
            esam.setDoi(mUCCDocument.getDoi());
        } else {
            esam.setDoi(am.getDoi());
        }

        //---
        esam.setDocumentType(am.getDocumentType());
        //---
        esam.setDocumentTypeConfidence(am.getDocumentTypeConfidence());

        esam.setOai(am.getOAIIdentifier());

        String downloadUrl = "";
        if (esrd.getPdfStatus() == 1) {
            if (esrd.isTdmOnly()) {
                downloadUrl = am.getPdfUrl();
            } else {
                String extension = fileExtensionRepository.findById(rd.getIdDocument())
                        .map(FileExtension::getName)
                        .orElse(FileExtensionType.PDF)
                        .toString();

                downloadUrl = "https://core.ac.uk/download/" + rd.getIdDocument() + "." + extension;
            }
        } else {
            if (esrd.isTdmOnly()) {
                downloadUrl = am.getPdfUrl();
            }
            if (mUCCDocument != null) {
                Optional<DocumentUrl> unpaywallUrl = documentsUrl.stream().filter(dUrl -> dUrl.getpDFUrlSource().equals(PDFUrlSource.UNPAYWALL)).findAny();
                if (unpaywallUrl.isPresent()) {
                    downloadUrl = unpaywallUrl.get().getUrl();
                }
            }
        }
        esam.setDownloadUrl(downloadUrl);

        if (esrd.getPdfStatus() == 1 && rd != null) {
            esam.setPdfHashValue(calculatePdfHash(rd.getIdDocument(), rd.getIdRepository()));
        }
        esam.setSetSpecs(this.extractSetSpecsFromXml(esam.getRawRecordXml()));
        Optional<DocumentMetadataExtendedAttributes> documentMetadataExtendedAttributes = extendedAttributesDAO.get(rd.getIdDocument());
        if (documentMetadataExtendedAttributes.isPresent()) {
            ElasticSearchExtendedMetadataAttributes elasticSearchExtendedMetadataAttributes = new ElasticSearchExtendedMetadataAttributes();
            elasticSearchExtendedMetadataAttributes.setAttachmentCount(new Long(documentMetadataExtendedAttributes.get().getAttachmentCount()));
            if (null != documentMetadataExtendedAttributes.get().getRepositoryMetadataPublicReleaseDate()) {
                elasticSearchExtendedMetadataAttributes.setPublicReleaseDate(new Timestamp(documentMetadataExtendedAttributes.get().getRepositoryMetadataPublicReleaseDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            }
            esam.setExtendedMetadataAttributes(elasticSearchExtendedMetadataAttributes);
        }


        return esam;
    }

    private List<String> extractSetSpecsFromXml(String rawRecordXml) {
        String cleanXml = rawRecordXml.replaceAll("\n", "").replaceAll("\t", "").replaceAll("\\s+", "");
        Pattern pattern = Pattern.compile("<setSpec>(\\w+)</setSpec>");
        Matcher matcher = pattern.matcher(cleanXml);
        List<String> setSpecs = new ArrayList<>();
        while(matcher.find()){
            setSpecs.add(matcher.group(1));
        }
        return setSpecs;
    }

    private String calculatePdfHash(Integer articleId, Integer repositoryId) {
        return filesystemDAO.calculatePdfHashValue(articleId, repositoryId);
    }

    private <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }

    private List<OrcidAuthor> convertToOrcidAuthors(List<String> orcidAuthorStrings) {
        List<OrcidAuthor> oAuthors = null;
        if (orcidAuthorStrings != null && !orcidAuthorStrings.isEmpty()) {
            oAuthors = new ArrayList<>();
        }
        for (String oas : orcidAuthorStrings) {
            OrcidAuthor oa = new OrcidAuthor(oas);
            oAuthors.add(oa);
        }
        return oAuthors;
    }

}
