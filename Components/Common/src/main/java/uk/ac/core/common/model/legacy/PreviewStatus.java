package uk.ac.core.common.model.legacy;

public enum PreviewStatus {
    UNPROCESSED(0), PROCESSED(1), ERROR(2);

    private final Integer mask;

    private PreviewStatus(Integer mask) {
        this.mask = mask;
    }

    public Integer getMask() {
        return mask;
    }
}
