package uk.ac.core.documentdownload.downloader;

import crawlercommons.fetcher.AbortedFetchException;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrlBucket;
import uk.ac.core.documentdownload.downloader.fetcher.IllegalDomainException;
import uk.ac.core.documentdownload.downloader.fetcher.RequiresLoginException;
import uk.ac.core.documentdownload.worker.DocumentDownloadIssueException;

public interface ObtainsFile {

    /**
     * Prepares the environemnt for processing. May include checking robots.txt or implemneting a crawl delay
     *
     * @param documentId
     * @param crawlingUrl
     * @param crawlingUrlBucket
     * @return
     * @throws DocumentDownloadIssueException
     * @throws InterruptedException
     */
    boolean prepare(long documentId, String oai, CrawlingUrl crawlingUrl, CrawlingUrlBucket crawlingUrlBucket) throws DocumentDownloadIssueException, InterruptedException;

    /**
     * Obtains a file
     *
     * @param currentUrl a seed url (file/http any kind of identifier)
     * @return a download result.
     * @throws AbortedFetchException
     */
    DownloadResult obtainFile(String currentUrl, String filePath) throws AbortedFetchException, RequiresLoginException, IllegalDomainException;
}
