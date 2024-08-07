package uk.ac.core.common.util.downloader;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.BaseFetcher;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mc26486
 */
public class HttpFileDownloader {

    private static final int NUMBER_OF_FETCHER_THREADS = 10;

    private static final UserAgent CORE_UA = new UserAgent("CORE", "http://core.ac.uk/contact", "http://core.ac.uk");

    private static final Set<String> validMimeTypes = new HashSet<>(Arrays.asList(
            "text/html",
            "application/pdf",
            "application/json",
            "text/plain",
            "text/xml",
            ""
            ));

    private static final Set<String> pdfOnlyMimeTypes = new HashSet<>(Arrays.asList(
            "application/pdf",
            ""));

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpFileDownloader.class);

    private static final int MAX_CONTENT_SIZE = 1073741824; // 1GB 

    private static SimpleHttpFetcher ALL_FETCHER;

    /**
     * Downloads file from given URL. Limits download to selected mime types,
     * returns either instance of DownloadResult which contains the downloaded
     * file as byte[], or null in case or error (in case of download exception).
     *
     * @param currentUrl
     * @return
     */
    public static DownloadResult downloadFileFromUrl(String currentUrl) {
        DownloadResult downloadResult = new DownloadResult();
        try {
            BaseFetcher fetcher = getFetcherInstance();

            FetchedResult fetchedResult = fetcher.get(currentUrl);

            downloadResult.setStatusCode(fetchedResult.getStatusCode());
            downloadResult.setContentType(fetchedResult.getContentType());
            downloadResult.setContent(fetchedResult.getContent());
            downloadResult.setBaseUrl(fetchedResult.getFetchedUrl());
            return downloadResult;
        } catch (BaseFetchException ex) {
            downloadResult.setStatusCode(0);
            logger.warn("Downloading exception (" + HttpFileDownloader.class.getName() + ") " + ex, HttpFileDownloader.class);
        }

        return downloadResult;
    }

    private static BaseFetcher getFetcherInstance() {
        BaseFetcher fetcher = null;

        if (ALL_FETCHER == null) {
            ALL_FETCHER = new SimpleHttpFetcher(NUMBER_OF_FETCHER_THREADS, HttpFileDownloader.CORE_UA);

            ALL_FETCHER.setValidMimeTypes(HttpFileDownloader.validMimeTypes);
        }
        ALL_FETCHER.setDefaultMaxContentSize(MAX_CONTENT_SIZE);

        fetcher = ALL_FETCHER;
        return fetcher;
    }
}
