package uk.ac.core.common.model.article;

/**
 * Deleted status states.
 */
public enum DeletedStatus {

    ALLOWED(0),
    DELETED(1),
    DISABLED(2);
    private final int value;

    DeletedStatus(int val) {
        this.value = val;
    }

    public int getValue() {
        return value;
    }

    /**
     * Get DeletedStatusState value from int
     *
     * @param val integer
     * @return DeletedStatusState
     * @throws RuntimeException
     */
    public static DeletedStatus fromInteger(Integer val) throws RuntimeException {

        for (DeletedStatus status : DeletedStatus.values()) {
            if (status.getValue() == val) {
                return status;
            }
        }
        /*
             * switch (val) { case 0: return DeletedStatus.ALLOWED; case 1:
             * return DeletedStatus.DELETED; case 2: return
             * DeletedStatus.DISABLED; case 3: return DeletedStatus.DISABLED;
             * default: throw new RuntimeException("Unknown value of deleted
             * status:" + val); }
         */
        throw new RuntimeException("Unknown value of deleted status:" + val);
    }
}
