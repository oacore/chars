package uk.ac.core.elasticsearch.services.converter;

import uk.ac.core.common.model.legacy.LegacyRepository;
import uk.ac.core.elasticsearch.entities.BasicArticleMetadata;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.entities.ElasticSearchCitation;
import uk.ac.core.elasticsearch.services.model.CompactArticleBO;
import uk.ac.core.elasticsearch.services.model.CompleteArticleBO;

import java.util.ArrayList;
import java.util.List;

public final class ArticleConverter {

    public static CompactArticleBO convertToArticleBO(BasicArticleMetadata articleMetadata) {
        CompactArticleBO compactArticleBO = new CompactArticleBO();
        compactArticleBO.setDocumentId(Integer.parseInt(articleMetadata.getId()));
        compactArticleBO.setFullText(articleMetadata.hasText());
        return compactArticleBO;
    }

    public static CompactArticleBO convertToCompactArticleBO(ElasticSearchArticleMetadata elasticSearchArticleMetadata) {
        CompactArticleBO compactArticleBO = new CompactArticleBO();
        compactArticleBO.setDocumentId(Integer.parseInt(elasticSearchArticleMetadata.getId()));
        compactArticleBO.setFullText(elasticSearchArticleMetadata.getRepositoryDocument().getTextStatus() == 1);
        return compactArticleBO;
    }

    public static CompleteArticleBO convertToCompleteArticleBO(ElasticSearchArticleMetadata metadata) {
        CompleteArticleBO BO = new CompleteArticleBO();
        BO.setCompactArticleBO(convertToCompactArticleBO(metadata));
        BO.setDoi(metadata.getDoi());
        BO.setOai(metadata.getOai());
        BO.setTitle(metadata.getTitle());
        BO.setAuthors(metadata.getAuthors());
        BO.setDescription(metadata.getDescription());
        BO.setContributors(metadata.getContributors());
        BO.setDatePublished(metadata.getDatePublished());
        BO.setDocumentType(metadata.getDocumentType());
        BO.setDocumentTypeConfidence(metadata.getDocumentTypeConfidence());
        BO.setDownloadUrl(metadata.getDownloadUrl());
        BO.setFullTextIdentifier(metadata.getFullTextIdentifier());
        BO.setPdfHashValue(metadata.getPdfHashValue());
        BO.setPublisher(metadata.getPublisher());
        BO.setRawRecordXml(metadata.getRawRecordXml());
        BO.setRelations(metadata.getRelations());
        BO.setJournals(metadata.getJournals());
        if (metadata.getLanguage() != null) {
            BO.setLanguage(metadata.getLanguage().getCode());
        }
        BO.setYear(metadata.getYear());
        BO.setTopics(metadata.getTopics());
        BO.setSubjects(metadata.getSubjects());

        List<ElasticSearchCitation> esCitations = metadata.getCitations();
        List<CompleteArticleBO.Citation> references = new ArrayList<>();

        for (ElasticSearchCitation elasticSearchCitation : esCitations) {
            references.add(new CompleteArticleBO.Citation(
                    elasticSearchCitation.getId(),
                    elasticSearchCitation.getTitle(),
                    elasticSearchCitation.getAuthors(),
                    elasticSearchCitation.getDate(),
                    elasticSearchCitation.getDoi(),
                    elasticSearchCitation.getRaw(),
                    elasticSearchCitation.getCites()));
        }

        BO.setReferences(references);

        List<LegacyRepository> esRepos = metadata.getRepositories();
        List<CompleteArticleBO.Repository> repositories = new ArrayList<>();

        for (LegacyRepository esRepo : esRepos) {
            repositories.add(new CompleteArticleBO.Repository(
                    Integer.parseInt(esRepo.getId()),
                    esRepo.getName(),
                    esRepo.getOpenDoarId()));
        }

        BO.setRepositories(repositories);

        return BO;

    }
}
