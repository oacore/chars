package uk.ac.core.documentdownload.downloader.crawling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.LoggerFactory;
import uk.ac.core.documentdownload.worker.DefaultDocumentDownloadWorker;

/**
 * Evaluates whether a URL can be downloaded accoring to TryOnlyPdf value
 *
 * @author samuel
 */
public class BlackListFilter {

    private String[] blacklist;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BlackListFilter.class);

    private static final String[] globalBlacklist = new String[]{
        ".zip?sequence=",
        ".xls?sequence=",
        ".xlsx?sequence=",
        ".wma?sequence=",
        ".3gp?sequence=",
        ".wav?sequence=",
    };

    /**
     *
     * @param blacklist
     */
    public BlackListFilter(String[] blacklist) {
        this.blacklist = (String[]) ArrayUtils.addAll(blacklist, globalBlacklist);
    }

    /**
     * Checks if the currentUrl can be downloaded. If a url does not contain any
     * disallowed strings in the blacklist, returns true
     *
     * @param currentUrl
     * @return true if a crawler should attempt download
     */
    public boolean canDownloadUrl(String currentUrl) {
        String urlToCheck = currentUrl.toLowerCase();

        for (String disallowedString : this.blacklist) {
            if (urlToCheck.contains(disallowedString.toLowerCase())) {
                logger.debug("Can not download file as url contains blacklisted item: " + disallowedString + " URL: " + currentUrl, this.getClass());
                return false;
            }
        }
        return true;
    }

    public String[] getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(String[] blacklist) {
        this.blacklist = blacklist;
    }

}
