package uk.ac.core.ExtendedMetadataProcessWorker.UrlFilter;

import uk.ac.core.common.model.legacy.DocumentUrl;

@FunctionalInterface
public interface Filter {
    /**
     * Evaluates if a DocumentUrl is valid for processing according to the implementation
     * @param url
     * @return
     */
    boolean allow(DocumentUrl url);
}
