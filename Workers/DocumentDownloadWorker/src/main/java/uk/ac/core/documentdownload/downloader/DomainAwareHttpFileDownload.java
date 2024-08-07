package uk.ac.core.documentdownload.downloader;

import crawlercommons.fetcher.AbortedFetchException;
import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.HttpFetchException;
import crawlercommons.fetcher.http.UserAgent;
import org.apache.http.HttpHeaders;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.LoggerFactory;
import uk.ac.core.common.util.downloader.HttpFileDownloader;
import uk.ac.core.database.service.repositories.RepositoryDomainException;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingDelayMultiton;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrlBucket;
import uk.ac.core.documentdownload.downloader.fetcher.*;
import uk.ac.core.documentdownload.issues.DocumentDownloadIssueReporting;
import uk.ac.core.documentdownload.worker.DefaultDocumentDownloadWorker;
import uk.ac.core.documentdownload.worker.DocumentDownloadIssueException;
import uk.ac.core.issueDetection.util.IssueType;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uk.ac.core.common.util.datastructure.FluentHashMap.map;

/**
 * @author mc26486
 */
public class DomainAwareHttpFileDownload implements ObtainsFile {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DomainAwareHttpFileDownload.class);

    private static final int NUMBER_OF_FETCHER_THREADS = 10;
    private static final int NUMBER_OF_MAX_REDIRECTS = 4;

    private static final UserAgent CORE_UA = new UserAgent("CORE", "https://core.ac.uk/contact", "https://core.ac.uk");

    private static final Set<String> validMimeTypes = new HashSet<>(Arrays.asList(
            "text/html",
            "application/pdf",
            //doc
            "application/msword",
            //docx
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain",
            "application/octet-stream",
            ""));

    private static final int MAX_CONTENT_SIZE = 1073741824; // 1GB 

    private DomainAwareHttpFetcher ALL_FETCHER;


    private DomainAwareHttpFetcher fetcher;
    private boolean sameDomainPolicyEnforced;
    private List<RepositoryDomainException> domainExceptions;
    private String repositoryDomain;
    private List<String> additionalMimeTypesToAccept;
    private DocumentDownloadIssueReporting issueReporting;

    /**
     * Creates a HTTP fetcher
     * <p>
     * Default mimetypes to download are
     * application/pdf
     * application/octet-stream
     *
     * @param domainExceptions
     * @param repositoryDomain
     * @param additionalMimeTypesToAccept or emtpy for no additional mimeTypes
     */
    public DomainAwareHttpFileDownload(
            boolean sameDomainPolicyEnforced,
            List<RepositoryDomainException> domainExceptions,
            String repositoryDomain,
            List<String> additionalMimeTypesToAccept,
            DocumentDownloadIssueReporting issueReporting
    ) {
        this(new DomainAwareHttpFetcher(
                        NUMBER_OF_FETCHER_THREADS,
                        DomainAwareHttpFileDownload.CORE_UA,
                        NUMBER_OF_MAX_REDIRECTS)
                , sameDomainPolicyEnforced, domainExceptions, repositoryDomain, additionalMimeTypesToAccept, issueReporting);
    }

    public DomainAwareHttpFileDownload(
            DomainAwareHttpFetcher fetcher,
            boolean sameDomainPolicyEnforced,
            List<RepositoryDomainException> domainExceptions,
            String repositoryDomain,
            List<String> additionalMimeTypesToAccept,
            DocumentDownloadIssueReporting issueReporting) {
        this.sameDomainPolicyEnforced = sameDomainPolicyEnforced;
        this.domainExceptions = domainExceptions;
        this.repositoryDomain = repositoryDomain;
        this.additionalMimeTypesToAccept = additionalMimeTypesToAccept;
        this.fetcher = fetcher;
        this.issueReporting = issueReporting;

        this.fetcher.setValidMimeTypes(DomainAwareHttpFileDownload.validMimeTypes);
        this.fetcher.setDefaultMaxContentSize(MAX_CONTENT_SIZE);

        if (additionalMimeTypesToAccept.size() > 0) {
            additionalMimeTypesToAccept.addAll(DomainAwareHttpFileDownload.validMimeTypes);
            fetcher.setValidMimeTypes(new HashSet<>(additionalMimeTypesToAccept));
        } else {
            fetcher.setValidMimeTypes(DomainAwareHttpFileDownload.validMimeTypes);
        }
    }


    /**
     * prepares for download - checks custom crawl delay and robots.txt
     *
     * @param documentId
     * @param crawlingUrl
     * @param crawlingUrlBucket
     * @return
     * @throws DocumentDownloadIssueException
     * @throws InterruptedException
     */
    @Override
    public boolean prepare(long documentId, String oai, CrawlingUrl crawlingUrl, CrawlingUrlBucket crawlingUrlBucket) throws DocumentDownloadIssueException, InterruptedException {
        //TODO add the robots.txt checking and delay
        //int delay = CrawlingDelayer.getInstance().getDelay();
        if (crawlingUrl.getCurrentUrl().startsWith("file://")) {
            return Boolean.TRUE;
        }
        CrawlingDelayMultiton crawlingDelayMultiton;
        try {
            if (this.sameDomainPolicyEnforced) {
                crawlingDelayMultiton = CrawlingDelayMultiton.getInstanceFromUrl(domainExceptions, crawlingUrl.getCurrentUrl(), this.repositoryDomain);
            } else {
                crawlingDelayMultiton = CrawlingDelayMultiton.getInstanceFromUrl(crawlingUrl.getCurrentUrl(), null);

            }
            if (crawlingDelayMultiton == null) {
                logger.error(DefaultDocumentDownloadWorker.class
                        .getName() + "Same domain violated for domain: " + crawlingUrl.getCurrentUrl(), this.getClass());
                crawlingUrlBucket.markVisited(crawlingUrl.getCurrentUrl());
                return false;
            }

            if (!crawlingDelayMultiton.isAllowed(crawlingUrl.getCurrentUrl())) {
                String currentUrl = crawlingUrl.getCurrentUrl();
                this.issueReporting.reportIssue(
                        this.issueReporting.createIssue(documentId, IssueType.ROBOTS, "URL is denied access: " + crawlingUrl.getCurrentUrl(), map("url", currentUrl), oai));
                crawlingUrlBucket.markVisited(currentUrl);
                return false;
            }

        } catch (MalformedURLException ex) {
            this.issueReporting.reportIssue(this.issueReporting.createIssue(documentId, IssueType.ATTACHMENT_MALFORMED_URL,
                    IssueType.ATTACHMENT_MALFORMED_URL.getDescription(),
                    map("currentUrl", crawlingUrl.getCurrentUrl()).with("sourceUrl", crawlingUrl.getOriginalUrl()), oai));
            crawlingUrlBucket.markVisited(crawlingUrl.getCurrentUrl());
            logger.error("DocumentDownloadWorker: " + ex.getMessage(), ex);
            return false;
        }

        Long delay = crawlingDelayMultiton.getDelay();
        if (delay < 100) {
            delay = 100L;
        }
        logger.info(String.format("Delay: %d", delay), this.getClass());
        if (delay > 10000) {
            logger.error("Crawling delay too long");
            this.issueReporting.reportIssue(this.issueReporting.createIssue(documentId, IssueType.ROBOTS, "Crawling delay too long", Collections.emptyMap(), oai));
            throw new DocumentDownloadIssueException(IssueType.ROBOTS, IssueType.ROBOTS.getDescription());
        }
        Thread.sleep(delay);
        return true;
    }

    /**
     * Downloads file from given URL. Limits download to selected mime types,
     * returns either instance of DownloadResult which contains the downloaded
     * file as byte[], or null in case or error (in case of download exception).
     *
     * @param currentUrl
     * @return
     */
    @Override
    public DownloadResult obtainFile(String currentUrl, String filePath) throws AbortedFetchException, RequiresLoginException, IllegalDomainException {
        DownloadResult downloadResult = new DownloadResult();
        try {
            // See CORE-1760. Pls suggest a better way if possible
            try {
                URIBuilder uriBuilder = new URIBuilder(new URI(currentUrl).normalize());
            } catch (final URISyntaxException ex) {
                // URL encoding the whole String will also encode the /
                // We don't want this
                currentUrl = currentUrl
                        .replace(" ", "%20")
                        .replace(",", "%2C");
            }
            FetchedResult fetchedResult = this.fetcher.get(currentUrl, new DomainAwareRedirectStrategy(sameDomainPolicyEnforced, domainExceptions, repositoryDomain), filePath);
            downloadResult.setStatusCode(fetchedResult.getStatusCode());
            downloadResult.setContentFirstBytes(fetchedResult.getContent());
            downloadResult.setContentType(fetchedResult.getContentType());
            downloadResult.setBaseUrl(fetchedResult.getFetchedUrl());
            downloadResult.setHeaders(fetchedResult.getHeaders());
            downloadResult.setContentSize(Long.parseLong(fetchedResult.getHeaders().get(HttpHeaders.CONTENT_LENGTH)));
            return downloadResult;
        } catch (HttpFetchException ex) {
            downloadResult.setStatusCode(ex.getHttpStatus());
            logger.warn("Soft error: " + ex.getMessage(), ex);
        } catch (AbortedFetchException | RequiresLoginException | IllegalDomainException ex) {
            throw ex;
        } catch (BaseFetchException ex) {
            downloadResult.setStatusCode(0);
            logger.warn("Downloading exception (" + HttpFileDownloader.class.getName() + ") " + ex, HttpFileDownloader.class, ex);
        } catch (FileTooBigException ex) {
            downloadResult.setStatusCode(990);
            Logger.getLogger(HttpFileDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return downloadResult;
    }
}
