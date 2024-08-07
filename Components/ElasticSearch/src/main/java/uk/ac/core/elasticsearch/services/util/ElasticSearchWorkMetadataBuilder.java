package uk.ac.core.elasticsearch.services.util;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.util.TextToDateTime;
import uk.ac.core.database.service.document.FieldStudyDAO;
import uk.ac.core.database.service.journals.JournalsDAO;
import uk.ac.core.elasticsearch.entities.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author MTarasyuk
 */

@Service
public class ElasticSearchWorkMetadataBuilder {

    @Autowired
    JournalsDAO journalsDAO;

    @Autowired
    private FieldStudyDAO fieldStudyDAO;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ElasticSearchWorkMetadataBuilder.class);

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ElasticSearchWorkMetadata generateElasticSearchWorkMetadata(
            ElasticSearchArticleMetadata elasticSearchArticleMetadata, Integer workId){

        if(elasticSearchArticleMetadata == null){
            String message = String.format("Document doesn't have elasticsearch " +
                    "article metadata and wasn't index with 'article' index");
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        ElasticSearchWorkMetadata workMetadata = new ElasticSearchWorkMetadata();

        String doi = elasticSearchArticleMetadata.getDoi();

        int repositoryId = Integer.parseInt(elasticSearchArticleMetadata.getRepositories().get(0).getId());

        ElasticSearchCrossrefDocument muccDocument = elasticSearchArticleMetadata.getCrossrefDocument();
        if(workId != null){
            workMetadata.setId(workId);
        }
        workMetadata.setTitle(elasticSearchArticleMetadata.getTitle());
        workMetadata.setAuthors(elasticSearchArticleMetadata.getAuthors());
        workMetadata.setDescription(elasticSearchArticleMetadata.getDescription());

        workMetadata.setContributors(getOrDefault(elasticSearchArticleMetadata.getContributors()));

        workMetadata.setDataProviders(elasticSearchArticleMetadata.getRepositories().stream()
                .map(repository -> Integer.parseInt(repository.getId()))
                .collect(Collectors.toSet()));


        workMetadata.setFullText(elasticSearchArticleMetadata.getFullText());

        workMetadata.setSourceFullTextUrls(getOrDefault(elasticSearchArticleMetadata.getFullTextIdentifier()));

        workMetadata.setLanguage(elasticSearchArticleMetadata.getLanguage());

        workMetadata.setDownloadUrl(elasticSearchArticleMetadata.getDownloadUrl());
        workMetadata.setCitationCount(elasticSearchArticleMetadata.getCitationCount());
        workMetadata.setReferences(convert(elasticSearchArticleMetadata.getCitations()));
        workMetadata.setDocumentType(elasticSearchArticleMetadata.getDocumentType());
        if(elasticSearchArticleMetadata.getJournals() != null){
            workMetadata.setJournals(getOrDefault(elasticSearchArticleMetadata.getJournals()));
        } else {
           String issn =  elasticSearchArticleMetadata.getIssn();
           if(issn != null){
               ElasticSearchJournal elasticSearchJournal = new ElasticSearchJournal();
               elasticSearchJournal.setIdentifiers(Collections.singletonList(issn));
               String title = journalsDAO.getJournalTitleByIdentifier("%" + issn + "%");
               if(title != null){
                   elasticSearchJournal.setTitle(title);
               }
               workMetadata.setJournals(new HashSet<>(Collections.singletonList(elasticSearchJournal)));
           }

        }

        Publisher publisher = new Publisher();
        publisher.setName(elasticSearchArticleMetadata.getPublisher());
        publisher.setIdentifiers(getOrDefault("default"));
        workMetadata.setPublisher(publisher);

        workMetadata.setCoreIds(new HashSet<>());
        if(muccDocument != null ){
            if(muccDocument.getAcceptedDate() != null){
                workMetadata.setAcceptedDate(muccDocument.getAcceptedDate()
                        .toLocalDateTime().format(formatter));
            }

            if(muccDocument.getPublishedDate() != null){
                workMetadata.setPublishedDate(muccDocument.getPublishedDate().toLocalDateTime()
                        .format(formatter));
                workMetadata.setYearPublished(muccDocument.getPublishedDate().toLocalDateTime().getYear());
            } else if (elasticSearchArticleMetadata.getDatePublished() != null){
                workMetadata.setPublishedDate(new TextToDateTime(elasticSearchArticleMetadata.getDatePublished())
                        .asLocalDateTime().format(formatter));
                workMetadata.setYearPublished(elasticSearchArticleMetadata.getYear());
            }
            if(elasticSearchArticleMetadata.getMagDocument().getMagId() != null){
                workMetadata.setMagId(elasticSearchArticleMetadata.getMagDocument().getMagId().toLowerCase());
            }
        } else {
            if(elasticSearchArticleMetadata.getPublishedDate() != null){
                workMetadata.setPublishedDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        .format(elasticSearchArticleMetadata.getPublishedDate()));
                workMetadata.setYearPublished(elasticSearchArticleMetadata.getYear());
            }
        }

        if(elasticSearchArticleMetadata.getId() != null){
            workMetadata.setCoreIds(getOrDefault(elasticSearchArticleMetadata.getId().toLowerCase()));
        }

        if(elasticSearchArticleMetadata.getExtendedMetadataAttributes() != null
                && elasticSearchArticleMetadata.getExtendedMetadataAttributes().getPublicReleaseDate() != null){
            workMetadata.setDepositedDate(elasticSearchArticleMetadata.getExtendedMetadataAttributes()
                    .getPublicReleaseDate().toLocalDateTime().format(formatter));
        } else if(muccDocument != null && muccDocument.getDepositedDate() != null) {
            workMetadata.setDepositedDate(muccDocument.getDepositedDate().toLocalDateTime()
                    .format(formatter));
        }


        workMetadata.setCreatedDate(LocalDateTime.ofInstant(elasticSearchArticleMetadata.getRepositoryDocument()
                .getMetadataAdded().toInstant(), ZoneId.systemDefault()).format(formatter));
        workMetadata.setUpdatedDate(LocalDateTime.ofInstant(elasticSearchArticleMetadata.getRepositoryDocument()
                .getMetadataUpdated().toInstant(), ZoneId.systemDefault()).format(formatter));

        workMetadata.setIndexedDate(LocalDateTime.now(ZoneId.systemDefault()).format(formatter));
        if (doi != null){
            workMetadata.setDoi(doi.toLowerCase());
        }

        if(repositoryId == 144){
            String[] split = elasticSearchArticleMetadata.getOai().split(":");
            if(split.length > 0){
                workMetadata.setArxivId(split[split.length - 1].toLowerCase());
            }
        }
        if(repositoryId == 150){
            String[] split = elasticSearchArticleMetadata.getOai().split(":");
            if(split.length > 0){
                workMetadata.setPubmedId(split[split.length - 1].toLowerCase());
            }
        }

        if(elasticSearchArticleMetadata.getOai() != null){
            workMetadata.setOaiIds(getOrDefault(elasticSearchArticleMetadata.getOai().toLowerCase()));
        }

        workMetadata.setIdentifiers(new HashSet<>(Arrays.asList(
                createIdentifier(IdentifierType.ARXIV_ID, workMetadata.getArxivId()),
                createIdentifier(IdentifierType.MAG_ID, workMetadata.getMagId()),
                createIdentifier(IdentifierType.PUBMED_ID, workMetadata.getPubmedId()))));

        if(workMetadata.getCoreIds() != null && !workMetadata.getCoreIds().isEmpty()){
            workMetadata.getIdentifiers()
                    .addAll(createIdentifiers(IdentifierType.CORE_ID, workMetadata.getCoreIds()));
        }

        if(workMetadata.getOaiIds() != null && !workMetadata.getOaiIds().isEmpty()){
            workMetadata.getIdentifiers()
                    .addAll(createIdentifiers(IdentifierType.OAI_ID, workMetadata.getOaiIds()));
        }

        if(workMetadata.getDoi() != null){
            workMetadata.getIdentifiers()
                    .add(createIdentifier(IdentifierType.DOI, workMetadata.getDoi()));
        }

        workMetadata.setIdentifiers(workMetadata.getIdentifiers().stream()
                .filter(i -> i.getIdentifier() != null)
                .collect(Collectors.toSet()));

        workMetadata.setFieldsOfStudy(getFirstFieldOfStudy(workMetadata));

        return workMetadata;
    }

    private String getFirstFieldOfStudy(ElasticSearchWorkMetadata metadata) {
        return fieldStudyDAO.findFirstNormalizedNameByIdIn(new ArrayList<>
                (metadata.getCoreIds()
                        .stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList())))
                .orElse(null);
    }

    private <T> HashSet<T> getOrDefault(Collection<T> elements) {
        if(elements == null || elements.isEmpty()) {
            return new HashSet<>();
        } else {
            return new HashSet<>(elements);
        }
    }

    private <T> Set<T> getOrDefault(T element) {
        if(element == null) {
            return new HashSet<>();
        } else {
            return new HashSet<>(Collections.singletonList(element));
        }
    }

    private Collection<Identifier> createIdentifiers(IdentifierType type, Collection<String> identifiers) {
        return identifiers.stream()
                .map(i -> createIdentifier(type, i))
                .collect(Collectors.toList());
    }

    private Identifier createIdentifier(IdentifierType type, String identifier){
        Identifier result = new Identifier();
        result.setType(type);
        if(identifier != null) {
            result.setIdentifier(identifier.toLowerCase());
        }
        return result;
    }

    private List<ElasticSearchWorkReference> convert(List<ElasticSearchCitation> elasticSearchCitations){
        return elasticSearchCitations.stream()
                .map(ElasticSearchWorkReference::new)
                .collect(Collectors.toList());
    }
}
