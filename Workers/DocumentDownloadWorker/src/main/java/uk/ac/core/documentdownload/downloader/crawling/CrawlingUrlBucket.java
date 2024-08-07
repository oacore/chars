package uk.ac.core.documentdownload.downloader.crawling;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mc26486
 */
public class CrawlingUrlBucket {

    private HashSet<String> visited;
    private LinkedList<CrawlingUrl> bucket;

    public CrawlingUrlBucket() {
        this.visited = new HashSet<>();
        this.bucket = new LinkedList<>();
    }

    public boolean addToTop(CrawlingUrl crawlingUrl) {
        if (this.isVisited(crawlingUrl.getCurrentUrl())
                || this.isDuplicate(crawlingUrl.getCurrentUrl())) {
            return false;
        } else {
            this.bucket.addFirst(crawlingUrl);
            return true;
        }
    }

    public boolean addToTop(List<CrawlingUrl> crawlingUrls) {
        boolean result = true;
        for (CrawlingUrl url : crawlingUrls) {
            result &= this.addToTop(url);
        }
        return result;
    }

    public boolean addToBottom(CrawlingUrl crawlingUrl) {
        if (this.isVisited(crawlingUrl.getCurrentUrl())
                || this.isDuplicate(crawlingUrl.getCurrentUrl())) {
            return false;
        } else {
            this.bucket.addLast(crawlingUrl);
            return true;
        }
    }

    public boolean addToBottom(List<CrawlingUrl> crawlingUrls) {
        boolean result = true;
        for (CrawlingUrl url : crawlingUrls) {
            result &= this.addToBottom(url);
        }
        return result;
    }

    public CrawlingUrl pop() {
        if (this.isEmpty()) {
            return null;
        }

        return this.bucket.pop();
    }

    public void markVisited(String url) {
        this.visited.add(url);
    }

    public boolean isVisited(String url) {
        return this.visited.contains(url);
    }

    public boolean isDuplicate(String url) {
        for (CrawlingUrl crawlingUrl : this.bucket) {
            if (crawlingUrl.getCurrentUrl().equalsIgnoreCase(url)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return this.bucket.isEmpty();
    }

    @Override
    public String toString() {
        return "CrawlingUrlBucket{" + "visited=" + visited.size() + ", bucket=" + bucket.size() + '}';
    }

    public void clear() {
        this.bucket.clear();
    }



}
