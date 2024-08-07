package uk.ac.core.notifications.model;

import java.util.HashMap;
import java.util.Map;

public class HarvestingData extends BaseEmailData {
    // The following constants represent placeholders in the relevant email template
    private static final String AFFECTED_COUNT = "affected_count";
    private static final String ISSUE_TYPE = "issue_type";
    private static final String METADATA_COUNT = "metadata_cnt";
    private static final String FULLTEXT_COUNT = "fulltext_cnt";

    private int metadataCount;
    private int fulltextCount;
    private int typesCount;
    private int affectedRecordsCount;

    public HarvestingData(int repoId) {
        super(repoId);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(REPO_NAME, this.repoName);
        map.put(METADATA_COUNT, this.metadataCount);
        map.put(FULLTEXT_COUNT, this.fulltextCount);
        map.put(ISSUE_TYPE, this.typesCount);
        map.put(AFFECTED_COUNT, this.affectedRecordsCount);
        return map;
    }

    public int getRepoId() {
        return repoId;
    }

    public void setRepoId(int repoId) {
        this.repoId = repoId;
    }

    public int getMetadataCount() {
        return metadataCount;
    }

    public void setMetadataCount(int metadataCount) {
        this.metadataCount = metadataCount;
    }

    public int getFulltextCount() {
        return fulltextCount;
    }

    public void setFulltextCount(int fulltextCount) {
        this.fulltextCount = fulltextCount;
    }

    public int getTypesCount() {
        return typesCount;
    }

    public void setTypesCount(int typesCount) {
        this.typesCount = typesCount;
    }

    public int getAffectedRecordsCount() {
        return affectedRecordsCount;
    }

    public void setAffectedRecordsCount(int affectedRecordsCount) {
        this.affectedRecordsCount = affectedRecordsCount;
    }
}
