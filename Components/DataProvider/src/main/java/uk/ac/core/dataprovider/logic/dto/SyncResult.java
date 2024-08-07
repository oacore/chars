package uk.ac.core.dataprovider.logic.dto;

public class SyncResult {

    private long addedToIndex;
    private long removedFromIndex;

    public SyncResult() {
    }

    public SyncResult(long addedToIndex, long removedFromIndex) {
        this.addedToIndex = addedToIndex;
        this.removedFromIndex = removedFromIndex;
    }

    public long getAddedToIndex() {
        return addedToIndex;
    }

    public void setAddedToIndex(long addedToIndex) {
        this.addedToIndex = addedToIndex;
    }

    public long getRemovedFromIndex() {
        return removedFromIndex;
    }

    public void setRemovedFromIndex(long removedFromIndex) {
        this.removedFromIndex = removedFromIndex;
    }
}
