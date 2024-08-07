package uk.ac.core.documentdownload.worker;

import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author mc26486
 */
public class LinksToPdfFilter {

    // List of strings which are usually within fulltext download links
    // MUST be entered lowercase
    // pdf
    // document - EPrints
    // bitstream - common for DSpace
    // fulltext - 
    // download - 
    // article - eprints  (http://centaur.reading.ac.uk/67663/1/article)
    // viewcontent - Digital Commons
    // objects - Fedora
    // /media/ - External hosting - Neleti https://www.neliti.com/publications/1045/rekrutmen-calon-anggota-legislatif-partai-demokrat-kabupaten-bolaang-mongondow-t
    // viewFile - OJS
    // catalog/view/ - Open Monograph Press
    // deliverymanager - exlibrisgroup
    // view/accepted - Specific for http://nparc.cisti-icist.nrc-cnrc.gc.ca/
    // datastream - http://unsworks.unsw.edu.au/fapi/datastream/unsworks:43071/SOURCE02?view=true
    // outputfile - WorkTribe
    private static final List<String> acceptedKeywords = Arrays.asList(
            "pdf",
            ".doc",
            ".docx",
            "document",
            "bitstream",
            "fulltext",
            "download",
            "/article",
            "viewcontent",
            "objects",
            "/media/",
            "viewfile",
            "catalog/view/",
            "deliverymanager",
            "view/accepted",
            "datastream",
            "outputfile"
    );

    public boolean canDownloadUrl(CrawlingUrl url) {
        String currentUrl = url.getCurrentUrl();
        for (String keyword : acceptedKeywords) {
            try {
                URL urlAsUrl = new URL(url.getCurrentUrl());
                currentUrl = urlAsUrl.getPath();
            } catch (MalformedURLException e) {
                // do nothing - use currentUrl as normal
            }
            if (currentUrl.toLowerCase().contains(keyword)) {
                System.out.println(currentUrl + " - matches" + keyword);
                return true;
            }
        }
        return false;
    }

}
