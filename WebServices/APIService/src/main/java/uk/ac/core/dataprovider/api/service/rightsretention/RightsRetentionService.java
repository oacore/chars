package uk.ac.core.dataprovider.api.service.rightsretention;

import uk.ac.core.dataprovider.api.model.rightsretention.HighlightedArticleMetadata;
import uk.ac.core.dataprovider.api.model.rightsretention.ReportedArticleMetadata;

import java.io.File;
import java.util.List;

public interface RightsRetentionService {
    List<HighlightedArticleMetadata> findPotentialArticles(int repositoryId);

    List<HighlightedArticleMetadata> findPotentialArticles(int repositoryId, String harvestingSet);

    List<ReportedArticleMetadata> validatePotentialArticles(List<HighlightedArticleMetadata> articles);

    ReportedArticleMetadata validateExternalFile(File file);
}
