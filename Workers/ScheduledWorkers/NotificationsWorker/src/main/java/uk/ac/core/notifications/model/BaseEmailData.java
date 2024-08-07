package uk.ac.core.notifications.model;

import java.util.Map;

public abstract class BaseEmailData {
    protected static final String REPO_NAME = "repo_name"; // mutual placeholder for every email template

    protected int repoId;
    protected String repoName;

    public BaseEmailData(int repoId) {
        this.repoId = repoId;
    }

    public BaseEmailData setRepoName(String repoName) {
        this.repoName = repoName;
        return this;
    }

    public abstract Map<String, Object> toMap();
}
