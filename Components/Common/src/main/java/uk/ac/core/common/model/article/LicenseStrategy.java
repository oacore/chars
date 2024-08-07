package uk.ac.core.common.model.article;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LicenseStrategy {
    ATTEMPT_EVERYTHING(0),
    RECOGNISE_AND_FOLLOW_LICENSE(1),
    SKIP_ALL(2);

    private static final Logger log = LoggerFactory.getLogger(LicenseStrategy.class);
    private final int dbFlag;

    LicenseStrategy(int dbFlag) {
        this.dbFlag = dbFlag;
    }

    public static LicenseStrategy fromDbFlag(int flag) {
        for (LicenseStrategy ls : values()) {
            if (ls.dbFlag == flag) {
                return ls;
            }
        }
        return null;
    }

    public boolean checkLicense(String docLicense) {
        boolean result;
        switch (this) {
            case ATTEMPT_EVERYTHING: {
                result = true;
                break;
            }
            case SKIP_ALL: {
                result = false;
                break;
            }
            case RECOGNISE_AND_FOLLOW_LICENSE: {
                License license = new License(docLicense);
                result = license.isOpenAccess();
                break;
            }
            default: {
                throw new IllegalStateException("Following license strategy is not supported: " + this.name());
            }
        }
        return result;
    }
}
