package uk.ac.core.extractmetadata.worker.issue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.ActionType;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.util.datastructure.Tuple;
import uk.ac.core.issueDetection.model.IssueBO;
import uk.ac.core.issueDetection.service.IssueService;
import uk.ac.core.issueDetection.util.IssueType;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetadataExtractIssueServiceImpl implements MetadataExtractIssueService {

    private static final String GENERIC_PDF_URL = "http://hdl.handle.net/123456789/";
    private static final String GENERIC_PDF_URL_ISSUE = "The issue with a generic PDF.";

    private static final String GENERIC_OAI = "oai:generic.eprints.org";
    private static final String INCORRECT_EPRINTS_ISSUE = "The issue with the incorrect EPrints configuration.";

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataExtractIssueServiceImpl.class);
    private static final String ISSUES_DISCOVERED_MSG = "The following issues have been discovered:\n";
    private static final String ISSUE_BODY_TEMPLATE = "%s, repositoryId: %s";

    private final IssueService issueService;

    public MetadataExtractIssueServiceImpl(IssueService issueService) {
        this.issueService = issueService;
    }

    @Override
    public void cleanMetadataExtractIssuesForRepo(int repositoryId) {
        issueService.deleteIssues(repositoryId, TaskType.EXTRACT_METADATA);
    }

    @Override
    public void reportIssues(int repositoryId, ArticleMetadata articleMetadata) {
        List<String> articleMetadataIdentifiers = articleMetadata.getIdentifiers();
        Set<String> pdfUrls = articleMetadata.getPdfUrls().keySet();

        issueService.saveIssues(detectMetadataExtractIssues(Long.valueOf(articleMetadata.getId()), Long.valueOf(repositoryId), articleMetadataIdentifiers, pdfUrls));
    }

    private List<IssueBO> detectMetadataExtractIssues(Long documentId, Long repositoryId, List<String> identifiers, Set<String> pdfUrls) {
        List<IssueBO> issues = detectGenericOaiIssues(documentId, repositoryId, identifiers);
        String oai = getOai(identifiers);
        issues.addAll(detectPdfUrlIssues(documentId, repositoryId, pdfUrls, oai));
        issues.forEach(this::logIssues);
        return issues;
    }

    private void logIssues(IssueBO issueBO) {
        LOGGER.error(ISSUES_DISCOVERED_MSG);
        LOGGER.error(String.format(ISSUE_BODY_TEMPLATE, issueBO.getMessage(), issueBO.getRepositoryId()));
    }

    private List<IssueBO> detectGenericOaiIssues(Long documentId, Long repositoryId, List<String> identifiers) {
        String oai = getOai(identifiers);
        return identifiers.stream()
                .filter(identifier -> identifier.startsWith(GENERIC_OAI))
                .map(genericOai -> new Tuple<>("genericOai", genericOai))
                .map(issueDetails -> createIssue(documentId, repositoryId, IssueType.GENERIC_OAI, INCORRECT_EPRINTS_ISSUE, issueDetails, oai))
                .collect(Collectors.toList());
    }

    private String getOai(List<String> identifiers) {
        Optional<String> oaiOpt = identifiers.stream().filter(identifier -> identifier.startsWith("oai:")).findFirst();
        String oai = "";
        if (oaiOpt.isPresent()) {
            oai = oaiOpt.get();
        }
        return oai;
    }


    private List<IssueBO> detectPdfUrlIssues(Long documentId, Long repositoryId, Set<String> pdfUrls, String oai) {
        return pdfUrls.stream()
                .filter(pdfUrl -> pdfUrl.contains(GENERIC_PDF_URL))
                .map(pdfUrl -> new Tuple<>("genericPdfUrl", pdfUrl))
                .map(details -> createIssue(documentId, repositoryId, IssueType.GENERIC_ATTACHMENT_URL, GENERIC_PDF_URL_ISSUE, details, oai))
                .collect(Collectors.toList());
    }

    private IssueBO createIssue(Long documentId, Long repositoryId, IssueType issueType, String message, Tuple<String, String> details, String oaiIdentifier) {
        Map<String, String> issueDetails = new HashMap<>();
        issueDetails.put(details.getX(), details.getY());

        return new IssueBO.Builder(repositoryId)
                .documentId(documentId)
                .issueType(issueType)
                .message(message)
                .details(issueDetails)
                .oai(oaiIdentifier)
                .build();

    }

}