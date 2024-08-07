package uk.ac.core.workermetrics.data.state;

/**
 * Whether a repository is monitored (watched) or not
 *
 * @author lucasanastasiou
 */
public enum WatchedRepository {
    NOT_WATCHED(0),
    MONITORED_ONLY_FULL(1),
    MONITORED_EVERY_STATE(2);

    private int flag;

    WatchedRepository(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public static WatchedRepository getByFlag(int flag) {
        for (WatchedRepository wp : values()) {
            if (wp.getFlag() == flag) {
                return wp;
            }
        }
        return null;
    }
}
