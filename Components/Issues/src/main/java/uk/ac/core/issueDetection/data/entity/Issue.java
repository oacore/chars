package uk.ac.core.issueDetection.data.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import uk.ac.core.common.model.legacy.ActionType;
import uk.ac.core.common.util.datetime.DateTimeUtil;
import uk.ac.core.issueDetection.util.GetIssueIdUtil;
import uk.ac.core.issueDetection.util.IssueType;

import java.time.LocalDateTime;
import java.util.Map;

@Document(indexName = "issues-v2", type = "issue", shards = 16)
public final class Issue {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private long repositoryId;

    @Field(type = FieldType.Keyword)
    private IssueType type;

    @Field(type = FieldType.Text)
    private String message;

    /**
     * Dynamic data, that serves as an description of an issue, depending on the issue type.
     */
    @Field(type = FieldType.Nested)
    private Map<String, String> details;

    @Field(type = FieldType.Long)
    private long documentIdentifier;

    @Field(type = FieldType.Keyword)
    private String actionType;

    @Field(type = FieldType.Keyword)
    private String severity;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = DateTimeUtil.STANDARD_LOCAL_DATE_TIME)
    private String createdAt = DateTimeUtil.formatInStandardBasicLocalDateTime(LocalDateTime.now());

    @Field(type = FieldType.Text)
    private String oai = null;

    public Issue(long repositoryId, long documentIdentifier, IssueType issueType, String message, Map<String, String> details, String oai) {
        this.repositoryId = repositoryId;
        this.documentIdentifier = documentIdentifier;
        this.type = issueType;
        this.actionType = issueType.getRelatedAction().getName();
        this.severity = issueType.getSeverity().name();
        this.message = message;
        this.details = details;
        this.id = GetIssueIdUtil.getIssueId(documentIdentifier, repositoryId, issueType);
        this.oai = oai;
    }

    public Issue() {

    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType.getName();
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(long documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getOai() {
        return oai;
    }

    public void setOai(String oai) {
        this.oai = oai;
    }
}