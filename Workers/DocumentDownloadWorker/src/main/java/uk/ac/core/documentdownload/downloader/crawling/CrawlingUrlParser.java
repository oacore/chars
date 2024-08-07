package uk.ac.core.documentdownload.downloader.crawling;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.ac.core.common.model.article.PDFUrlSource;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mc26486
 */
public class CrawlingUrlParser {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CrawlingUrlParser.class);
    
    public static boolean isValidUrl(String url) {

        String lowerCase = url.toLowerCase();

        if (lowerCase.isEmpty()) {
            return false;
        }

        if ("null".equals(lowerCase)) {
            return false;
        }

        // only if it's not ".pdf" ending link check the suffix
        return lowerCase.endsWith(".pdf") ||
                (!lowerCase.endsWith(".ppt")
                && !lowerCase.endsWith(".pptx")
                && !lowerCase.endsWith(".txt")
                && !lowerCase.endsWith(".ps") //TODO: REALLY?? check if can be used as PDF
                && !lowerCase.endsWith(".jpg")
                && !lowerCase.endsWith(".png")
                && !lowerCase.endsWith(".gif")
                && !lowerCase.endsWith(".bmp")
                && !lowerCase.endsWith(".xml")
                && !lowerCase.endsWith(".gz")
                && !lowerCase.endsWith(".zip")
                && !lowerCase.endsWith(".m4v")
                && !lowerCase.endsWith(".mp3")
                && !lowerCase.endsWith(".mp4")
                && !lowerCase.endsWith(".flv")
                && !lowerCase.endsWith(".css")
                && !lowerCase.endsWith(".tif")
                && !lowerCase.endsWith(".jpeg")
                && !lowerCase.endsWith(".xls")
                && !lowerCase.endsWith(".sib")
                && !lowerCase.endsWith(".rtf")
                && !lowerCase.endsWith(".mov")
                && !lowerCase.endsWith(".wmv"));
    }

    public static List<CrawlingUrl> parseList(HashMap<String, PDFUrlSource> currentUrls, String originalUrl) {
        return CrawlingUrlParser.parseList(currentUrls, originalUrl, 0);
    }

    public static List<CrawlingUrl> parseList(HashMap<String, PDFUrlSource> currentUrls, String originalUrl, Integer currentHarvestLevel) {
        ArrayList<CrawlingUrl> filteredUrls = new ArrayList<>();
        currentUrls.forEach((url, source) -> {
            if (source.equals(PDFUrlSource.UNPAYWALL)){
                filteredUrls.add(new CrawlingUrl(originalUrl, url, currentHarvestLevel));
            }
            else if (CrawlingUrlParser.isValidUrl(url)) {
                filteredUrls.add(new CrawlingUrl(originalUrl, url, currentHarvestLevel));
            }
        });
        return filteredUrls;
    }

    /**
     * Returns a list of URL's from a String.
     *
     * @param page
     * @param originalUrl
     * @param currentUrl
     * @param currentHarvestLevel
     * @return
     */
    public static List<CrawlingUrl> parsePage(String page, String originalUrl, String currentUrl, Integer currentHarvestLevel) {
        String baseUrl = CrawlingUrlParser.convertToBaseUrl(currentUrl);
        Document document = Jsoup.parse(page, baseUrl);

        LinkedList<CrawlingUrl> filteredUrls = new LinkedList<>();

        filteredUrls.addAll(parseMetaTagsForUrls(document, originalUrl, currentUrl, currentHarvestLevel));

        filteredUrls.addAll(parseAnchorHREF(document, originalUrl, currentUrl, currentHarvestLevel));
        

        //FIXME: reversing the order of filtered urls, in this way the priority of pdf on top will be preserved
        Collections.reverse(filteredUrls);
        return filteredUrls;
    }

    private static final List<String> attachmentMetaTags = new ArrayList<>(Arrays.asList(
            "eprints.document_url",
            "citation_pdf_url"
    ));

    public static List<CrawlingUrl> parseMetaTagsForUrls(Document document, String originalUrl, String currentUrl, Integer currentHarvestLevel) {
        LinkedList<CrawlingUrl> filteredUrls = new LinkedList<>();

        for (String tagName : attachmentMetaTags) {
            Elements metalinks = document.select("meta[name='" + tagName + "']");
            String metaLink = metalinks.attr("content");
            if (metaLink != null && !metaLink.isEmpty()) {
                filteredUrls.add(new CrawlingUrl(originalUrl, metaLink, currentHarvestLevel + 1));
            }
        }
        return filteredUrls;
    }

    public static List<CrawlingUrl> parseAnchorHREF(Document document, String originalUrl, String currentUrl, Integer currentHarvestLevel) {
        LinkedList<CrawlingUrl> filteredUrls = new LinkedList<>();
        Elements linksOnPage = document.select("a[href]");
        for (Element link : linksOnPage) {
            String absoluteUrl = link.absUrl("href");
            if (CrawlingUrlParser.isValidUrl(absoluteUrl)) {
                // If the URL contains '.pdf', put this to the top of the list
                CrawlingUrl crawlingUrl = new CrawlingUrl(originalUrl, absoluteUrl, currentHarvestLevel + 1);
                filteredUrls.add(crawlingUrl);
            }
        }
        return filteredUrls;
    }

    private static String convertToBaseUrl(String currentUrl) {
        URL url;

        try {
            url = new URL(currentUrl);
            String base = url.getProtocol() + "://" + url.getHost() + ((url.getPort() != 80 && url.getPort() > 0) ? ":" + url.getPort() : "");
            return base;
        } catch (MalformedURLException ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }
}
