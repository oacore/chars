package uk.ac.core.notifications.model;

import java.util.HashMap;
import java.util.Map;

public class DeduplicationData extends BaseEmailData {
    // The following constants represent placeholders in the relevant email template
    private static final String ACTION_COUNT = "action_cnt";
    private static final String DUPLICATES_COUNT = "duplicates_cnt";

    private int duplicatesCount;
    private int actionCount;

    public DeduplicationData(int repoId) {
        super(repoId);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(REPO_NAME, this.repoName);
        map.put(ACTION_COUNT, this.actionCount);
        map.put(DUPLICATES_COUNT, this.duplicatesCount);
        return map;
    }

    public int getRepoId() {
        return repoId;
    }

    public void setRepoId(int repoId) {
        this.repoId = repoId;
    }

    public int getDuplicatesCount() {
        return duplicatesCount;
    }

    public void setDuplicatesCount(int duplicatesCount) {
        this.duplicatesCount = duplicatesCount;
    }

    public int getActionCount() {
        return actionCount;
    }

    public void setActionCount(int actionCount) {
        this.actionCount = actionCount;
    }
}
