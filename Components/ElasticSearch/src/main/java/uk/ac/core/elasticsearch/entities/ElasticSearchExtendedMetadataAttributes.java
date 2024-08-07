package uk.ac.core.elasticsearch.entities;

import java.sql.Timestamp;

public class ElasticSearchExtendedMetadataAttributes {

    private Long attachmentCount;
    private Timestamp publicReleaseDate;

    public Long getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(Long attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public Timestamp getPublicReleaseDate() {
        return publicReleaseDate;
    }

    public void setPublicReleaseDate(Timestamp publicReleaseDate) {
        this.publicReleaseDate = publicReleaseDate;
    }
}
