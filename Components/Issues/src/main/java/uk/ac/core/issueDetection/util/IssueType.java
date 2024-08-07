package uk.ac.core.issueDetection.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import uk.ac.core.common.model.legacy.ActionType;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum IssueType {
    OAI_ENDPOINT(IssueSeverity.ERROR, ActionType.METADATA_DOWNLOAD, "There were problems with an OAI endpoint."),
    INVALID_OAIPMH_ENDPOINT(IssueSeverity.ERROR, ActionType.METADATA_DOWNLOAD, "An OAI-PMH endpoint is invalid."),
    SSL_CERTIFICATE_ERROR(IssueSeverity.ERROR, ActionType.METADATA_DOWNLOAD, "A SSL certificate error occurred."),

    GENERIC_OAI(IssueSeverity.WARNING, ActionType.METADATA_EXTRACT, "A generic identifier is used."),
    GENERIC_ATTACHMENT_URL(IssueSeverity.WARNING, ActionType.METADATA_EXTRACT, "A generic document url is used."),

    ROBOTS(IssueSeverity.ERROR, ActionType.DOCUMENT, "There were problems with the configured robots.txt."),
    ATTACHMENT_MALFORMED_URL(IssueSeverity.ERROR, ActionType.DOCUMENT, "A document URL is malformed."),
    ATTACHMENT_NOT_VALID(IssueSeverity.ERROR, ActionType.DOCUMENT, "A document is not valid."),
    NO_FULL_TEXT_LINKS(IssueSeverity.WARNING, ActionType.DOCUMENT, "No attachments found."),
    EXTERNAL_UNKNOWN(IssueSeverity.ERROR, ActionType.DOCUMENT, "No full text was found due to unknown external reasons."),
    INTERNAL(IssueSeverity.ERROR, ActionType.DOCUMENT, "An internal system error occurred."),
    ENCRYPTED_ATTACHMENT(IssueSeverity.ERROR, ActionType.DOCUMENT, "A document was found but it had digital restrictions."),
    ATTACHMENT_TITLE_MISMATCH(IssueSeverity.ERROR, ActionType.DOCUMENT, "A document was found but a title did not match a title in the metadata."),
    RESTRICTED_ATTACHMENT(IssueSeverity.WARNING, ActionType.DOCUMENT, "The document exists on the repository but the download is restricted. (e.g document is embargoed)"),
    UNSUPPORTED_FILETYPE(IssueSeverity.WARNING, ActionType.DOCUMENT, "The attached document has an unsupported type."),
    NO_VALID_ATTACHMENT_DOWNLOAD_URLS(IssueSeverity.ERROR, ActionType.DOCUMENT, "No eligible document URLs were available."),
    ATTACHMENT_IO_EXCEPTION(IssueSeverity.ERROR, ActionType.DOCUMENT, "The error has occurred during the document processing."),
    SLOW_NETWORK(IssueSeverity.ERROR, ActionType.DOCUMENT, "The document download was suspended due to the network slowness."),
    NON_EXISTENT_PAGE_ATTACHMENT(IssueSeverity.ERROR, ActionType.DOCUMENT, "A document does not exist on the provided page(404 error)."),
    UNSPECIFIED_DOWNLOAD_ERROR(IssueSeverity.ERROR, ActionType.DOCUMENT, "There was an internal unspecified error."),
    ATTACHMENT_TOO_BIG(IssueSeverity.ERROR, ActionType.DOCUMENT, "The document was too large."),
    ATTACHMENT_SAME_DOMAIN_POLICY_ENFORCED(IssueSeverity.ERROR, ActionType.DOCUMENT, "The OAI-PMH domain and the attachment domain must be the same"),
    LINK_REDIRECTED_TO_DISALLOWED_URL(IssueSeverity.ERROR, ActionType.DOCUMENT, "A valid link redirected to a site which CORE is not permitted to access (see Same Domain Policy)"),

    // Language Detection Issues
    POTENTIAL_LANGUAGE_MISMATCH(IssueSeverity.WARNING, ActionType.LANGUAGE_DETECTION, "The language reported in the metadata did not match the fulltext detected language"),
    NORMALISED_LANGUAGE(IssueSeverity.INFO, ActionType.LANGUAGE_DETECTION, "The metadata language was normalised"),
    UNPARSABLE_LANGUAGE(IssueSeverity.WARNING, ActionType.LANGUAGE_DETECTION, "The given language was not understood"),

    // License issue
    UNRECOGNIZED_LICENSE_STRING(IssueSeverity.WARNING, ActionType.DOCUMENT, "License field for document not recognised");

    private final String description;
    private final ActionType relatedAction;
    private final IssueSeverity severity;

    IssueType(IssueSeverity issueSeverity, ActionType relatedAction, String description) {
        this.description = description;
        this.relatedAction = relatedAction;
        this.severity = issueSeverity;
    }

    public String getDescription() {
        return description;
    }

    public ActionType getRelatedAction() {
        return relatedAction;
    }

    public IssueSeverity getSeverity() {
        return severity;
    }
}