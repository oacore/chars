package uk.ac.core.metadata_download_failure_diagnosis_tool.model;

public enum FailureDiagnosisResolution {
    OAI_PMH_ENDPOINT_UNAVAILABLE("OAI-PMH endpoint cannot be reached or " +
                                         "it redirects to a different resource that cannot be harvested by CHARS"),
    OAI_PMH_ENDPOINT_EMPTY("OAI-PMH endpoint available but ListRecords request returned empty list"),
    DB_ISSUE("Failed because of internal database issue"),
    TICKET_FOR_OPS("Create ticket for Ops team for detailed investigation"),
    OAI_PMH_ENDPOINT_FIXED("OAI-PMH endpoint was unavailable for different reasons " +
            "but we managed to find the correct one"),
    REPOSITORY_BROKEN("The OAI-PMH endpoint wasn't fixed automatically so the repository" +
            "was marked as skipped (not disabled!)");

    private final String description;

    FailureDiagnosisResolution(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String toHtml() {
        return "<span style=\"font-weight: bold;\">" +
                this.name() +
                "</span>";
    }
}
