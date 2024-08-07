package uk.ac.core.documentdownload.downloader;

import crawlercommons.fetcher.AbortedFetchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrlBucket;
import uk.ac.core.documentdownload.downloader.fetcher.IllegalDomainException;
import uk.ac.core.documentdownload.downloader.fetcher.RequiresLoginException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class ArXivFileGetter implements ObtainsFile {

    private final Logger logger = LoggerFactory.getLogger(ArXivFileGetter.class);
    private final int SLEEP_DELAY = 500;
    private final String PDF_MIME_TYPE = "application/pdf";
    private final String HTML_MIME_TYPE = "text/html";

    private ObtainsFile downloader;
    private Boolean useLocalData;

    private final String arXivPath = "/data/remote/arxiv/extracted/";

    /**
     * Gets ArXiv documents from disk or by modifying the ArXiv url efficiently
     * @param downloader the native strategy for downloading pdfs
     */
    public ArXivFileGetter(ObtainsFile downloader, boolean useLocalData) {
        this.downloader = downloader;
        this.useLocalData = useLocalData;
    }

    /**
     * prepare for download, usually processes robots etc, but for ArXiv, we know we must limit by 4 per second/sleep
     * for 1 second. We only really want to block upon a http download, so we move the delay to obtainFile
     * @param documentId
     * @param crawlingUrl
     * @param crawlingUrlBucket
     * @return
     */
    @Override
    public boolean prepare(long documentId,String oai, CrawlingUrl crawlingUrl, CrawlingUrlBucket crawlingUrlBucket) {
        return true;
    }

    /**
     * Obtains the ArXiv PDF either via the local filesystem or a download.
     *
     * Note:
     * if an http://arxiv.org url is provided, it is rewritten to export.arxiv.org
     * if an arxiv.org/abs link is provided, it is rewritten to arxiv.org/pdf
     *
     *
     * @param currentUrl The URL to attempt
     * @return
     * @throws AbortedFetchException
     */
    @Override
    public DownloadResult obtainFile(String currentUrl, String filePath) throws AbortedFetchException, IllegalDomainException, RequiresLoginException {

        logger.info("Using specific arxiv.org downloader/processor: {} ", currentUrl);

        if (currentUrl.contains("arxiv.org/")) {
            String arXivId = this.parseArXivId(currentUrl);

            File arXivFile = this.getArXivFileLocation(arXivId);

            if (this.useLocalData && arXivFile.exists()) {
                logger.info("Obtaining file via filesystem: {} , {}", currentUrl, arXivFile);
                DownloadResult result = new DownloadResult();
                result.setBaseUrl(currentUrl);
                try {
                    result.setContentFirstBytes(Files.readAllBytes(arXivFile.toPath()));
                    result.setContentType(PDF_MIME_TYPE);
                    result.setStatusCode(200);
                    return result;
                } catch (IOException e) {
                    result.setStatusCode(500);
                }
            }
        } else {
            // If the site is not arxiv, don't download from it.
            // Mock an html file and allow the upstream DefaultDocumentDownload to process the next item
            DownloadResult result = new DownloadResult();
            result.setContentFirstBytes("".getBytes());
            result.setContentType(HTML_MIME_TYPE);
            result.setStatusCode(200);
            return result;
        }

        // For efficeny, we know that what the final pdf url is for ArXiv, we'll use that
        // we also need to use the export.arxiv.org domain for automated harvesting
        logger.info("Obtaining file via http: {}", currentUrl);

        currentUrl = currentUrl.replace("abs", "pdf");

        logger.info("Rewriting url from arxiv.org to export.arxiv.org: {} ", currentUrl);
        currentUrl = currentUrl.replace("//arxiv.org/", "//export.arxiv.org/");

        logger.info("Obtaining file via download: {} ", currentUrl);

        try {
            logger.info("sleeping for {} ms", SLEEP_DELAY);
            Thread.sleep(SLEEP_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // call  upstream Document Downloader
        DownloadResult downloadResult = this.downloader.obtainFile(currentUrl, filePath);

        // Restore the original url
        String finalUrl = downloadResult.getBaseUrl().replace("//export.arxiv.org/", "//arxiv.org/");
        logger.info("Rewriting arxiv base url from export.arxiv.org to arxiv.org: {} ", finalUrl);
        downloadResult.setBaseUrl(finalUrl);
        // end fix

        return downloadResult;
    }

    public String parseArXivId(String url) {
        return url
                .replace("https", "http")
                .replace("http://arxiv.org/abs/", "")
                .replace("http://arxiv.org/pdf/", "")
                .replace(".pdf", "");
    }

    public Optional<String> parseArXivFolderDate(String arXivId) {
        String localArXivId = arXivId;
        if (arXivId.contains("/")) {
            localArXivId = arXivId.substring(arXivId.indexOf("/") + 1);
        }
        if (localArXivId.length() > 4) {
            return Optional.of(localArXivId.substring(0, 4));
        }
        return Optional.empty();
    }

    public File getArXivFileLocation(String arXivId) {

        String date = this.parseArXivFolderDate(arXivId).isPresent() ? this.parseArXivFolderDate(arXivId).get() : "";

        String filename = arXivId.replace("/", "");

        String path = new StringBuffer()
                .append(this.arXivPath)
                .append(date)
                .append("/")
                .append(filename)
                .append(".pdf")
                .toString();
        return new File(path);
    }
}
