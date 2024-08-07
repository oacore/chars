package uk.ac.core.documentdownload.worker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.ac.core.documentdownload.downloader.DownloadResult;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrlParser;
import uk.ac.core.documentdownload.downloader.crawling.SignpostingUrl;

@Service
public class PageParserService {
    
    /**
     *
     * @author mc26486
     * @param downloadResult
     * @param crawlingUrl
     * @return 
     */
    public List<CrawlingUrl> getUrlsFromPage(DownloadResult downloadResult, CrawlingUrl crawlingUrl, String filePath) throws IOException {
        String downloadedDocument = new String(Files.readAllBytes(new File(filePath).toPath()));
        String originalUrl;
        // if original URL is empty, then the current URL is the one which we have from metadata
        // and therefore the ORIGINAL URL
        if (crawlingUrl.getOriginalUrl() == null || crawlingUrl.getOriginalUrl().isEmpty()) {
            originalUrl = crawlingUrl.getCurrentUrl();
        } else {
            originalUrl = crawlingUrl.getOriginalUrl();
        }

        // Does the downloaded page support Signposting?
        String[] values = downloadResult.getHeaders().getValues("Link");
        if (values.length != 0) {
            List<CrawlingUrl> signPostingUrls = extractSignPostingUrls(values, originalUrl, crawlingUrl.getCurrentHarvestLevel());
            if (!signPostingUrls.isEmpty()){
                return signPostingUrls;
            }
        }
        // Get list of urls from the HTML page
        List<CrawlingUrl> urlsFromPage = CrawlingUrlParser.parsePage(downloadedDocument, originalUrl, downloadResult.getBaseUrl(), crawlingUrl.getCurrentHarvestLevel());

        return urlsFromPage;
    }

    public List<CrawlingUrl> extractSignPostingUrls(String[] values, String originalUrl, Integer currentHarvestLevel) {
        List<String> valuesList = Arrays.asList(values);
        List<CrawlingUrl> rellist = valuesList.stream()
                .filter(x -> x.contains("rel=\"item\""))
                .map((String x) -> {
                    //TODO make the parsing inside the constructor
                    String[] signpostingsections = x.split(";");
                    SignpostingUrl s= new SignpostingUrl(signpostingsections[0].replace(">", "").replace("<", "").trim(), originalUrl, currentHarvestLevel+1);
                    s.setRelType(signpostingsections[1].replace("rel=","").replace("\"", "").trim());
                    if (signpostingsections.length>2){
                        s.setApplicationType(signpostingsections[2].replace("type=\"", "").replace("\"", "").trim());
                    }
                    return s;
                })
                .collect(Collectors.toList());

        return rellist;
    }

   
}
