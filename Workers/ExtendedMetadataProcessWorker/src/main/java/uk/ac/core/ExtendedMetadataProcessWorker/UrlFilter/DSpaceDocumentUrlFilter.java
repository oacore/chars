package uk.ac.core.ExtendedMetadataProcessWorker.UrlFilter;

import uk.ac.core.common.model.legacy.DocumentUrl;

import java.util.regex.Pattern;

public class DSpaceDocumentUrlFilter implements Filter {

    private Pattern urlMatcher;

    public DSpaceDocumentUrlFilter() {
        this(Pattern.compile("/[\\d.]+/[\\d.]+$"));
    }

    public DSpaceDocumentUrlFilter(Pattern urlMatcher) {
        this.urlMatcher = urlMatcher;
    }

    public boolean allow(DocumentUrl url) {
        return !url.getUrl().contains("doi.org/")
                &&
                (url.getUrl().contains("hdl.handle")
                        || urlMatcher.matcher(url.getUrl()).find());
    }
}
